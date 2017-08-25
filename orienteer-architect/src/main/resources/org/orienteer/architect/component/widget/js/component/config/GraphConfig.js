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

    this.graph.getModel().setValue = function (cell, value) {
        if (value instanceof OArchitectOClass || value instanceof OArchitectOProperty) {
            var change = new mxValueChange(this, cell, value);
            change.undo = function () {
                var nextState = this.value.toEditorConfigObject();
                this.value = this.previous;
                this.previous = this.model.valueForCellChanged(this.cell, this.previous);
                if (this.previous != null) this.previous.nextState = nextState;
            };
            change.redo = function () {
                this.previous = this.value;
                this.value = this.model.valueForCellChanged(this.cell, this.value, true);
            };
            this.execute(change);
            return value;
        }
        return mxGraphModel.prototype.setValue.apply(this, arguments);
    };
};

GraphConfig.prototype.valueForCellChanged = function (cell, value, redo) {
    var previous = null;
    if (redo) {
        previous = value instanceof OArchitectOClass || value instanceof OArchitectOProperty ? value.nextState : cell.value;
        if (previous != null) cell.value = previous;
    } else {
        previous = value instanceof OArchitectOClass || value instanceof OArchitectOProperty ? value.previousState : cell.value;
        cell.value = value;
    }
    if (previous == null) previous = value;
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

