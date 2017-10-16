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
    graph.isCellConnectable = this.createIsCellConnectable();
    graph.isValidConnection = this.createIsValidConnection();
    graph.isCellDisconnectable = this.createIsCellDisconnectable();
    graph.connectionHandler.cursor = 'pointer';
    graph.connectionHandler.connectImage = new mxImage(app.basePath + OArchitectConstants.CONNECTOR_IMG_PATH, OArchitectConstants.ICON_SIZE, OArchitectConstants.ICON_SIZE);
    graph.connectionHandler.factoryMethod = this.createConnectionHandlerFactoryMethod();
    graph.connectionHandler.createEdgeState = this.createConnectionHandlerCreateEdgeState();
    graph.connectionHandler.isConnectableCell = this.createIsCellConnectable();

    graph.connectionHandler.createLinkOnConnection = false;
    graph.connectionHandler.linkConnection = false;

    graph.connectionHandler.createEdge = this.createConnectionHandlerCreateEdge();
    graph.connectionHandler.createIcons = this.createConnectionHandlerCreateIcons();
    graph.connectionHandler.redrawIcons = this.createConnectionHandlerRedrawIcons();

    graph.connectionHandler.connect = this.createConnectionHandlerConnect();

    graph.connectionHandler.validateConnection = function (source, target) {
        if (source === target && this.createLinkOnConnection) return null;
        return mxConnectionHandler.prototype.validateConnection.apply(this, arguments);
    };

    graph.connectionHandler.mouseUp = function (sender, me) {
        if (me == null || me.state == null) this.createLinkOnConnection = false;
        mxConnectionHandler.prototype.mouseUp.apply(this, arguments);
    };

    this.configEvents();
};

GraphConnectionConfig.prototype.configEvents = function () {
    this.graph.addListener(mxEvent.CELLS_REMOVED, this.createCellRemovedBehavior());
    this.graph.addListener(mxEvent.CELL_CONNECTED, this.createCellConnectedBehavior());
};

GraphConnectionConfig.prototype.createIsCellConnectable = function () {
    return function (cell) {
        var connectable  = !(!app.canUpdate || cell == null);
        if (connectable) {
            connectable = cell.value instanceof OArchitectOClass || cell.value instanceof OArchitectOProperty && cell.value.canConnect();
        }
        return connectable;
    };
};

GraphConnectionConfig.prototype.createIsCellDisconnectable = function () {
    return function () {
        return false;
    };
};

GraphConnectionConfig.prototype.isCellDeletable = function () {
    return function (cell) {
        if (cell.edge && cell.source.value instanceof OArchitectOClass && cell.target.value instanceof OArchitectOClass)
            return !cell.source.value.existsInDb;
        return mxGraph.prototype.isCellDeletable.apply(this, arguments);
    };
};

GraphConnectionConfig.prototype.createIsValidConnection = function () {
    return function (source, target) {
        var sourceValue = source.value;
        var targetValue = target.value;
        var valid = false;
        if (sourceValue instanceof OArchitectOClass && targetValue instanceof OArchitectOClass) {
            valid = !sourceValue.containsSuperClass(targetValue) && !targetValue.containsSuperClass(sourceValue);
        } else if (sourceValue instanceof OArchitectOProperty && targetValue instanceof OArchitectOClass) {
            valid = sourceValue.canConnect();
        }
        return valid;
    };
};

GraphConnectionConfig.prototype.createConnectionHandlerFactoryMethod = function () {
    var config = this;
    return function (source, target) {
        var edge = new mxCell();
        edge.setValue(getEdgeValue(source, target));
        edge.setEdge(true);
        edge.setStyle(config.getConnectionStyle(edge.value, source, target));
        var geo = new mxGeometry();
        geo.relative = true;
        edge.setGeometry(geo);
        return edge;
    };

    function getEdgeValue(source, target) {
        var result = '';
        if (source != null && target != null) {
            var sourceValue = source.value;
            var targetValue = target.value;
            if (sourceValue instanceof OArchitectOClass && targetValue instanceof OArchitectOClass) {
                result = sourceValue.isSuperClassExistsInDb(targetValue) ? OArchitectConstants.SAVED_INHERITANCE : OArchitectConstants.UNSAVED_INHERITANCE;
            } else if (sourceValue instanceof OArchitectOProperty && targetValue instanceof OArchitectOClass) {
                result = sourceValue.isLinkExistsInDb(targetValue) ? OArchitectConstants.SAVED_LINK : OArchitectConstants.UNSAVED_LINK;
            } else if (sourceValue instanceof OArchitectOProperty && targetValue instanceof OArchitectOProperty) {
                result = sourceValue.isLinkExistsInDb(targetValue.ownerClass) ? OArchitectConstants.SAVED_LINK : OArchitectConstants.UNSAVED_LINK;
            }
        }
        return result;
    }
};

