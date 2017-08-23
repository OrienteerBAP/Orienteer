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
    this.graph.isClass = function (cell) {
        return cell != null && cell.value instanceof OArchitectOClass;
    };
    this.graph.isValidPropertyTarget = function (cell) {
        return OArchitectUtil.isValidPropertyTarget(cell);
    };
    this.graph.isCellMovable = function(cell) {
        if (!app.canUpdate)
            return false;
        return this.isClass(cell);
    };
    this.graph.isCellEditable = function (cell) {
        return false;
    };
    this.graph.isValidDropTarget = function(cell) {
        return this.isClass(cell);
    };
    this.graph.isCellSelectable = function (cell) {
        if (cell == null)
            return false;
        if (!app.canUpdate)
            return false;
        if (cell.edge && cell.source != null && cell.source.value instanceof OArchitectOClass)
            return !cell.source.value.existsInDb;
        return this.isClass(cell) || this.getModel().isEdge(cell);
    };
    this.graph.getTooltipForCell = function (cell) {
        return null;
    };
    this.graph.isCellResizable = function (cell) {
        return false;
    };

    this.graph.convertValueToString = this.convertValueToString;
    this.graph.getModel().valueForCellChanged = this.valueForCellChanged;
};

GraphConfig.prototype.valueForCellChanged = function (cell, value) {
    var previous = cell.value instanceof OArchitectOClass || cell.value instanceof OArchitectOProperty ? cell.value.previousState : cell.value;
    cell.value = value;
    return previous;
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

