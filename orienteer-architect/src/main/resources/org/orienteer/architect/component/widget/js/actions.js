
var addOClassAction = function (editor, cell, evt) {

    var action = function () {
        editor.graph.stopEditing(false);
        var pt = editor.graph.getPointForEvent(evt);
        var vertex = createOClassVertex(new OClass(DEFAULT_OCLASS_NAME), pt.x, pt.y);
        editor.graph.setSelectionCells(editor.graph.importCells([vertex], 0, 0, cell));
    };

    action();
};

var addOPropertyAction = function (editor, cell, evt) {

    var action = function () {
        var graph = editor.graph;
        graph.stopEditing(false);
        var pt = graph.getPointForEvent(evt);
        if (cell !== null) {
            var oClassCell = getParent(graph, cell);
            var property = new OProperty(oClassCell.value.name);
            var modal = new OPropertyEditModalWindow(property, app.editorId, true);
            modal.onDestroy = function (property, event) {
                if (event === this.OK) {
                    var vertex = createOPropertyVertex(property);
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

    var getParent = function (graph, cell) {
        if (cell.value instanceof OClass)
            return cell;
        return getParent(graph, graph.getModel().getParent(cell));
    };

    action();
};

var addExistsOClassesAction = function (editor, cell, evt) {
    var graph = editor.graph;
    var pt = graph.getPointForEvent(evt);

    var action = function (json) {
        var classes = toOClasses(json);
        if (classes.length > 0) {
            const START_X = pt.x;
            const START_Y = pt.y;
            var x = START_X;
            var counterX = 1;
            forEach(classes, function (oClass) {
                var classCell = addOClassToGraph(oClass, x, START_Y);
                connectOClass(classCell, getOClassSuperClassesCells(graph, classCell.value), connectToSuperClasses);
                connectOClass(classCell, getOClassSubClassesCells(graph, classCell.value), connectToSubClasses);
                x = counterX % 3 !== 0 ? x + OCLASS_WIDTH + 10 : START_X;
                counterX++;
            });
        }
    };

    var addOClassToGraph = function(oClass, x, y) {
        var classVertex = createOClassVertex(oClass, x, y);
        graph.getModel().beginUpdate();
        try {
            graph.addCell(classVertex, graph.getDefaultParent());
            var properties = oClass.properties;
            if (properties !== null && properties.length > 0) {
                forEach(properties, function (property) {
                    graph.addCell(createOPropertyVertex(property), classVertex);
                  });
            }
        } finally {
            graph.getModel().endUpdate();
        }
        return classVertex;
    };

    var connectOClass = function (classCell, cells, func) {
        if (cells.length > 0) {
            graph.getModel().beginUpdate();
            try {
                forEach(cells, function (cell) {
                   func(classCell, cell);
                });
            } finally {
                graph.getModel().endUpdate();
            }
        }
    };

    var connectToSuperClasses = function (classCell, superClassCell) {
        graph.insertEdge(graph.getDefaultParent(), null, '', classCell, superClassCell);
    };

    var connectToSubClasses = function (classCell, subClassCell) {
        graph.insertEdge(graph.getDefaultParent(), null, '', subClassCell, classCell);
    };

    app.requestOClasses(getOClassesAsJSON(editor.graph), action);
};

var editOPropertyAction = function (editor, cell, evt) {
    var action = function () {
        if (cell.value instanceof OProperty) {
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
};

var saveEditorConfigAction = function (editor) {
    app.saveEditorConfig(mxUtils.getXml(getEditorXmlNode(editor.graph)));
};

var applyEditorChangesAction = function (editor) {
    saveEditorConfigAction.apply(this, arguments);
    app.applyEditorChanges(getOClassesAsJSON(editor.graph));
};

//TODO: delete after development
var toJsonAction = function (editor) {
    console.log('OClasses in JSON: ' + getOClassesAsJSON(editor.graph));
};
