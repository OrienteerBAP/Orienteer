/**
 * Static class which contains names of {@link OArchitectEditor} actions
 */
var OArchitectActionNames = {
    ADD_OCLASS_ACTION: 'addOClass',
    ADD_OPROPERTY_ACTION: 'addOProperty',
    ADD_EXISTS_OCLASSES_ACTION: 'addExistsOClasses',
    EDIT_OPROPERTY_ACTION: 'editOProperty',
    DELETE_OPROPERTY_ACTION: 'deleteOProperty',
    TO_JSON_ACTION: 'toJsonAction',
    SAVE_EDITOR_CONFIG_ACTION: 'saveEditorConfig',
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
        editor.graph.stopEditing(false);
        var pt = editor.graph.getPointForEvent(evt);
        var vertex = OArchitectUtil.createOClassVertex(new OArchitectOClass(OArchitectConstants.DEFAULT_OCLASS_NAME), pt.x, pt.y);
        editor.graph.setSelectionCells(editor.graph.importCells([vertex], 0, 0, cell));
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
                var oClassCell = OArchitectUtil.searchOClassCell(graph, cell);
                var property = new OArchitectOProperty(oClassCell.value.name);
                var modal = new OPropertyEditModalWindow(property, app.editorId, true);
                modal.onDestroy = function (property, event) {
                    if (event === this.OK) {
                        var vertex = OArchitectUtil.createOPropertyVertex(property);
                        vertex.geometry.x = getOPropertyX(pt.x, graph, oClassCell);
                        vertex.geometry.y = getOPropertyY(pt.y, graph, oClassCell);
                        graph.getModel().beginUpdate();
                        try {
                            graph.addCell(vertex, oClassCell);
                            oClassCell.value.addOProperty(property);
                        } finally {
                            graph.getModel().endUpdate();
                        }
                        graph.getSelectionCell(vertex);
                    }
                };
                modal.show(pt.x, pt.y);
            } else {
                var infoModal = new InfoModalWindow(localizer.addOPropertyError, app.editorId);
                infoModal.show(pt.x, pt.y);
            }
        };

        var getOPropertyX = function (mouseX, graph, cell) {
            return mouseX - graph.getView().getState(cell).x;
        };

        var getOPropertyY = function (mouseY, graph, cell) {
            return mouseY - graph.getView().getState(cell).y;
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
            var classes = OArchitectUtil.fromJsonToOClasses(json);
            if (classes.length > 0) {
                const START_X = pt.x;
                const START_Y = pt.y;
                var x = START_X;
                var counterX = 1;
                OArchitectUtil.forEach(classes, function (oClass) {
                    addOClassToGraph(oClass, x, START_Y);
                    x = counterX % 3 !== 0 ? x + OArchitectConstants.OCLASS_WIDTH + 10 : START_X;
                    counterX++;
                });
            }
        };

        var addOClassToGraph = function(oClass, x, y) {
            var classCell = OArchitectUtil.createOClassVertex(oClass, x, y);
            var superClassesCells = OArchitectUtil.getOClassSuperClassesCells(graph, oClass);
            var subClassesCells = OArchitectUtil.getOClassSubClassesCells(graph, oClass);
            graph.getModel().beginUpdate();
            try {
                graph.addCell(classCell, graph.getDefaultParent());
                var properties = oClass.properties;
                if (properties !== null && properties.length > 0) {
                    OArchitectUtil.forEach(properties, function (property) {
                        graph.addCell(OArchitectUtil.createOPropertyVertex(property), classCell);
                    });
                }
                connectOClass(classCell, superClassesCells, connectToSuperClasses);
                connectOClass(classCell, subClassesCells, connectToSubClasses);
            } finally {
                graph.getModel().endUpdate();
            }
            return classCell;
        };

        var connectOClass = function (classCell, cells, func) {
            if (cells.length > 0) {
                graph.getModel().beginUpdate();
                try {
                    OArchitectUtil.forEach(cells, function (cell) {
                        func(classCell, cell);
                    });
                } finally {
                    graph.getModel().endUpdate();
                }
            }
        };

        var connectToSuperClasses = function (classCell, superClassCell) {
            graph.connectionHandler.connect(classCell, superClassCell);
        };

        var connectToSubClasses = function (classCell, subClassCell) {
            graph.connectionHandler.connect(subClassCell, classCell);
        };

        app.requestExistsOClasses(OArchitectUtil.getOClassesAsJSON(editor.graph), action);
    },

    /**
     * Edit {@link OArchitectOProperty} action
     */
    editOPropertyAction: function (editor, cell, evt) {
        var action = function () {
            if (cell !== null && cell.value instanceof OArchitectOProperty) {
                var graph = editor.graph;
                graph.stopEditing(false);
                var modal = new OPropertyEditModalWindow(cell.value, app.editorId, false);
                var pt = graph.getPointForEvent(evt);
                modal.show(pt.x, pt.y);
                modal.onDestroy = function (property, event) {
                    if (event === this.OK) {
                        graph.getModel().beginUpdate();
                        try {
                            graph.cellLabelChanged(cell, property);
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
     * Delete {@link OArchitectOProperty} from editor action
     */
    deleteOPropertyAction: function (editor, cell) {
        if (cell !== null && cell.value instanceof OArchitectOProperty) {
            var graph = editor.graph;
            graph.stopEditing(false);
            var oClassCell = OArchitectUtil.searchOClassCell(graph, cell);
            var oClass = oClassCell.value;
            var property = cell.value;
            oClass.removeProperty(property);
            graph.getModel().beginUpdate();
            try {
                graph.removeCells([cell]);
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
        console.log('OClasses in JSON: ' + OArchitectUtil.getOClassesAsJSON(editor.graph));
    }
};
