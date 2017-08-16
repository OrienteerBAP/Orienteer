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
    APPLY_EDITOR_CHANGES_ACTION: 'applyChanges'
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
        var pt = graph.getPointForEvent(evt);
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
        modal.show(pt.x, pt.y);

    },

    /**
     * Add new {@link OArchitectOProperty} to editor action
     */
    addOPropertyAction: function (editor, cell, evt) {

        var action = function () {
            var graph = editor.graph;
            graph.stopEditing(false);
            var pt = graph.getPointForEvent(evt);
            if (cell !== null) {
                var oClass = OArchitectUtil.getClassByPropertyCell(graph, cell).value;
                var property = new OArchitectOProperty(oClass);
                var modal = new OPropertyEditModalWindow(property, app.editorId, onDestroy, true);
                modal.show(pt.x, pt.y);

                function onDestroy(property, event) {
                    if (event === this.OK) {
                        oClass.createProperty(property.name, property.type, property.cell);
                    }
                }
            } else {
                var infoModal = new InfoModalWindow(localizer.addOPropertyError, app.editorId);
                infoModal.show(pt.x, pt.y);
            }
        };

        action();
    },

    /**
     * Add exists classes from database to editor action
     */
    addExistsOClassesAction: function (editor, cell, evt) {
        var graph = editor.graph;
        var pt = graph.getPointForEvent(evt);

        var action = function (json) {
            const START_X = pt.x;
            const START_Y = pt.y;
            var x = START_X;
            var counterX = 1;
            var jsonClasses = JSON.parse(json);
            OArchitectUtil.forEach(jsonClasses, function (jsonClass) {
                var oClass = new OArchitectOClass();
                oClass.cell = OArchitectUtil.createOClassVertex(oClass, x, START_Y);
                oClass.configFromDatabase(jsonClass);
                addOClassCell(oClass.cell);
                x = counterX % 3 !== 0 ? x + OArchitectConstants.OCLASS_WIDTH + 10 : START_X;
                counterX++;
            });
        };

        function addOClassCell(cell) {
            graph.getModel().beginUpdate();
            try {
                graph.addCell(cell, graph.getDefaultParent());
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
            if (cell !== null && cell.value instanceof OArchitectOClass) {
                var graph = editor.graph;
                graph.stopEditing(false);
                var pt = graph.getPointForEvent(evt);
                var modal = new OClassEditModalWindow(cell.value, app.editorId, onDestroy, false);
                modal.show(pt.x, pt.y);

                function onDestroy(oClass, event) {
                    if (event === this.OK) {
                        graph.getModel().beginUpdate();
                        try {
                            graph.getModel().setValue(cell, oClass);
                        } finally {
                            graph.getModel().endUpdate();
                        }
                    }
                }
            }
        };

        action();
    },

    /**
     * Edit {@link OArchitectOProperty} action
     */
    editOPropertyAction: function (editor, cell, evt) {
        var action = function () {
            if (cell !== null && cell.value instanceof OArchitectOProperty) {
                var graph = editor.graph;
                graph.stopEditing(false);
                var modal = new OPropertyEditModalWindow(cell.value, app.editorId, onDestroy, false);
                var pt = graph.getPointForEvent(evt);
                modal.show(pt.x, pt.y);

                function onDestroy(property, event) {
                    if (event === this.OK) {
                        property.notifySubClassesPropertiesAboutChanges();
                    }
                }
            }
        };

        action();
    },

    /**
     * Delete cell from editor action
     */
    deleteCellAction: function (editor, cell) {
        OArchitectUtil.deleteCells(editor.graph.getSelectionCells());
    },

    /**
     * Delete {@link OArchitectOProperty} from editor action
     */
    deleteOPropertyAction: function (editor, cell) {
        if (cell != null && cell.value instanceof OArchitectOProperty) {
            var graph = editor.graph;
            graph.stopEditing(false);
            var oClassCell = OArchitectUtil.getClassByPropertyCell(graph, cell);
            var oClass = oClassCell.value;
            var property = cell.value;
            oClass.removeProperty(property, false);
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
    }
};
