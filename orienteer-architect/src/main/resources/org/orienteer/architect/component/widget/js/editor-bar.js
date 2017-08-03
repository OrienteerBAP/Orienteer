/**
 * Abstract bar for editor
 * @param editor
 * @param container
 * @constructor
 */
var AbstractBar = function(editor, container) {
    this.editor = editor;
    this.container = container;
    this.barElements = [];
};

AbstractBar.prototype.addItem = function (action, element) {
    this.barElements.push({
        action: action,
        element: element
    });
};

AbstractBar.prototype.addAction = function (label, actionName, actionFunction) {
    this.editor.addAction(actionName, actionFunction);
    this.addElementToContainer(label, actionName);
};

/**
 * Function for create element for {@link AbstractBar}
 */
AbstractBar.prototype.createElement = function () {
    throw new Error('No implementation for \'createElement\' function!');
};

AbstractBar.prototype.addElementToContainer = function () {
    throw new Error('No implementation for \'addElementToContainer\' function!');
};

/**
 *
 * @param editor
 * @param container
 * @constructor
 */
var Toolbar = function (editor, container) {
    AbstractBar.apply(this, arguments);
};

Toolbar.prototype = Object.create(AbstractBar.prototype);
Toolbar.prototype.constructor = Toolbar;

Toolbar.prototype.addElementToContainer = function (label, actionName) {
    this.addItem(this, label, actionName);
    var element = this.createElement(label, actionName);
    var editor = this.editor;
    this.container.appendChild(element);

    mxEvent.addListener(element, 'click', function (evt) {
        editor.execute(actionName);
    });
};

Toolbar.prototype.createElement = function (label, action) {
    var button = document.createElement('a');
    button.innerHTML = label;
    button.setAttribute('class', this.getCssClassByAction(action));
    button.style.margin = '5px';
    return button;
};

Toolbar.prototype.getCssClassByAction = function (action) {
    return 'btn btn-primary';
};

/**
 *
 * @param editor
 * @param container
 * @constructor
 */
var Sidebar = function (editor, container) {
    AbstractBar.apply(this, arguments);
};

Sidebar.prototype = Object.create(AbstractBar.prototype);
Sidebar.prototype.constructor = Sidebar;


Sidebar.prototype.addElementToContainer = function (label, actionName) {
    this.addItem(this, label, actionName);
    var editor = this.editor;
    var graph = editor.graph;
    var element = this.createElement(label, actionName);
    this.container.appendChild(element);

    mxUtils.makeDraggable(element, graph, function (graph, evt, cell) {
        editor.execute(actionName, cell, evt);
    });
};

Sidebar.prototype.createElement = function (label, action) {
    var a = document.createElement('a');
    a.classList.add(SIDEBAR_ITEM_CLASS);
    a.setAttribute('title', label);
    a.appendChild(this.getIconElementForAction(action));
    return a;
};

Sidebar.prototype.getIconElementForAction = function (action) {
    var icon = document.createElement('i');
    icon.setAttribute('class', FA_FILE_O);
    icon.classList.add(FA_2X);
    icon.style.margin = '5px';
    return icon;
};