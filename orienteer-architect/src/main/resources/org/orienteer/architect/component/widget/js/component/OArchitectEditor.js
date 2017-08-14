/**
 * Editor for 'orienteer-architect'. Extends from {@link mxEditor}
 * @param container element which contains editor
 * @constructor
 */
var OArchitectEditor = function(container) {
    mxEditor.apply(this, arguments);
    this.sidebar = null;
    this.toolbar = null;
    this.container = container;

    this.configureDefaultActions();
    this.configureGraph([new GraphConfig(this), new GraphConnectionConfig(this),
        new GraphStyleConfig(this)]);
    this.configureLayouts();
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
    var graph = this.graph;
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


OArchitectEditor.prototype.configureDefaultActions = function () {
    this.addAction(OArchitectActionNames.EDIT_OCLASS_ACTION, OArchitectAction.editOClassAction);
    this.addAction(OArchitectActionNames.EDIT_OPROPERTY_ACTION, OArchitectAction.editOPropertyAction);
    this.addAction(OArchitectActionNames.DELETE_OPROPERTY_ACTION, OArchitectAction.deleteOPropertyAction);
    this.addAction(OArchitectActionNames.DELETE_CELL_ACTION, OArchitectAction.deleteCellAction);
};

OArchitectEditor.prototype.clone = function() {
    return mxUtils.clone(this);
};


