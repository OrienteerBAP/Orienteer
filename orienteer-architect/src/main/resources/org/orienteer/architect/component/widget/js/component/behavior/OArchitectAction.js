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
    TO_JSON_ACTION:              'toJsonAction',
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
        };
        modal.show(evt.getGraphX(), evt.getGraphY());
    },

    /**
     * Add new {@link OArchitectOProperty} to editor action
     */
    addOPropertyAction: function (editor, cell, evt) {

        var action = function () {
            var graph = editor.graph;
            graph.stopEditing(false);
            var classCell = cell != null ? OArchitectUtil.getClassByPropertyCell(cell) : null;
            if (classCell !== null) {
                var property = new OArchitectOProperty(classCell.value);
                var modal = new OPropertyEditModalWindow(property, app.editorId, onDestroy, true);
                modal.show(evt.getGraphX(), evt.getGraphY());

                function onDestroy(property, event) {
                    if (event === this.OK) {
                        graph.getModel().beginUpdate();
                        try {
                            classCell.value.saveState();
                            classCell.value.createProperty(property.name, property.type, property.cell);
                            classCell.value.updateValueInCell();
                        } finally {
                            graph.getModel().endUpdate();
                        }
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
        if (addOPropertyLinkEvent != null) {
            var graph = editor.graph;
            var source = addOPropertyLinkEvent.source;
            var target = addOPropertyLinkEvent.target;
            var targetClass = target.value;
            var property = new OArchitectOProperty(targetClass);
            var modal = new OPropertyEditModalWindow(property, app.editorId, onDestroy, true);
            modal.orientDbTypes = OArchitectOType.linkTypes;
            modal.show(addOPropertyLinkEvent.event.getGraphX(), addOPropertyLinkEvent.event.getGraphY());

            function onDestroy(property, event) {
                if (event === this.OK) {
                    graph.getModel().beginUpdate();
                    try {
                        targetClass.saveState();
                        var newProperty = targetClass.createProperty(property.name, property.type);
                        newProperty.setLinkedClass(source.value);
                        newProperty.saveState();
                        targetClass.updateValueInCell();
                        newProperty.updateValueInCell();
                    } finally {
                        graph.getModel().endUpdate();
                    }
                } else modal.showErrorFeedback(localizer.cannotCreateLink);
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
            graph.getModel().beginUpdate();
            OArchitectUtil.forEach(jsonClasses, function (jsonClass) {
                var oClass = new OArchitectOClass();
                oClass.cell = OArchitectUtil.createOClassVertex(oClass, x, START_Y);
                oClass.configFromDatabase(jsonClass);
                addOClassCell(oClass.cell);
                cells.push(oClass.cell);
                x = counterX % 3 !== 0 ? x + OArchitectConstants.OCLASS_WIDTH + 10 : START_X;
                counterX++;
            });
            if (cells.length > 1) applyLayout(cells);
            graph.getModel().endUpdate();
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
            var graph = editor.graph;
            graph.stopEditing(false);
            var modal = new OClassEditModalWindow(cell.value, app.editorId, null, false);
            modal.show(evt.getGraphX(), evt.getGraphY());
        };

        action();
    },

    /**
     * Edit {@link OArchitectOProperty} action
     */
    editOPropertyAction: function (editor, cell, evt) {
        var action = function () {
            var graph = editor.graph;
            graph.stopEditing(false);
            var modal = new OPropertyEditModalWindow(cell.value, app.editorId, null, false);
            modal.show(evt.getGraphX(), evt.getGraphY());
        };

        action();
    },

    /**
     * Delete cell from editor action
     */
    deleteCellAction: function (editor, cell) {
        var cellsForDelete = editor.graph.getSelectionCells();
        if (cellsForDelete == null || cellsForDelete.length === 0 && cell != null) {
            cellsForDelete = cell.value instanceof OArchitectOClass ? [cell] : [OArchitectUtil.getClassByPropertyCell(cell)];
        }
        cellsForDelete = getPreparedCells(cellsForDelete);
        removeCells(cellsForDelete);

        function removeCells(cells) {
            if (cells != null && cells.length > 0) {
                OArchitectUtil.deleteCells(cells);
            }
        }

        function getPreparedCells(cells) {
            var result = [];
            OArchitectUtil.forEach(cells, function (cell) {
                if (OArchitectUtil.isCellDeletable(cell)) {
                    if (cell.value instanceof OArchitectOClass) {
                        cell.value.removed = true;
                    }
                    result.push(cell);
                }
            });
            return result;
        }
    },

    /**
     * Delete {@link OArchitectOProperty} from editor action
     */
    deleteOPropertyAction: function (editor, cell) {
        if (cell != null && cell.value instanceof OArchitectOProperty) {
            var graph = editor.graph;
            graph.stopEditing(false);
            var oClassCell = OArchitectUtil.getClassByPropertyCell(cell);
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
    },

    /**
     * Save editor current state in database action.
     * Creates POST request to Wicket.
     */
    saveEditorConfigAction: function (editor) {
        app.saveEditorConfig(mxUtils.getXml(OArchitectUtil.getEditorXmlNode(editor.graph)));
    },

    /**
     * Apply editor changes action.
     * Creates POST request to Wicket
     */
    applyEditorChangesAction: function (editor) {
        OArchitectAction.saveEditorConfigAction.apply(this, arguments);
        app.applyEditorChanges(OArchitectUtil.getOClassesAsJSON(editor.graph));
    },

    //TODO: delete after development
    toJsonAction: function (editor) {
        console.warn('OClasses in JSON: ', OArchitectUtil.getOClassesAsJSON(editor.graph));
    },

    /**
     * Enable fullscreen mode
     */
    fullScreenModeAction: function (editor) {
        app.switchFullScreenMode();
    }
};
