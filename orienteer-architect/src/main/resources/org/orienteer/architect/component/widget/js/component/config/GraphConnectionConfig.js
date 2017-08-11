/**
 * Class for config graph connections
 * @param editor which contains graph for config
 * @constructor
 */
var GraphConnectionConfig = function (editor) {
    this.editor = editor;
    this.graph = editor.graph;
};

GraphConnectionConfig.prototype.config = function () {
    var graph = this.graph;
    graph.isCellConnectable = this.isCellConnectable;
    graph.isValidConnection = this.isValidConnection;
    graph.isCellLocked = this.isCellLocked;
    graph.getAllConnectionConstraints = this.getAllConnectionConstraints;
    graph.connectionHandler.cursor = 'pointer';
    graph.connectionHandler.factoryMethod = this.connectionHandlerFactoryMethod;
    graph.connectionHandler.createEdgeState = this.connectionHandlerCreateEdgeState;
    graph.connectionHandler.isConnectableCell = this.connectionHandlerIsConnectableCell;

    mxEdgeHandler.prototype.isConnectableCell = function(cell) {
        return false;
    };
    mxConstraintHandler.prototype.pointImage =  new mxImage(app.basePath + OArchitectConstants.CONNECTOR_IMG_PATH, 16, 16);

    this.configEvents();
};

GraphConnectionConfig.prototype.configEvents = function () {
    this.graph.addListener(mxEvent.CELL_CONNECTED, this.createCellConnectedBehavior());
    this.graph.addListener(mxEvent.CELLS_REMOVED, this.createCellRemovedBehavior());
};

GraphConnectionConfig.prototype.isCellConnectable = function (cell) {
    if (cell == null)
        return false;
    return cell.value instanceof OArchitectOClass || cell.value instanceof OArchitectOProperty && cell.value.canConnect();
};

GraphConnectionConfig.prototype.isCellLocked = function (cell) {
    var locked = false;
    if (cell.edge) {
        locked = cell.source.value instanceof OArchitectOProperty && !cell.source.value.canDisconnect();
    }
    return locked;
};

GraphConnectionConfig.prototype.isValidConnection = function (source, target) {
    var sourceValue = source.value;
    var targetValue = target.value;
    var valid = false;
    if (sourceValue instanceof OArchitectOClass && targetValue instanceof OArchitectOClass) {
        valid = !sourceValue.containsSuperClass(targetValue) && !targetValue.containsSuperClass(sourceValue);
    } else if (sourceValue instanceof OArchitectOProperty && targetValue instanceof OArchitectOClass) {
        valid = sourceValue.canConnect();
    }
    return valid && mxGraph.prototype.isValidConnection.apply(this, arguments);;
};

GraphConnectionConfig.prototype.getAllConnectionConstraints = function (terminal, source) {
    var constraints = null;
    var show = terminal !== null && this.model.isVertex(terminal.cell) && this.isCellConnectable(terminal.cell);
    show = show && !source ? terminal.cell.value instanceof OArchitectOClass : true;
    if (show) {
        var value = terminal.cell.value;
        if (value instanceof OArchitectOClass) {
            constraints = [new mxConnectionConstraint(new mxPoint(0.5, 0)),
                new mxConnectionConstraint(new mxPoint(1, 0.5)),
                new mxConnectionConstraint(new mxPoint(0.5, 1)),
                new mxConnectionConstraint(new mxPoint(0, 0.5))];
        } else if (value instanceof OArchitectOProperty && OArchitectOType.isLink(value.type)) {
            constraints = [new mxConnectionConstraint(new mxPoint(0, 0.5)),
                new mxConnectionConstraint(new mxPoint(1, 0.5))];
        }
    }

    return constraints;
};

GraphConnectionConfig.prototype.connectionHandlerFactoryMethod = function (source) {

    var getEdgeStyle = function (source) {
        var style = null;
        if (source.value instanceof OArchitectOClass) {
            style = OArchitectConstants.OCLASS_CONNECTION_STYLE;
        } else if (source.value instanceof OArchitectOProperty && OArchitectOType.isLink(source.value.type)) {
            style = OArchitectConstants.OPROPERTY_CONNECTION_STYLE;
        }
        return style;
    };

    var edge = new mxCell('');
    edge.setEdge(true);
    edge.setStyle(getEdgeStyle(source));
    var geo = new mxGeometry();
    geo.relative = true;
    edge.setGeometry(geo);

    return edge;
};

GraphConnectionConfig.prototype.connectionHandlerCreateEdgeState = function(me) {
    if (me.sourceState !== null) {
        var cell = me.sourceState.cell;
        var edge = cell !== null ? this.factoryMethod(cell) :
            this.graph.createEdge(null, null, null, null, null, 'edgeStyle=orthogonalEdgeStyle');
        return new mxCellState(this.graph.view, edge, this.graph.getCellStyle(edge));
    }
    return mxConnectionHandler.prototype.createEdge.apply(this, arguments);
};

GraphConnectionConfig.prototype.connectionHandlerIsConnectableCell = function (cell) {
    return false;
};

GraphConnectionConfig.prototype.createCellConnectedBehavior = function () {
    return function (graph, eventObject) {
        var properties = eventObject.properties;
        if (!properties.source) {
            OArchitectConnector.connect(properties.edge.source, properties.edge.target);
        }
    }
};

GraphConnectionConfig.prototype.createCellRemovedBehavior = function () {
    return function (graph, eventObject) {
        var cells = eventObject.properties.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];
            if (cell.edge) {
                OArchitectConnector.disconnect(cell.source, cell.target);
            }
        }
    }
};