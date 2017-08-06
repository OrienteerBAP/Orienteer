
var OArchitectEditor = function(container) {
    mxEditor.apply(this, arguments);
    this.sidebar = null;
    this.toolbar = null;
    this.container = container;

    this.configureDefaultActions();
    this.configureGraph();
    this.configureLayouts();
};

OArchitectEditor.prototype = Object.create(mxEditor.prototype);
OArchitectEditor.prototype.constructor = OArchitectEditor;

OArchitectEditor.prototype.createOClassStyle = function () {
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

OArchitectEditor.prototype.createOPropertyStyle = function () {
    var style = {};
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

OArchitectEditor.prototype.createEdgeStyle = function () {
    var style = this.graph.stylesheet.getDefaultEdgeStyle();
    style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    return style;
};

OArchitectEditor.prototype.createVertexStyle = function () {
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

OArchitectEditor.prototype.configureGraph = function () {
    mxConnectionHandler.prototype.connectImage = new mxImage(app.basePath+CONNECTOR_IMG_PATH, 16, 16);
    this.setGraphContainer(this.container);
    this.graph.setConnectable(true);
    this.graph.setCellsDisconnectable(false);
    this.graph.setCellsCloneable(false);
    this.graph.setSwimlaneNesting(false);
    this.graph.setDropEnabled(true);
    this.graph.setAllowDanglingEdges(false);
    this.graph.connectionHandler.factoryMethod = null;
    this.graph.setPanning(true);
    this.configureGraphStyle();
    this.configureGraphBehavior();
    this.configureGraphLabels();
};

OArchitectEditor.prototype.configureGraphBehavior = function () {

    this.graph.isCellResizable = function (cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellMovable = function(cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellEditable = function (cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isValidDropTarget = function(cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellSelectable = function (cell) {
        return this.isSwimlane(cell);
    };

    this.graph.convertValueToString = this.createStringConverter();
    this.graph.cellLabelChanged = this.createCellLabelChangedBehavior();
    this.graph.addListener(mxEvent.CELL_CONNECTED, this.createCellConnectedBehavior());
    this.graph.addListener(mxEvent.CELLS_REMOVED, this.createCellRemovedBehavior());
};

OArchitectEditor.prototype.configureGraphLabels = function () {
    this.graph.setHtmlLabels(true);
    var editor = this;
    this.graph.getLabel = function (cell) {
        var label = mxGraph.prototype.getLabel.apply(this, arguments);
        if (this.model.isVertex(cell)) {
            var max = parseInt(this.getCellGeometry(cell).width / 8);
            var container = null;
            if (cell.value instanceof OClass) {
                container = new OArchitectDefaultContainer(cell.value.name, editor);
            } else if (cell.value instanceof OProperty) {
                container = new OPropertyContainer(cell.value, editor, cell);
            }
            if (container !== null) label = container.createElement(max);
        }
        return label;
    };
};

OArchitectEditor.prototype.configureLayouts = function () {
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

OArchitectEditor.prototype.createStringConverter = function () {
    var defaultBehavior = this.graph.convertValueToString;
    return function (cell) {
        if (cell.value instanceof OClass) {
            return cell.value.name;
        } else if (cell.value instanceof OProperty) {
            return cell.value.name;
        } else defaultBehavior.apply(this, arguments);
    };
};

OArchitectEditor.prototype.createCellLabelChangedBehavior = function () {
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

OArchitectEditor.prototype.createCellConnectedBehavior = function () {
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

OArchitectEditor.prototype.createCellRemovedBehavior = function () {
    return function (graph, eventObject) {
        var cells = eventObject.properties.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];
            if (cell.value instanceof OClass && cell.edge) {
                var sourceClass  = cell.source.value;
                var targetClass  = cell.target.value;
                sourceClass.deleteSuperClass(targetClass.name);
                console.log('source: ' + sourceClass);
                console.log('target: ' + targetClass);
            }
        }
    }
};

OArchitectEditor.prototype.configureGraphStyle = function() {
    var graph = this.graph;
    var stylesheet = graph.getStylesheet();
    stylesheet.putDefaultVertexStyle(this.createVertexStyle(graph));
    stylesheet.putDefaultEdgeStyle(this.createEdgeStyle(graph));
    stylesheet.putCellStyle(OCLASS_EDITOR_STYLE, this.createOClassStyle(graph));
    stylesheet.putCellStyle(OPROPERTY_EDITOR_STYLE, this.createOPropertyStyle(graph));
};

OArchitectEditor.prototype.configureDefaultActions = function () {
    this.addAction(actions.EDIT_OPROPERTY_ACTION, editOPropertyAction);
    this.addAction(actions.DELETE_OPROPERTY_ACTION, deleteOPropertyAction);
};

OArchitectEditor.prototype.clone = function() {
    return mxUtils.clone(this);
};
