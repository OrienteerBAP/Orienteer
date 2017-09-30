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

    this.unsaveCounter = 0;

    this.connectionLockCounter = 0;

    this.fullScreenEnable = true;

    this.undoOrRedoRuns = false;

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
    var editor = this;
    var addProperty = new OArchitectPopupMenuAction(localizer.addProperty, OArchitectConstants.FA_ALIGN_JUSTIFY, OArchitectActionNames.ADD_OPROPERTY_ACTION, true);
    var undo = new OArchitectPopupMenuAction(localizer.undo, OArchitectConstants.FA_UNDO, 'undo');
    var redo = new OArchitectPopupMenuAction(localizer.redo, OArchitectConstants.FA_REDO, 'redo');
    var deleteAction = new OArchitectPopupMenuAction(localizer.deleteAction, OArchitectConstants.FA_DELETE, OArchitectActionNames.DELETE_CELL_ACTION, true);
    addProperty.isValidCell = OArchitectUtil.isValidPropertyTarget;
    undo.isEnabled = function () {
        return editor.undoManager.canUndo();
    };
    redo.isEnabled = function () {
        return editor.undoManager.canRedo();
    };
    deleteAction.isValidCell = function (cell) {
        return OArchitectUtil.isCellDeletable(cell);
    };
    menu.addAction(undo);
    menu.addAction(redo);
    menu.addAction(new OArchitectPopupMenuAction(localizer.addClass, OArchitectConstants.FA_FILE_O, OArchitectActionNames.ADD_OCLASS_ACTION, false, true));
    menu.addAction(new OArchitectPopupMenuAction(localizer.addExistsClasses, OArchitectConstants.FA_DATABASE, OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION, false, true));
    menu.addAction(addProperty);
    menu.addAction(deleteAction);
};

/**
 * Install undo handler see {@link mxEditor#installUndoHandler}
 * @param graph
 */
OArchitectEditor.prototype.installUndoHandler = function (graph) {
    var editor = this;
    var listener = mxUtils.bind(this, function(sender, evt) {
        if (editor.isSaveActions()) {
            var edit = evt.getProperty('edit');
            var changesForSave = [];
            OArchitectUtil.forEach(edit.changes, function (change) {
                if (!(change instanceof mxStyleChange)) {
                    changesForSave.push(change);
                }
            });
            if (changesForSave.length > 0) {
                edit.changes = changesForSave;
                this.undoManager.undoableEditHappened(edit);
            }
        }
    });

    graph.getModel().addListener(mxEvent.UNDO, listener);
    graph.getView().addListener(mxEvent.UNDO, listener);
};

OArchitectEditor.prototype.connectionAvailable = function () {
    return this.connectionLockCounter === 0;
};

OArchitectEditor.prototype.disableConnection = function () {
    this.connectionLockCounter++;
};

OArchitectEditor.prototype.enableConnection = function () {
    this.connectionLockCounter--;
};

OArchitectEditor.prototype.clearCommandHistory = function () {
    this.undoManager.clear();
};

OArchitectEditor.prototype.beginUnsaveActions = function () {
    this.unsaveCounter++;
};

OArchitectEditor.prototype.endUnsaveActions = function () {
    if (this.unsaveCounter > 0) this.unsaveCounter--;
};

OArchitectEditor.prototype.isSaveActions = function () {
    return this.unsaveCounter === 0;
};

OArchitectEditor.prototype.undo = function () {
    if (app.canUpdate) {
        this.undoOrRedoRuns = true;
        mxEditor.prototype.undo.apply(this, arguments);
        this.undoOrRedoRuns = false;
    }
};

OArchitectEditor.prototype.redo = function () {
    if (app.canUpdate) {
        this.undoOrRedoRuns = true;
        mxEditor.prototype.redo.apply(this, arguments);
        this.undoOrRedoRuns = false;
    }
};

OArchitectEditor.prototype.configureDefaultActions = function () {
    this.addAction(OArchitectActionNames.EDIT_OCLASS_ACTION, OArchitectAction.editOClassAction);
    this.addAction(OArchitectActionNames.EDIT_OPROPERTY_ACTION, OArchitectAction.editOPropertyAction);
    this.addAction(OArchitectActionNames.DELETE_OPROPERTY_ACTION, OArchitectAction.deleteOPropertyAction);
    this.addAction(OArchitectActionNames.DELETE_CELL_ACTION, OArchitectAction.deleteCellAction);
    this.addAction(OArchitectActionNames.FULL_SCREEN_MODE, OArchitectAction.fullScreenModeAction);
    this.addAction(OArchitectActionNames.ADD_OPROPERTY_LINK_ACTION, OArchitectAction.addOPropertyLinkAction);
};

OArchitectEditor.prototype.clone = function() {
    return mxUtils.clone(this);
};
