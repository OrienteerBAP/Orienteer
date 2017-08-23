/**
 * Editor for 'orienteer-architect'. Extends from {@link mxEditor}
 * @param container element which contains editor
 * @constructor
 */
var OArchitectEditor = function(container) {
    mxEditor.apply(this, arguments);
    this.sidebar = null;
    this.toolbar = null;
    this.outline = null;
    this.popupMenu = null;
    this.container = container;

    this.fullscreen = false;

    this.configureDefaultActions();
    this.configureGraph([new GraphConfig(this), new GraphConnectionConfig(this),
        new GraphStyleConfig(this)]);
    this.configureLayouts();
    this.configurePopupMenu();
};

OArchitectEditor.prototype = Object.create(mxEditor.prototype);
OArchitectEditor.prototype.constructor = OArchitectEditor;

OArchitectEditor.prototype.configureGraph = function (configs) {
    this.setGraphContainer(this.container);
    OArchitectUtil.forEach(configs, function (config) {
       config.config();
    });
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

OArchitectEditor.prototype.configurePopupMenu = function () {
    if (app.canUpdate) {
        mxEvent.disableContextMenu(this.container);
        this.popupHandler.enabled = false;
        var graph = this.graph;
        var menu = new OArchitectPopupMenu(this.container, this);
        var handler = new OArchitectPopupMenuHandler(menu, graph);
        this.addActionsToPopupMenu(menu);
        graph.addMouseListener(handler);
        graph.addListener(mxEvent.ESCAPE, function () {
            handler.destroy();
        });
    }
};

OArchitectEditor.prototype.addActionsToPopupMenu = function (menu) {
    var action = new OArchitectPopupMenuAction(localizer.addProperty, OArchitectConstants.FA_ALIGN_JUSTIFY, OArchitectActionNames.ADD_OPROPERTY_ACTION, true);
    action.isValidCell = OArchitectUtil.isValidPropertyTarget;
    menu.addAction(new OArchitectPopupMenuAction(localizer.undo, OArchitectConstants.FA_UNDO, 'undo'));
    menu.addAction(new OArchitectPopupMenuAction(localizer.redo, OArchitectConstants.FA_REDO, 'redo'));
    menu.addAction(new OArchitectPopupMenuAction(localizer.addClass, OArchitectConstants.FA_FILE_O, OArchitectActionNames.ADD_OCLASS_ACTION, false, true));
    menu.addAction(new OArchitectPopupMenuAction(localizer.addExistsClasses, OArchitectConstants.FA_DATABASE, OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION, false, true));
    menu.addAction(action);
    menu.addAction(new OArchitectPopupMenuAction(localizer.deleteAction, OArchitectConstants.FA_DELETE, OArchitectActionNames.DELETE_CELL_ACTION, true));
};

/**
 * Install undo handler see {@link mxEditor#installUndoHandler}
 * @param graph
 */
OArchitectEditor.prototype.installUndoHandler = function (graph) {
    this.installUndoSaver(graph);
    var undoHandler = function(sender, evt) {
        var changes = evt.getProperty('edit').changes;
        var cells = graph.getSelectionCellsForChanges(changes);
        OArchitectUtil.forEach(cells, function (cell) {
            if (cell.isVertex()) {
                if (cell.value instanceof OArchitectOClass) {
                    OArchitectOClassConfigurator.configOClassFromCell(cell.value, cell);
                    cell.value.removed = false;
                } else if (cell.value instanceof OArchitectOProperty) {
                    var property = cell.value;
                    var classCell = OArchitectUtil.getCellByClassName(cell.value.ownerClass);
                    if (classCell != null) property.configFromCell(classCell.value, cell);
                }
            } else if (cell.isEdge() && cell.source != null && cell.target != null) {
                var sourceCell = cell.source;
                var targetCell = cell.target;
                var sourceValue = sourceCell.value;
                var targetValue = targetCell.value;
                if (sourceValue != null && targetValue != null && targetValue instanceof OArchitectOClass) {
                    if (sourceValue instanceof OArchitectOClass) {
                        OArchitectUtil.manageEdgesBetweenCells(sourceCell, targetCell, sourceValue.superClasses.indexOf(targetValue) > -1);
                    } else if (sourceValue instanceof OArchitectOProperty) {
                        OArchitectUtil.manageEdgesBetweenCells(sourceCell, targetCell, sourceValue.linkedClass === targetValue);
                    }
                }
            }
        });
        graph.setSelectionCells(cells);
    };
    this.undoManager.removeListener(mxEvent.UNDO);
    this.undoManager.removeListener(mxEvent.REDO);
    this.undoManager.addListener(mxEvent.UNDO, undoHandler);
    this.undoManager.addListener(mxEvent.REDO, undoHandler);
};

OArchitectEditor.prototype.installUndoSaver = function (graph) {
    var listener = mxUtils.bind(this, function(sender, evt) {
        var edit = evt.getProperty('edit');
        var changesForSave = [];
        OArchitectUtil.forEach(edit.changes, function (change) {
            if (change instanceof mxValueChange) {
                if (change.previous !== null && change.value !== null) {
                    changesForSave.push(change);
                }
            } else changesForSave.push(change);
        });
        if (changesForSave.length > 0) {
            edit.changes = changesForSave;
            this.undoManager.undoableEditHappened(edit);
        }
    });

    graph.getModel().addListener(mxEvent.UNDO, listener);
    graph.getView().addListener(mxEvent.UNDO, listener);
};

OArchitectEditor.prototype.undo = function () {
    if (app.canUpdate) {
        mxEditor.prototype.undo.apply(this, arguments);
    }
};

OArchitectEditor.prototype.redo = function () {
    if (app.canUpdate) {
        mxEditor.prototype.redo.apply(this, arguments);
    }
};

OArchitectEditor.prototype.configureDefaultActions = function () {
    this.addAction(OArchitectActionNames.EDIT_OCLASS_ACTION, OArchitectAction.editOClassAction);
    this.addAction(OArchitectActionNames.EDIT_OPROPERTY_ACTION, OArchitectAction.editOPropertyAction);
    this.addAction(OArchitectActionNames.DELETE_OPROPERTY_ACTION, OArchitectAction.deleteOPropertyAction);
    this.addAction(OArchitectActionNames.DELETE_CELL_ACTION, OArchitectAction.deleteCellAction);
    this.addAction(OArchitectActionNames.FULL_SCREEN_MODE, OArchitectAction.fullScreenModeAction);
};

OArchitectEditor.prototype.clone = function() {
    return mxUtils.clone(this);
};

