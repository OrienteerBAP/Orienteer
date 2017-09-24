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
        var graph = editor.graph;
        graph.stopEditing(false);
        var pt = graph.getPointForEvent(evt.evt);
        var modal = new OClassEditModalWindow(new OArchitectOClass(), app.editorId, onDestroy, true);

        function onDestroy(oClass, event) {
            if (event === this.OK) {
                var vertex = OArchitectUtil.createOClassVertex(oClass);
                oClass.setCell(vertex);
                vertex.geometry.x = pt.x;
                vertex.geometry.y = pt.y;
                graph.getModel().beginUpdate();
                try {
                    graph.addCell(vertex, graph.getDefaultParent());
                } finally {
                    graph.getModel().endUpdate();
                }
            }
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
                    var modal = new OPropertyEditModalWindow(property, app.editorId, onDestroy, true);
                    modal.show(evt.getGraphX(), evt.getGraphY());

                    function onDestroy(property, event) {
                        if (event === this.OK) {
                            graph.getModel().beginUpdate();
                            try {
                                console.warn('start transaction');
                                var command = new OPropertyCreateCommand(property, classCell.value);
                                graph.getModel().execute(command);
                                // classCell.value.saveState();
                                // var prop = classCell.value.createProperty(property.name, property.type, property.cell);
                                // prop.setInversePropertyEnable(property.inversePropertyEnable);
                                // prop.updateValueInCell();
                            } finally {
                                console.warn('end transaction');
                                graph.getModel().endUpdate();
                            }
                        }
                        OArchitectAction.unlockActionsForClass(classCell.value);
                    }
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
            modal.show(addOPropertyLinkEvent.event.getGraphX(), addOPropertyLinkEvent.event.getGraphY());

            function onDestroy(property, event) {
                if (event === this.OK) {
                    graph.getModel().beginUpdate();
                    try {
                        sourceClass.saveState();
                        var newProperty = sourceClass.createProperty(property.name, property.type);
                        newProperty.setLinkedClass(target.value);
                        newProperty.setInversePropertyEnable(property.inversePropertyEnable);
                        newProperty.setInverseProperty(property.inverseProperty);
                        // newProperty.saveState();
                        // sourceClass.updateValueInCell();
                        sourceClass.notifySubClassesAboutChangesInProperty(newProperty, false);
                        newProperty.updateValueInCell();
                    } finally {
                        graph.getModel().endUpdate();
                    }
                } else modal.showErrorFeedback(localizer.cannotCreateLink);
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
                console.warn('json class: ', jsonClass);
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
                var graph = editor.graph;
                graph.stopEditing(false);
                var modal = new OClassEditModalWindow(cell.value, app.editorId, function (oClass, event) {
                    if (event === modal.OK) {
                        graph.getModel().beginUpdate();
                        oClass.updateValueInCell();
                        graph.getModel().endUpdate();
                    }
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
                graph.getModel().beginUpdate();
                var modal = new OPropertyEditModalWindow(cell.value, app.editorId, function (property, event) {
                    if (event === modal.OK) {
                        console.warn('update level: ', editor.graph.getModel().updateLevel);
                        property.updateValueInCell();
                    }
                    graph.getModel().endUpdate();
                    OArchitectAction.unlockActionsForClass(cell.value.ownerClass);
                }, false);
                modal.show(evt.getGraphX(), evt.getGraphY());
            }
        };

        action();
    },

    /**
     * Delete cell from editor action
     */
    deleteCellAction: function (editor, cell) {
        var cellsForDelete = editor.graph.getSelectionCells();
        if (cellsForDelete == null || cellsForDelete.length === 0 && cell != null) {
            cellsForDelete = OArchitectUtil.isCellDeletable(cell) ? [cell] : [OArchitectUtil.getClassCellByPropertyCell(cell)];
        }
        cellsForDelete = getPreparedCells(cellsForDelete);
        removeCells(cellsForDelete);

        function getPreparedCells(cells) {
            var result = [];
            OArchitectUtil.forEach(cells, function (cell) {
                if (OArchitectUtil.isCellDeletable(cell) && canDelete(cell)) {
                    cell.value.removed = true;
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
            if (cells != null && cells.length > 0) {
                OArchitectUtil.deleteCells(cells);
            }
        }
    },

    /**
     * Delete {@link OArchitectOProperty} from editor action
     */
    deleteOPropertyAction: function (editor, cell) {
        if (cell != null && cell.value instanceof OArchitectOProperty) {
            if (OArchitectAction.isAvailableActionForClass(cell.value.ownerClass)) {
                var graph = editor.graph;
                graph.stopEditing(false);
                var oClassCell = OArchitectUtil.getClassCellByPropertyCell(cell);
                var oClass = oClassCell.value;
                var property = cell.value;
                graph.getModel().beginUpdate();
                try {
                    oClass.saveState();
                    oClass.removeProperty(property, false);
                    oClass.updateValueInCell();
                } finally {
                    graph.getModel().endUpdate();
                }
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
                    edge.value = '';
                });
            } else {
                msg = new OArchitectMessage(localizer.applyChangesError, true);
            }
            msg.show();
            app.editor.toolbar.elementExecuted = false;
        });
    },

    /**
     * Enable fullscreen mode
     */
    fullScreenModeAction: function () {
        app.switchFullScreenMode();
    },

    //TODO: remove this action in release
    toClassesAction: function () {
        console.warn('Classes: ', OArchitectUtil.getAllClassesInEditor());
    },

    //TODO: remove this action in release
    toJsonAction: function () {
        console.warn('To JSON: ', OArchitectUtil.getOClassesAsJSON(app.editor.graph));
    }
};
