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
    graph.isCellDisconnectable = this.isCellDisconnectable;
    graph.connectionHandler.cursor = 'pointer';
    graph.connectionHandler.connectImage = new mxImage(app.basePath + OArchitectConstants.CONNECTOR_IMG_PATH, OArchitectConstants.ICON_SIZE, OArchitectConstants.ICON_SIZE);
    graph.connectionHandler.factoryMethod = this.connectionHandlerFactoryMethod;
    graph.connectionHandler.createEdgeState = this.connectionHandlerCreateEdgeState;
    graph.connectionHandler.isConnectableCell = this.isCellConnectable;

    graph.connectionHandler.createLinkOnConnection = false;
    graph.connectionHandler.linkConnection = false;

    graph.connectionHandler.createEdge = this.connectionHandlerCreateEdge;
    graph.connectionHandler.createIcons = this.connectionHandlerCreateIcons;
    graph.connectionHandler.redrawIcons = this.connectionHandlerRedrawIcons;

    graph.connectionHandler.connect = this.connectionHandlerConnect;

    this.configEvents();
};

GraphConnectionConfig.prototype.configEvents = function () {
    this.graph.addListener(mxEvent.CELLS_REMOVED, this.createCellRemovedBehavior());
    this.graph.addListener(mxEvent.CELL_CONNECTED, this.createCellConnectedBehavior());
};

GraphConnectionConfig.prototype.isCellConnectable = function (cell) {
    if (!app.canUpdate)
        return false;
    if (cell == null)
        return false;
    return cell.value instanceof OArchitectOClass || cell.value instanceof OArchitectOProperty && cell.value.canConnect();
};

GraphConnectionConfig.prototype.isCellDisconnectable = function (cell) {
    if (!app.canUpdate)
        return false;
    if (cell.edge && cell.source.value instanceof OArchitectOProperty)
        return cell.source.value.canDisconnect();

    return this.isCellDeletable(cell);
};

GraphConnectionConfig.prototype.isCellDeletable = function (cell) {
    if (cell.edge && cell.source.value instanceof OArchitectOClass)
        return !cell.source.value.existsInDb;
    return mxGraph.prototype.isCellDeletable.apply(this, arguments);
};

GraphConnectionConfig.prototype.isValidConnection = function (source, target) {
    var sourceValue = source.value;
    var targetValue = target.value;
    var valid = false;
    if (sourceValue instanceof OArchitectOClass && targetValue instanceof OArchitectOClass) {
        valid = !sourceValue.containsSuperClass(targetValue) && !targetValue.containsSuperClass(sourceValue);
    } else if (sourceValue instanceof OArchitectOProperty && targetValue instanceof OArchitectOClass) {
        valid = sourceValue.canConnect() && sourceValue.ownerClass !== targetValue;
    }
    return valid;
};

GraphConnectionConfig.prototype.connectionHandlerFactoryMethod = function (source) {
    var getEdgeStyle = function (source) {
        var style = null;
        if (source.value instanceof OArchitectOClass) {
            style = source.value.existsInDb ? OArchitectConstants.OCLASS_EXISTS_CONNECTION_STYLE : OArchitectConstants.OCLASS_CONNECTION_STYLE;
        } else if (source.value instanceof OArchitectOProperty && OArchitectOType.isLink(source.value.type)) {
            if (source.value.ownerClass.existsInDb && source.value.linkedClass.existsInDb) {
                style = OArchitectConstants.OPROPERTY_EXISTS_CONNECTION_STYLE;
            } else
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

GraphConnectionConfig.prototype.connectionHandlerConnect = function (source, target, evt, dropTarget) {
    this.currentEvent = evt;
    if (this.createLinkOnConnection) this.createLinkOnConnection = source.value instanceof OArchitectOClass;
    mxConnectionHandler.prototype.connect.apply(this, arguments);
    this.graph.setSelectionCells([]);
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

GraphConnectionConfig.prototype.connectionHandlerCreateIcons = function (state) {
    if (state.cell.value.existsInDb) {
        return [];
    }

    var createLinkIcon = state.cell != null && state.cell.value instanceof OArchitectOClass ||
        state.cell.value instanceof OArchitectOProperty && state.cell.value.isLink();
    var icons = state.cell.value instanceof OArchitectOClass ?
        mxConnectionHandler.prototype.createIcons.apply(this, arguments) : [];

    if (createLinkIcon) {
        var image = new mxImage(app.basePath + OArchitectConstants.LINK_IMG_PATH, OArchitectConstants.ICON_SIZE, OArchitectConstants.ICON_SIZE);
        var bounds = new mxRectangle(0, 0, image.width, image.height);
        var icon = new mxImageShape(bounds, image.src, null, null, 0);
        icon.preserveImageAspect = false;
        icon.init(this.graph.getView().getOverlayPane());
        icon.node.style.cursor = mxConstants.CURSOR_CONNECT;

        var getState = mxUtils.bind(this, function () {
            return this.currentState != null ? this.currentState : state;
        });

        var mouseDown = mxUtils.bind(this, function (evt) {
            if (!mxEvent.isConsumed(evt)) {
                this.icon = icon;
                this.createLinkOnConnection = true;
                this.graph.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt, getState()));
            }
        });
        mxEvent.redirectMouseEvents(icon.node, this.graph, getState, mouseDown);
        icons.push(icon);
        this.redrawIcons(icons, state);
    }
    return icons;
};

GraphConnectionConfig.prototype.connectionHandlerRedrawIcons = function (icons, state) {
    if (icons != null && icons[0] != null && state != null) {
        var withLinkIcon = icons.length === 2;
        var pos = this.getIconPosition(icons[0], state);
        if (withLinkIcon) {
            initIcon(icons[0], pos.y, pos.x, OArchitectConstants.ICON_SIZE * 4);
            initIcon(icons[1], pos.y, pos.x, OArchitectConstants.ICON_SIZE * 2);
        } else initIcon(icons[0], pos.y, pos.x, OArchitectConstants.ICON_SIZE * 2);

    }

    function initIcon(icon, y, x, xStep) {
        icon.bounds.x = x + OArchitectConstants.OCLASS_WIDTH / 2 - xStep;
        icon.bounds.y = y;
        icon.redraw();
    }
};

GraphConnectionConfig.prototype.connectionHandlerCreateEdge = function (value, source, target) {
    var edge = null;
    if (!this.createLinkOnConnection) {
        edge = mxConnectionHandler.prototype.createEdge.apply(this, arguments);
    } else {
        this.createLinkOnConnection = false;
        this.linkConnection = true;
        var state = this.edgeState;
        var evt = new mxMouseEvent(this.currentEvent, state);
        this.graph.fireMouseEvent(mxEvent.MOUSE_UP, evt);
        app.editor.execute(OArchitectActionNames.ADD_OPROPERTY_LINK_ACTION, {
            "source": source,
            "target": target,
            "event": evt
        });
    }

    return edge;
};

GraphConnectionConfig.prototype.createCellConnectedBehavior = function () {
    return function (graph, eventObject) {
        if (!graph.connectionHandler.linkConnection) {
            var properties = eventObject.properties;
            if (properties.edge != null) {
                var source = properties.edge.source;
                var target = properties.edge.target;
                OArchitectConnector.connect(source, target);
            }
        } else graph.connectionHandler.linkConnection = false;
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