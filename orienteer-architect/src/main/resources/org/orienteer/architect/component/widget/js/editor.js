
var SchemeEditor = function(container) {
    mxEditor.apply(this, arguments);
    this.sidebar = undefined;
    this.toolbar = undefined;
    this.container = container;

    this.configureGraph();
    this.configureDefaultActions();
    this.configureLayouts();
};

SchemeEditor.prototype = Object.create(mxEditor.prototype);
SchemeEditor.prototype.constructor = SchemeEditor;

SchemeEditor.prototype.createOClassStyle = function () {
    var style = {};
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_SWIMLANE;
    style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
    style[mxConstants.STYLE_GRADIENTCOLOR] = '#41B9F5';
    style[mxConstants.STYLE_FILLCOLOR] = '#8CCDF5';
    style[mxConstants.STYLE_SWIMLANE_FILLCOLOR] = '#ffffff';
    style[mxConstants.STYLE_STROKECOLOR] = '#1B78C8';
    style[mxConstants.STYLE_FONTCOLOR] = '#000000';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    style[mxConstants.STYLE_STARTSIZE] = '28';
    style[mxConstants.STYLE_VERTICAL_ALIGN] = 'middle';
    style[mxConstants.STYLE_FONTSIZE] = '12';
    style[mxConstants.STYLE_FONTSTYLE] = 1;
    return style;
};

SchemeEditor.prototype.createOPropertyStyle = function () {
    var style = {};
    // style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
    // style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    // style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
    // style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
    // style[mxConstants.STYLE_GRADIENTCOLOR] = '#41B9F5';
    // style[mxConstants.STYLE_FILLCOLOR] = '#8CCDF5';
    // // style[mxConstants.STYLE_SWIMLANE_FILLCOLOR] = '#ffffff';
    // style[mxConstants.STYLE_FONTCOLOR] = '#000000';

    // // style[mxConstants.STYLE_STARTSIZE] = '28';
    // style[mxConstants.STYLE_VERTICAL_ALIGN] = 'middle';
    // style[mxConstants.STYLE_FONTSIZE] = '12';
    // style[mxConstants.STYLE_FONTSTYLE] = 1;
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
    style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_FONTCOLOR] = '#000000';
    style[mxConstants.STYLE_FONTSIZE] = '11';
    style[mxConstants.STYLE_FONTSTYLE] = 0;
    style[mxConstants.STYLE_SPACING_LEFT] = '4';
    style[mxConstants.STYLE_STROKECOLOR] = '#1B78C8';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    return style;
};

SchemeEditor.prototype.createEdgeStyle = function () {
    var style = this.graph.stylesheet.getDefaultEdgeStyle();
    style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    // style[mxConstants.STYLE_ROUNDED] = true;
    // style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
    return style;
};

SchemeEditor.prototype.createVertexStyle = function () {
    var style = {};
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
    style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_FONTCOLOR] = '#000000';
    style[mxConstants.STYLE_FONTSIZE] = '11';
    style[mxConstants.STYLE_FONTSTYLE] = 0;
    style[mxConstants.STYLE_SPACING_LEFT] = '4';
    return style;
};

SchemeEditor.prototype.configureGraph = function () {
    mxConnectionHandler.prototype.connectImage = new mxImage(CONNECTOR_IMG_PATH, 16, 16);
    this.setGraphContainer(this.container);
    this.graph.setConnectable(true);
    this.graph.setMultigraph(false);
    this.graph.setCellsDisconnectable(false);
    this.graph.setCellsCloneable(false);
    this.graph.setSwimlaneNesting(false);
    this.graph.setDropEnabled(true);
    this.graph.setHtmlLabels(true);
    this.graph.setAllowDanglingEdges(false);
    this.graph.connectionHandler.factoryMethod = null;

    this.configureGraphStyle();
    this.configureGraphBehavior();
};

