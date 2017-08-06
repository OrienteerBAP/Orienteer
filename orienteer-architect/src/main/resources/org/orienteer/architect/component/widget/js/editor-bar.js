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
    var element = this.createElement(label, actionName);
    this.container.appendChild(element);
    this.makeDraggable(element, actionName);
};

Sidebar.prototype.makeDraggable = function (element, actionName) {
    var editor = this.editor;
    var graph = editor.graph;
    var draggable = mxUtils.makeDraggable(element, graph, function (graph, evt, cell) {
        editor.execute(actionName, cell, evt);
    });
    draggable.getDropTarget = this.getDropTarget(actionName);
};

Sidebar.prototype.getDropTarget = function (actionName) {
    return function (graph, x, y) {
        if (actionName === actions.ADD_OCLASS_ACTION) {
            return null;
        } else if (actionName === actions.ADD_OPROPERTY_ACTION) {
            var cell = graph.getCellAt(x, y);
            if (graph.isSwimlane(cell))
                return cell;
            var parent = graph.getModel().getParent(cell);
            return graph.isSwimlane(parent) ? parent : null;
        } else if (actionName === actions.ADD_EXISTS_OCLASSES_ACTION) {
            var cell = graph.getCellAt(x, y);
            return cell === null;
        }
    };
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
    if (action === actions.ADD_OCLASS_ACTION) {
        icon.setAttribute('class', FA_FILE_O_CLASS);
    } else if (action === actions.ADD_OPROPERTY_ACTION) {
        icon.setAttribute('class', FA_ALIGN_JUSTIFY_CLASS);
    } else if (action === actions.ADD_EXISTS_OCLASSES_ACTION) {
        icon.setAttribute('class', FA_DATABASE_CLASS);
    }
    icon.classList.add(FA_2X_CLASS);
    icon.style.margin = '5px';
    icon.style.display = 'block';
    return icon;
};