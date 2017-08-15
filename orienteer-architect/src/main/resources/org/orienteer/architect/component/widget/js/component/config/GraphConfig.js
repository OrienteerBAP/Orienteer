/**
 * Class for config graph
 * @param editor which contains graph for config
 * @constructor
 */
var GraphConfig = function (editor) {
    this.editor = editor;
    this.graph = editor.graph;
};

GraphConfig.prototype.config = function () {
    this.graph.setConnectable(true);
    this.graph.setCellsDisconnectable(true);
    this.graph.setCellsCloneable(false);
    this.graph.setSwimlaneNesting(false);
    this.graph.setDropEnabled(true);
    this.graph.setAllowDanglingEdges(false);
    this.graph.setPanning(true);
    this.graph.foldingEnabled = false;
    this.configureGraphBehavior();
    this.configureGraphLabels();
};


GraphConfig.prototype.configureGraphBehavior = function () {
    this.graph.isCellResizable = function (cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellMovable = function(cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellEditable = function (cell) {
        return false;
    };
    this.graph.isValidDropTarget = function(cell) {
        return this.isSwimlane(cell);
    };
    this.graph.isCellSelectable = function (cell) {
        return this.isSwimlane(cell) || this.getModel().isEdge(cell);
    };

    this.graph.convertValueToString = this.convertValueToString;
    this.graph.cellLabelChanged = this.cellLabelChanged;
};


GraphConfig.prototype.configureGraphLabels = function () {
    this.graph.setHtmlLabels(true);
    var editor = this.editor;
    this.graph.getLabel = function (cell) {
        var label = mxGraph.prototype.getLabel.apply(this, arguments);
        if (this.model.isVertex(cell)) {
            var max = parseInt(this.getCellGeometry(cell).width / 8);
            var container = null;
            if (cell.value instanceof OArchitectOClass) {
                container = new OClassContainer(cell.value, editor, cell);
            } else if (cell.value instanceof OArchitectOProperty) {
                container = new OPropertyContainer(cell.value, editor, cell);
            }
            if (container !== null) label = container.createElement(max);
        }
        return label;
    };
};

GraphConfig.prototype.convertValueToString = function (cell) {
    if (cell.value instanceof OArchitectOClass) {
        return cell.value.name;
    } else if (cell.value instanceof OArchitectOProperty) {
        return cell.value.name;
    } else return mxGraph.prototype.convertValueToString.apply(this, arguments);
};

GraphConfig.prototype.cellLabelChanged = function (cell, newValue) {
    var value = null;
    if (cell.value instanceof OArchitectOClass) {
        value = newValue;
        newValue = mxUtils.clone(cell.value);
        newValue.name = value;
    } else if (cell.value instanceof OArchitectOProperty) {
        value = newValue;
        newValue = mxUtils.clone(cell.value);
        newValue.name = value;
    }
    mxGraph.prototype.cellLabelChanged.apply(this, arguments);
};