GraphConnectionConfig.prototype.createConnectionHandlerConnect = function () {
    return function (source, target, evt, dropTarget) {
        this.currentEvent = evt;
        if (this.createLinkOnConnection) this.createLinkOnConnection = source.value instanceof OArchitectOClass;
        mxConnectionHandler.prototype.connect.apply(this, arguments);
        this.graph.setSelectionCells([]);
    };
};

GraphConnectionConfig.prototype.createConnectionHandlerCreateEdgeState = function() {
    return function (me) {
        if (me.sourceState !== null) {
            var cell = me.sourceState.cell;
            var edge = cell !== null ? this.factoryMethod(cell) :
                this.graph.createEdge(null, null, null, null, null, 'edgeStyle=orthogonalEdgeStyle');
            return new mxCellState(this.graph.view, edge, this.graph.getCellStyle(edge));
        }
        return mxConnectionHandler.prototype.createEdge.apply(this, arguments);
    };
};

GraphConnectionConfig.prototype.createConnectionHandlerCreateIcons = function () {
    return function (state) {
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
};

GraphConnectionConfig.prototype.createConnectionHandlerRedrawIcons = function () {
    return function (icons, state) {
        if (icons != null && icons[0] != null && state != null) {
            var withLinkIcon = icons.length === 2;
            var pos = this.getIconPosition(icons[0], state);
            if (withLinkIcon) {
                initIcon(icons[0], pos.y, pos.x, OArchitectConstants.ICON_SIZE * 4);
                initIcon(icons[1], pos.y, pos.x, OArchitectConstants.ICON_SIZE);
            } else initIcon(icons[0], pos.y, pos.x, OArchitectConstants.ICON_SIZE);

        }

        function initIcon(icon, y, x, xStep) {
            icon.bounds.x = x + OArchitectConstants.OCLASS_WIDTH / 2 - xStep;
            icon.bounds.y = y;
            icon.redraw();
        }
    };
};

GraphConnectionConfig.prototype.createConnectionHandlerCreateEdge = function () {
    return function (value, source, target) {
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
};

GraphConnectionConfig.prototype.createCellConnectedBehavior = function () {
    return function (graph, eventObject) {
        if (!graph.connectionHandler.linkConnection) {
            var edge = eventObject.getProperty('edge');
            if (edge != null && app.editor.connectionAvailable()) {
                var source = edge.source;
                var target = edge.target;
                if (source != null && target != null) {
                    if (!app.editor.undoOrRedoRuns) {
                        graph.getModel().beginUpdate();
                        graph.getModel().execute(new OConnectionManageCommand(source, target, false));
                        graph.getModel().endUpdate();
                    }
                }
            }
        } else graph.connectionHandler.linkConnection = false;
    }
};

GraphConnectionConfig.prototype.createCellRemovedBehavior = function () {
    return function (graph, eventObject) {
        var cells = eventObject.properties.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];
            if (cell.edge && app.editor.connectionAvailable()) {
                if (!app.editor.undoOrRedoRuns) {
                    graph.getModel().beginUpdate();
                    graph.getModel().execute(new OConnectionManageCommand(cell.source, cell.target, true));
                    graph.getModel().endUpdate();
                }
            }
        }
    }
};

GraphConnectionConfig.prototype.getConnectionStyle = function (edgeValue, source, target) {
    var style = null;
    if (source.value instanceof OArchitectOClass) {
        style = this.getOClassConnectionStyle(edgeValue === OArchitectConstants.UNSAVED_INHERITANCE, source);
    } else if (source.value instanceof OArchitectOProperty && OArchitectOType.isLink(source.value.type)) {
        style = this.getOPropertyConnectionStyle(source, target);
    }
    return style;
};

GraphConnectionConfig.prototype.getOClassConnectionStyle = function (createUserInheritance, source) {
    var style = null;
    if (createUserInheritance) {
        style = OArchitectConstants.OCLASS_CONNECTION_STYLE;
    } else if (source.value instanceof OArchitectOClass) {
        style = source.value.existsInDb ? OArchitectConstants.OCLASS_EXISTS_CONNECTION_STYLE : OArchitectConstants.OCLASS_CONNECTION_STYLE;
    }
    return style;
};

GraphConnectionConfig.prototype.getOPropertyConnectionStyle = function (source, target) {
    var style;
    if (source.value.existsInDb && source.value.linkedClass !== null && source.value.linkedClass.existsInDb) {
        if (target.value instanceof OArchitectOClass) {
            style = source.value.isLinkExistsInDb(target.value) ? OArchitectConstants.OPROPERTY_EXISTS_CONNECTION_STYLE : OArchitectConstants.OPROPERTY_CONNECTION_STYLE;
        } else {
            style = source.value.isLinkExistsInDb(target.value.ownerClass) ? OArchitectConstants.OPROPERTY_EXISTS_INVERSE_CONNECTION_STYLE : OArchitectConstants.OPROPERTY_CONNECTION_STYLE;
        }
    } else if (target != null && target.value instanceof OArchitectOProperty) {
        style = OArchitectConstants.OPROPERTY_INVERSE_CONNECTION_STYLE;
    } else style = OArchitectConstants.OPROPERTY_CONNECTION_STYLE;
    return style;
};