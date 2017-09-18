/**
 * Bar for OArchitectEditor
 * @param editor editor which need sidebar
 * @param container container (html element) for bar
 * @constructor
 */
var OArchitectBar = function(editor, container) {
    this.editor = editor;
    this.container = container;
    this.barElements = [];
};

/**
 * Add item to bar
 * @param action - action which will execute when user do something with item
 * @param element - element which contains bar item
 */
OArchitectBar.prototype.addItem = function (action, element) {
    this.barElements.push({
        action: action,
        element: element
    });
};

/**
 * Add action to bar and add action to editor. Create new element for action
 * @param label label for action
 * @param actionName action name for editor and creating element
 * @param actionFunction function which will be execute on action
 */
OArchitectBar.prototype.addAction = function (label, actionName, actionFunction) {
    this.editor.addAction(actionName, actionFunction);
    this.addElementToContainer(label, actionName);
};

/**
 * Function for create element for {@link OArchitectBar}. Must be implemented by sub classes.
 */
OArchitectBar.prototype.createElement = function () {
    throw new Error('No implementation for \'createElement\' function!');
};

/**
 * Function for add element to container. Must be implemented by sub classes.
 */
OArchitectBar.prototype.addElementToContainer = function () {
    throw new Error('No implementation for \'addElementToContainer\' function!');
};

/**
 * Toolbar for {@link OArchitectEditor}. Extends from {@link OArchitectBar}
 * @param editor editor which need sidebar
 * @param container container (html element) for sidebar
 * @constructor
 */
var OArchitectToolbar = function (editor, container) {
    OArchitectBar.apply(this, arguments);
    this.elementExecuted = false;
};

OArchitectToolbar.prototype = Object.create(OArchitectBar.prototype);
OArchitectToolbar.prototype.constructor = OArchitectToolbar;

OArchitectToolbar.prototype.addElementToContainer = function (label, actionName) {
    this.addItem(this, label, actionName);
    var element = this.createElement(label, actionName);
    var editor = this.editor;
    var toolbar = this;
    this.container.appendChild(element);
    mxEvent.addListener(element, 'click', function () {
        if (!toolbar.elementExecuted) {
            editor.execute(actionName);
        }
    });
};

OArchitectToolbar.prototype.createElement = function (label, action) {
    var button = document.createElement('a');
    button.innerHTML = label;
    button.setAttribute('class', this.getCssClassByAction(action));
    button.style.margin = '5px';
    return button;
};

OArchitectToolbar.prototype.getCssClassByAction = function (action) {
    return 'btn btn-primary';
};

/**
 * Sidebar for {@link OArchitectEditor}. Extends from {@link OArchitectBar}
 * @param editor editor which need sidebar
 * @param container container (html element) for sidebar
 * @constructor
 */
var OArchitectSidebar = function (editor, container) {
    OArchitectBar.apply(this, arguments);
};

OArchitectSidebar.prototype = Object.create(OArchitectBar.prototype);
OArchitectSidebar.prototype.constructor = OArchitectSidebar;

OArchitectSidebar.prototype.addElementToContainer = function (label, actionName) {
    this.addItem(this, label, actionName);
    var element = this.createElement(label, actionName);
    this.container.appendChild(element);
    this.makeDraggable(element, actionName);
};

OArchitectSidebar.prototype.makeDraggable = function (element, actionName) {
    var editor = this.editor;
    var graph = editor.graph;
    var draggable = mxUtils.makeDraggable(element, graph, function (graph, evt, cell) {
        var mouseEvent = new mxMouseEvent(evt, new mxCellState(graph.getModel(), cell));
        graph.fireMouseEvent(mxEvent.MOUSE_UP, mouseEvent);
        editor.execute(actionName, cell, mouseEvent);
    }, null, -mxConstants.TOOLTIP_VERTICAL_OFFSET, -mxConstants.TOOLTIP_VERTICAL_OFFSET);
    draggable.getDropTarget = this.getDropTarget(actionName);
    draggable.mouseDown = function () {
        this.element.style.cursor = 'move';
        mxDragSource.prototype.mouseDown.apply(this, arguments);
    };
    draggable.mouseUp = function () {
        this.element.style.cursor = 'default';
        mxDragSource.prototype.mouseUp.apply(this, arguments);
    }
};

OArchitectSidebar.prototype.getDropTarget = function (actionName) {
    return function (graph, x, y) {
        if (actionName === OArchitectActionNames.ADD_OCLASS_ACTION) {
            return null;
        } else if (actionName === OArchitectActionNames.ADD_OPROPERTY_ACTION) {
            var cell = graph.getCellAt(x, y);
            var result = getPropertyTargetCell(cell);
            return result != null ? result : getPropertyTargetCell(graph.getModel().getParent(cell));
        } else if (actionName === OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION) {
            var cell = graph.getCellAt(x, y);
            return cell === null;
        }

        function getPropertyTargetCell(cell) {
            return graph.isClass(cell) ? cell : null;
        }
    };
};

OArchitectSidebar.prototype.createElement = function (label, action) {
    var a = document.createElement('a');
    a.classList.add(OArchitectConstants.SIDEBAR_ITEM_CLASS);
    a.setAttribute('title', label);
    a.appendChild(this.getIconElementForAction(action));
    return a;
};

OArchitectSidebar.prototype.getIconElementForAction = function (action) {
    var icon = document.createElement('i');
    if (action === OArchitectActionNames.ADD_OCLASS_ACTION) {
        icon.setAttribute('class', OArchitectConstants.FA_FILE_O);
    } else if (action === OArchitectActionNames.ADD_OPROPERTY_ACTION) {
        icon.setAttribute('class', OArchitectConstants.FA_ALIGN_JUSTIFY);
    } else if (action === OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION) {
        icon.setAttribute('class', OArchitectConstants.FA_DATABASE);
    }
    icon.classList.add(OArchitectConstants.FA_2X_CLASS);
    icon.style.margin = '5px';
    icon.style.display = 'block';
    return icon;
};