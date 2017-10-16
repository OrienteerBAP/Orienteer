/**
 * Static class which contains names of {@link OArchitectEditor} actions
 */
var OArchitectActionNames = {
    ADD_OCLASS_ACTION:           'addOClass',
    ADD_OPROPERTY_ACTION:        'addOProperty',
    ADD_EXISTS_OCLASSES_ACTION:  'addExistsOClasses',
    EDIT_OCLASS_ACTION:          'editOClass',
    EDIT_OPROPERTY_ACTION:       'editOProperty',
    DELETE_OPROPERTY_ACTION:     'deleteOProperty',
    DELETE_CELL_ACTION:          'deleteCell',
    SAVE_EDITOR_CONFIG_ACTION:   'saveEditorConfig',
    APPLY_EDITOR_CHANGES_ACTION: 'applyChanges',
    FULL_SCREEN_MODE:            'fullScreenMode',
    ADD_OPROPERTY_LINK_ACTION:   'addOPropertyLinkAction'
};

/**
 * Static class which contains {@link OArchitectEditor} actions.
 * See {@link mxEditor#addAction} for more information.
 */
var OArchitectAction = {

    activeActionForClass: {},

    lockAddOClass: false,

    lockActionsForClass: function (oClass) {
        this.activeActionForClass[oClass.name] = 1;
    },

    unlockActionsForClass: function (oClass) {
        this.activeActionForClass[oClass.name] = undefined;
    },

    isAvailableActionForClass: function (oClass) {
        return this.activeActionForClass[oClass.name] === undefined;
    },

    /**
     * Add new {@link OArchitectOClass} to editor action
     */
    addOClassAction: function (editor, cell, evt) {
        if (!OArchitectAction.lockAddOClass) {
            OArchitectAction.lockAddOClass = true;
            var graph = editor.graph;
            graph.stopEditing(false);
            var pt = graph.getPointForEvent(evt.evt);
            graph.getModel().beginUpdate();
            var modal = new OClassEditModalWindow(new OArchitectOClass(), app.editorId, function (oClass, event) {
                if (event === this.OK) {
                    graph.getModel().execute(new OClassCreateCommand(oClass, pt.x, pt.y));
                }
                OArchitectAction.lockAddOClass = false;
                graph.getModel().endUpdate();
            }, true);
        }

        modal.show(evt.getGraphX(), evt.getGraphY());
    },

    /**
     * Add new {@link OArchitectOProperty} to editor action
     */
    addOPropertyAction: function (editor, cell, evt) {

        var action = function () {
            var graph = editor.graph;
            graph.stopEditing(false);
            var classCell = cell != null ? OArchitectUtil.getClassCellByPropertyCell(cell) : null;
            if (classCell !== null) {
                if (OArchitectAction.isAvailableActionForClass(classCell.value)) {
                    OArchitectAction.lockActionsForClass(classCell.value);
                    var property = new OArchitectOProperty(classCell.value);
                    var modal = new OPropertyEditModalWindow(property, app.editorId, function () {
                        OArchitectAction.unlockActionsForClass(classCell.value);
                    }, true);
                    modal.updateProperty = function (property, propertyWithChanges) {
                        var model = graph.getModel();
                        model.beginUpdate();
                        model.execute(new OPropertyCreateCommand(propertyWithChanges, property.ownerClass));
                        model.endUpdate();
                    };
                    modal.show(evt.getGraphX(), evt.getGraphY());
                }
            } else {
                var error = new OArchitectErrorModalWindow(localizer.addOPropertyError, app.editorId);
                error.show(evt.getGraphX(), evt.getGraphY());
            }

        };

        action();
    },

    addOPropertyLinkAction: function (editor, addOPropertyLinkEvent) {
        if (addOPropertyLinkEvent != null && OArchitectAction.isAvailableActionForClass(addOPropertyLinkEvent.source.value)) {
            OArchitectAction.lockActionsForClass(addOPropertyLinkEvent.source.value);
            OArchitectAction.lockActionsForClass(addOPropertyLinkEvent.target.value);
            var graph = editor.graph;
            var source = addOPropertyLinkEvent.source;
            var target = addOPropertyLinkEvent.target;
            var sourceClass = source.value;
            var property = new OArchitectOProperty(sourceClass);
            property.linkedClass = target.value;
            var modal = new OPropertyEditModalWindow(property, app.editorId, onDestroy, true);
            modal.orientDbTypes = OArchitectOType.linkTypes;
            modal.updateProperty = function (property, propertyWithChanges) {
                var model = graph.getModel();
                var createPropertyCommand = new OPropertyCreateCommand(propertyWithChanges, sourceClass);
                model.beginUpdate();
                model.execute(createPropertyCommand);
                model.execute(new OPropertyLinkChangeCommand(createPropertyCommand.property, target.value));
                model.execute(new OPropertyInverseChangeCommand(createPropertyCommand.property,
                    propertyWithChanges.inversePropertyEnable, propertyWithChanges.inverseProperty));
                model.endUpdate();
            };
            modal.show(addOPropertyLinkEvent.event.getGraphX(), addOPropertyLinkEvent.event.getGraphY());

            function onDestroy(property, event) {
                if (event !== this.OK) modal.showErrorFeedback(localizer.cannotCreateLink);
                OArchitectAction.unlockActionsForClass(sourceClass);
                OArchitectAction.unlockActionsForClass(target.value);
            }
        }
    },

    /**
     * Add exists classes from database to editor action
     */
    addExistsOClassesAction: function (editor, cell, evt) {
        var graph = editor.graph;
        var pt = graph.getPointForEvent(evt.evt);

        var action = function (json) {
            const START_X = pt.x;
            const START_Y = pt.y;
            var x = START_X;
            var counterX = 1;
            var jsonClasses = JSON.parse(json);
            var cells = [];
            var classes = [];
            graph.getModel().beginUpdate();
            OArchitectUtil.forEach(jsonClasses, function (jsonClass) {
                var oClass = new OArchitectOClass();
                oClass.configFromJson(jsonClass, OArchitectUtil.createOClassVertex(oClass, x, START_Y));
                oClass.setDatabaseJson(jsonClass);
                oClass.previousState = null;
                oClass.updateValueInCell(true, true);
                addOClassCell(oClass.cell);
                cells.push(oClass.cell);
                classes.push(oClass);
                x = counterX % 3 !== 0 ? x + OArchitectConstants.OCLASS_WIDTH + 10 : START_X;
                counterX++;
            });
            if (cells.length > 1) {
                applyLayout(cells);
                OArchitectUtil.updateAllCells();
            }
            graph.getModel().endUpdate();
            OArchitectUtil.updateExistsInDB(classes);
        };


        function addOClassCell(cell) {
            graph.getModel().beginUpdate();
            try {
                graph.addCell(cell, graph.getDefaultParent());
            } finally {
                graph.getModel().endUpdate();
            }
        }

        function applyLayout(cells) {
            graph.getModel().beginUpdate();
            try {
                var layout = new mxCircleLayout(graph, 20);
                layout.disableEdgeStyle = false;
                layout.isVertexIgnored = function (cell) {
                    if (cell.edge)
                        return mxPartitionLayout.prototype.isVertexIgnored.apply(this, arguments);
                    return cells.indexOf(cell) === -1;
                };
                layout.execute(graph.getDefaultParent());
            } finally {
                graph.getModel().endUpdate();
            }
        }

        app.requestExistsOClasses(OArchitectUtil.getOClassesAsJSON(editor.graph), action);
    },

    /**
     * Edit {@link OArchitectOClass} action
     */
    editOClassAction: function (editor, cell, evt) {
        var action = function () {
            if (OArchitectAction.isAvailableActionForClass(cell.value)) {
                OArchitectAction.lockActionsForClass(cell.value);
                var modal = new OClassEditModalWindow(cell.value, app.editorId, function () {
                    OArchitectAction.unlockActionsForClass(cell.value);
                }, false);
                modal.show(evt.getGraphX(), evt.getGraphY());
            }
        };

        action();
    },

    /**
     * Edit {@link OArchitectOProperty} action
     */
    editOPropertyAction: function (editor, cell, evt) {
        var action = function () {
            if (OArchitectAction.isAvailableActionForClass(cell.value.ownerClass)) {
                OArchitectAction.lockActionsForClass(cell.value.ownerClass);
                var graph = editor.graph;
                graph.stopEditing(false);

                var modal = new OPropertyEditModalWindow(cell.value, app.editorId, function () {
                    OArchitectAction.unlockActionsForClass(cell.value.ownerClass);
                }, false);

                modal.updateProperty = function (property, propertyWithChanges) {
                    var model = graph.getModel();
                    model.beginUpdate();
                    if (property.name !== propertyWithChanges.name || property.type !== propertyWithChanges.type) {
                        model.execute(new OPropertyNameAndTypeChangeCommand(property, propertyWithChanges.name, propertyWithChanges.type));
                    }
                    if (property.isLink()) {
                        model.execute(new OPropertyInverseChangeCommand(property,
                            propertyWithChanges.inversePropertyEnable, propertyWithChanges.inverseProperty));
                    }
                    model.endUpdate();
                };
                modal.show(evt.getGraphX(), evt.getGraphY());
            }
        };

        action();
    },

    /**
     * Delete cell from editor action
     */
    deleteCellAction: function (editor, cell) {
        var graph = editor.graph;
        var cellsForDelete = graph.getSelectionCells();
        if (cellsForDelete == null || cellsForDelete.length === 0 && cell != null) {
            cellsForDelete = OArchitectUtil.isCellDeletable(cell) ? [cell] : [OArchitectUtil.getClassCellByPropertyCell(cell)];
        }
        cellsForDelete = getPreparedCells(cellsForDelete);
        removeCells(cellsForDelete);

        function getPreparedCells(cells) {
            var result = [];
            OArchitectUtil.forEach(cells, function (cell) {
                if (OArchitectUtil.isCellDeletable(cell) && canDelete(cell)) {
                    result.push(cell);
                }
            });

            function canDelete(cell) {
                if (cell.isVertex()) {
                    return canDeleteClass(cell);
                } else {
                    return canDeleteClass(cell.source) && canDeleteClass(cell.target);
                }

                function canDeleteClass(cell) {
                    return OArchitectAction.isAvailableActionForClass(cell.value instanceof OArchitectOClass ?
                        cell.value : cell.value.ownerClass);
                }
            }

            return result;
        }

        function removeCells(cells) {
            var model = graph.getModel();
            model.beginUpdate();
            OArchitectUtil.forEach(cells, function (cell) {
                if (cell.isVertex()) {
                    if (cell.value instanceof OArchitectOClass) {
                        model.execute(new OClassRemoveCommand(cell.value));
                    } else if (cell.value instanceof OArchitectOProperty) {
                        model.execute(new OPropertyRemoveCommand(cell.value));
                    }
                } else {
                    model.execute(new OConnectionManageCommand(cell.source, cell.target, true));
                }
            });
            model.endUpdate();
        }
    },

    /**
     * Delete {@link OArchitectOProperty} from editor action
     */
    deleteOPropertyAction: function (editor, cell) {
        if (cell != null && cell.value instanceof OArchitectOProperty) {
            if (OArchitectAction.isAvailableActionForClass(cell.value.ownerClass)) {
                editor.graph.getModel().beginUpdate();
                editor.graph.getModel().execute(new OPropertyRemoveCommand(cell.value));
                editor.graph.getModel().endUpdate();
            }
        }
    },

    /**
     * Save editor current state in database action.
     * Creates POST request to Wicket.
     */
    saveEditorConfigAction: function (editor) {
        app.editor.toolbar.elementExecuted = true;
        app.saveEditorConfig(mxUtils.getXml(OArchitectUtil.getEditorXmlNode(editor.graph)), function (respond) {
            var msg;
            if (respond.save) {
                msg = new OArchitectMessage(localizer.saveDataModelSuccess);
            } else {
                msg = new OArchitectMessage(localizer.saveDataModelError, true);
            }
            msg.show();
            app.editor.toolbar.elementExecuted = false;
        });
    },

    /**
     * Apply editor changes action.
     * Creates POST request to Wicket
     */
    applyEditorChangesAction: function (editor) {
        app.editor.toolbar.elementExecuted = true;
        app.applyEditorChanges(OArchitectUtil.getOClassesAsJSON(editor.graph), function (respond) {
            var msg;
            if (respond.apply) {
                msg = new OArchitectMessage(localizer.applyChangesSuccess);
                OArchitectUtil.forEach(OArchitectUtil.getAllEdgesWithValue(OArchitectConstants.UNSAVED_INHERITANCE), function (edge) {
                    edge.setValue(OArchitectConstants.SAVED_INHERITANCE);
                });
                OArchitectUtil.forEach(OArchitectUtil.getAllEdgesWithValue(OArchitectConstants.UNSAVED_LINK), function (edge) {
                    edge.setValue(OArchitectConstants.SAVED_LINK);
                });
            } else {
                msg = new OArchitectMessage(localizer.applyChangesError, true);
            }
            msg.show();
            app.editor.toolbar.elementExecuted = false;
            editor.clearCommandHistory();
        });
    },

    /**
     * Enable fullscreen mode
     */
    fullScreenModeAction: function () {
        app.switchFullScreenMode();
    }
};