SchemeEditor.prototype.configureGraphBehavior = function () {

    this.graph.isCellResizable = function (cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellMovable = function(cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellEditable = function (cell) {
        return cell.value instanceof OClass;
    };
    this.graph.isValidDropTarget = function(cell) {
        return this.isSwimlane(cell);
    };

    this.graph.convertValueToString = this.createStringConverter();
    this.graph.cellLabelChanged = this.createCellLabelChangedBehavior();
    this.graph.addListener(mxEvent.CELL_CONNECTED, this.createCellConnectedBehavior());
    this.graph.addListener(mxEvent.CELLS_REMOVED, this.createCellRemovedBehavior());
};

SchemeEditor.prototype.configureLayouts = function () {
    this.layoutSwimlanes = true;
    this.createSwimlaneLayout = function () {
        var layout = new mxStackLayout(this.graph, false);
        layout.fill = true;
        layout.resizeParent = true;

        layout.isVertexMovable = function(cell) {
            return true;
        };

        return layout;
    };
};

SchemeEditor.prototype.createStringConverter = function () {
    var defaultBehavior = this.graph.convertValueToString;
    return function (cell) {
        if (cell.value instanceof OClass) {
            return cell.value.name;
        } else if (cell.value instanceof OProperty) {
            return cell.value.name;
        } else defaultBehavior.apply(this, arguments);
    };
};

SchemeEditor.prototype.createCellLabelChangedBehavior = function () {
    var defaultBehavior = this.graph.cellLabelChanged;
    return function (cell, newValue, size) {
        if (cell.value instanceof OClass) {
            var value = newValue;
            newValue = mxUtils.clone(cell.value);
            newValue.name = value;
        } else if (cell.value instanceof OProperty) {
            var value = newValue;
            newValue = mxUtils.clone(cell.value);
            newValue.name = value;
        }
        defaultBehavior.apply(this, arguments);
    };
};

SchemeEditor.prototype.createCellConnectedBehavior = function () {
    return function (graph, eventObject) {
        var properties = eventObject.properties;
        if (!properties.source && properties.edge.source.value instanceof OClass) {
            var sourceClass = properties.edge.source.value;
            var targetClass = properties.edge.target.value;
            sourceClass.addSuperClass(targetClass.name);
            console.log('source: ' + sourceClass);
            console.log('target: ' + targetClass);
        }
    }
};

SchemeEditor.prototype.createCellRemovedBehavior = function () {
    return function (graph, eventObject) {
        var cells = eventObject.properties.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];
            if (cell.value instanceof OClass && cell.edge) {
                var sourceClass  = cell.source.value;
                var targetClass  = cell.target.value;
                sourceClass.removeSuperClass(targetClass.name);
                console.log('source: ' + sourceClass);
                console.log('target: ' + targetClass);
            }
        }
    }
};

SchemeEditor.prototype.configureGraphStyle = function() {
    var graph = this.graph;
    var stylesheet = graph.getStylesheet();
    stylesheet.putDefaultVertexStyle(this.createVertexStyle(graph));
    stylesheet.putDefaultEdgeStyle(this.createEdgeStyle(graph));
    stylesheet.putCellStyle(OCLASS_EDITOR_STYLE, this.createOClassStyle(graph));
    stylesheet.putCellStyle(OPROPERTY_EDITOR_STYLE, this.createOPropertyStyle(graph));
};

SchemeEditor.prototype.configureDefaultActions = function () {
    this.addAction(ADD_OPROPERTY_ACTION, addOPropertyAction);
};

SchemeEditor.prototype.clone = function() {
    return mxUtils.clone(this);
};

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
        var oClassCell = getParent(graph, cell);
        var pt = graph.getPointForEvent(evt);
        var property = new OProperty(oClassCell.value.name);
        var modal = createOPropertyCreateModalWindow(property, app.editorId);
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
        vertex.setAttribute();
        return vertex;
    };

    var getParent = function (graph, cell) {
        if (cell.value instanceof OClass)
            return cell;
        return getParent(graph, graph.getModel().getParent(cell));
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