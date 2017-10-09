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
    this.configEvents();
};


GraphConfig.prototype.configureGraphBehavior = function () {
    this.graph.graphHandler.setRemoveCellsFromParent(false);
    this.graph.isClass = function (cell) {
        return cell != null && cell.value instanceof OArchitectOClass;
    };
    this.graph.isValidPropertyTarget = function (cell) {
        return OArchitectUtil.isValidPropertyTarget(cell);
    };
    this.graph.isCellMovable = function() {
        return app.canUpdate;
    };
    this.graph.isCellEditable = function () {
        return false;
    };
    this.graph.isValidDropTarget = function(cell) {
        return this.isClass(cell);
    };
    this.graph.isCellSelectable = function () {
        return true;
    };
    this.graph.getTooltipForCell = function (cell) {
        var tooltip = null;
        if (cell !== null) {
            if (cell.value instanceof OArchitectOClass) {
                tooltip = localizer.classMsg + ': ' + cell.value.name;
            } else if (cell.value instanceof OArchitectOProperty) {
                tooltip = localizer.property + ': ' + cell.value.name;
            }
        }
        return tooltip;
    };
    this.graph.isCellResizable = function () {
        return false;
    };

    this.graph.edgeLabelsMovable = false;

    this.graph.convertValueToString = this.convertValueToString;
};

GraphConfig.prototype.valueForCellChanged = function (cell, value, redo) {
    if (value instanceof OArchitectOClass || value instanceof OArchitectOProperty) {
        cell.setValue(value);
        return value.previousState;
    }
    return mxGraphModel.prototype.valueForCellChanged.apply(this, arguments);
};

GraphConfig.prototype.configureGraphLabels = function () {
    this.graph.setHtmlLabels(true);
    var editor = this.editor;
    var config = this;
    this.graph.getLabel = function (cell) {
        var label = null;
        if (cell.isVertex()) {
            var max = parseInt(this.getCellGeometry(cell).width / 8);
            var container = null;
            if (cell.value instanceof OArchitectOClass) {
                container = new OClassContainer(cell.value, editor, cell);
            } else if (cell.value instanceof OArchitectOProperty) {
                container = new OPropertyContainer(cell.value, editor, cell);
            }
            if (container !== null) label = container.createElement(max);
        } else if (cell.isEdge()) {
            label = config.getLabelForEdge(cell);
        }
        return label;
    };
};

GraphConfig.prototype.getLabelForEdge = function (cell) {
    var label = null;
    if (cell.isEdge()) {
        var source = cell.source.value;
        var target = cell.target.value;
        var sourceGeo = source instanceof OArchitectOProperty ? this.graph.getCellGeometry(cell.source.parent) : null;
        var targetGeo = this.graph.getCellGeometry(target instanceof OArchitectOProperty ? cell.target.parent : cell.target);
        if (source instanceof OArchitectOProperty && target instanceof OArchitectOProperty) {
            if (OArchitectOType.isMultiValue(source.type) && OArchitectOType.isMultiValue(target.type)) {
                label = '*..*';
            } else if (OArchitectOType.isMultiValue(source.type) && !OArchitectOType.isMultiValue(target.type)) {
                label = isRight() ? '1..*' : '*..1';
            } else if (!OArchitectOType.isMultiValue(source.type) && OArchitectOType.isMultiValue(target.type)) {
                label = isRight() ? '*..1' : '1..*';
            } else if (!OArchitectOType.isMultiValue(source.type) && !OArchitectOType.isMultiValue(source.type)) {
                label = '1..1';
            }
        } else if (source instanceof OArchitectOProperty) {
            label = isRight() ? '*..1' : '1..*';
        }

        function isRight() {
            return sourceGeo.x < targetGeo.x;
        }
    }
    return label;
};

GraphConfig.prototype.convertValueToString = function (cell) {
    if (cell.value instanceof OArchitectOClass) {
        return cell.value.name;
    } else if (cell.value instanceof OArchitectOProperty) {
        return cell.value.name;
    } else return mxGraph.prototype.convertValueToString.apply(this, arguments);
};

GraphConfig.prototype.configEvents = function () {
    var graph = this.graph;
    graph.addListener(mxEvent.DOUBLE_CLICK, function (sender, evt) {
        var cell = evt.getProperty('cell');
        var event = evt.getProperty('event');
        event = new mxMouseEvent(event);
        graph.fireMouseEvent(mxEvent.DOUBLE_CLICK, event);
        if (cell != null) {
            var classCell = OArchitectUtil.getClassCellByPropertyCell(cell);
            if (classCell !== null && classCell.value instanceof OArchitectOClass) {
                app.editor.execute(OArchitectActionNames.ADD_OPROPERTY_ACTION, classCell, event);
            }
        } else {
            app.editor.execute(OArchitectActionNames.ADD_OCLASS_ACTION, null, event);
        }
    });
    graph.addListener(mxEvent.CELLS_MOVED, function (sender, evt) {
        var cells = evt.getProperty('cells');
        OArchitectUtil.forEach(cells, function (cell) {
            if (cell.value instanceof OArchitectOProperty) {
                graph.getModel().beginUpdate();
                setTimeout(function () {
                    var oClassCell = OArchitectUtil.getClassCellByPropertyCell(cell);
                    if (oClassCell !== null && oClassCell.value !== null) {
                        graph.getModel().execute(new OClassChangePropertyOrderCommand(oClassCell.value));
                        graph.getModel().endUpdate();
                    }
                }, 0);
            }
        });
    });
};