
var addOClassAction = function (editor, cell, evt) {

    var action = function () {
        editor.graph.stopEditing(false);
        var pt = editor.graph.getPointForEvent(evt);
        var vertex = editor.graph.getModel().cloneCell(createVertex());
        vertex.geometry.x = pt.x;
        vertex.geometry.y = pt.y;
        editor.graph.setSelectionCells(editor.graph.importCells([vertex], 0, 0, cell));
    };

    var createVertex = function () {
        var vertex = new mxCell(getValue(), new mxGeometry(0, 0, getWidth(), getHeight()), getStyle());
        vertex.setVertex(true);
        return vertex;
    };

    var getValue = function () {
        return new OClass('MyClass');
    };

    var getStyle = function () {
        return OCLASS_EDITOR_STYLE;
    };

    var getWidth = function () {
        return OCLASS_WIDTH;
    };

    var getHeight = function () {
        return OCLASS_HEIGHT;
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
            var infoModal = createInfoModalWindow(OPROPERT_ADD_ERR, app.editorId);
            infoModal.show(pt.x, pt.y);
        }
    };

    var getOPropertyX = function (mouseX, graph, cell) {
        return mouseX - graph.getView().getState(cell).x;
    };

    var getOPropertyY = function (mouseY, graph, cell) {
        return mouseY - graph.getView().getState(cell).y;
    };

    var createOPropertyVertex = function (property) {
        var vertex = new mxCell(property,
            new mxGeometry(0, 0, 0, OPROPERTY_HEIGHT), OPROPERTY_EDITOR_STYLE);
        vertex.setVertex(true);
        vertex.setConnectable(false);
        return vertex;
    };

    var getParent = function (graph, cell) {
        if (cell.value instanceof OClass)
            return cell;
        return getParent(graph, graph.getModel().getParent(cell));
    };

    action();
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
    app.saveEditorConfig(mxUtils.getXml(getEditorXmlNode(editor)));
};

var applyEditorChangesAction = function (editor) {
    const OCLASS_TAG  = 'OClass';
    const PARENT_ATTR = 'parent';
    var node = getEditorXmlNode(editor);
    var classesXml = node.getElementsByTagName(OCLASS_TAG);
    var withParents = [];
    var withoutParents = [];
    var codec = new mxCodec();

    forEach(classesXml, function (classNode) {
        var child = false;
        if (classNode.getAttribute(PARENT_ATTR)) {
            child = true;
        }
        var oClass = codec.decode(classNode);
        if (child) withParents.push(JSON.stringify(oClass));
        else withoutParents.push(JSON.stringify(oClass));
    });

    saveEditorConfigAction.apply(this, arguments);
    app.applyEditorChanges('[' + withoutParents.concat(withParents).join(',') + ']');

    function forEach(arr, func) {
        for (var i = 0; i < arr.length; i++) {
            func(arr[i]);
        }
    }
};

var getEditorXmlNode = function (editor) {
    var encoder = new mxCodec();
    return encoder.encode(editor.graph.getModel());
};

//TODO: delete after development
var toJsonAction = function (editor) {
    const OCLASS_TAG  = 'OClass';
    const PARENT_ATTR = 'parent';
    var node = getEditorXmlNode(editor);
    var classesXml = node.getElementsByTagName(OCLASS_TAG);
    var withParents = [];
    var withoutParents = [];
    var codec = new mxCodec();

    forEach(classesXml, function (classNode) {
        var child = false;
        if (classNode.getAttribute(PARENT_ATTR)) {
            child = true;
        }
        var oClass = codec.decode(classNode);
        if (child) withParents.push(JSON.stringify(oClass));
        else withoutParents.push(JSON.stringify(oClass));
    });

    console.log('JSON: ' + '[' + withoutParents.concat(withParents).join(',') + ']');

    function forEach(arr, func) {
        for (var i = 0; i < arr.length; i++) {
            func(arr[i]);
        }
    }
};