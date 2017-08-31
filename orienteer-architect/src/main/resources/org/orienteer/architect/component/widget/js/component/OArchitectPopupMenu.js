/**
 * Popup menu for {@link OArchitectEditor}
 * @param container - node element which contains popup menu
 * @param editor - {@link OArchitectEditor}
 * @constructor
 */
var OArchitectPopupMenu = function (container, editor, graph) {
    this.container = container;
    this.editor = editor;
};

/**
 * @type Node element which contains menu
 */
OArchitectPopupMenu.prototype.menu = null;

/**
 * @type {Array} which contains {@link OArchitectPopupMenuAction}
 */
OArchitectPopupMenu.prototype.popupMenuActions = [];

/**
 * Show popup menu in given coordinates
 */
OArchitectPopupMenu.prototype.show = function (event) {
    if (this.menu != null) this.destroy();
    this.menu = this.createMenuElement(event);
    this.container.appendChild(this.menu);
};

/**
 * Destroy popup menu
 */
OArchitectPopupMenu.prototype.destroy = function () {
    if (this.menu != null) {
        this.container.removeChild(this.menu);
        this.menu = null;
    }
};

/**
 * Add action to popup menu
 * @param action - {@link OArchitectPopupMenuAction}
 */
OArchitectPopupMenu.prototype.addAction = function (action) {
    if (this.popupMenuActions.indexOf(action) === -1) {
        this.popupMenuActions.push(action);
    }
};

/**
 * @param event - PointerEvent which contains coordinates
 * @returns Element - menu element
 */
OArchitectPopupMenu.prototype.createMenuElement = function (event) {
    var div = document.createElement('div');
    div.classList.add(OArchitectConstants.LIST_GROUP);
    div.style.position = 'absolute';
    div.style.left = event.getGraphX() + 'px';
    div.style.top = event.getGraphY() + 'px';
    var menu = this;
    var cell = event.getCell();
    OArchitectUtil.forEach(this.popupMenuActions, function (popupMenuAction) {
        if (cell != null && popupMenuAction.useOnCell || cell != null && !popupMenuAction.notOnCell || cell == null && !popupMenuAction.useOnCell) {
            if (popupMenuAction.isEnabled() && popupMenuAction.isValidCell(cell)) {
                div.appendChild(menu.createItemElement(popupMenuAction, cell, event));
            }
        }
    });
    return div;
};

/**
 * @param action {@link OArchitectPopupMenuAction}
 * @param cell {@link mxCell} under mouse
 * @param event - PointerEvent which contains coordinates
 * @returns Element - popup menu action element
 */
OArchitectPopupMenu.prototype.createItemElement = function (action, cell, event) {
    var a = document.createElement('a');
    a.classList.add(OArchitectConstants.LIST_GROUP_ITEM);
    a.style.cursor = 'pointer';
    a.appendChild(this.createIconElement(action.faIconCss));
    a.appendChild(this.createLabelElement(action.label));
    this.addClickActionToElement(a, action, cell, event);
    return a;
};

/**
 * @param iconCssClass - Font Awesome css icon class
 * @returns Element - which contains Font Awesome icon
 */
OArchitectPopupMenu.prototype.createIconElement = function (iconCssClass) {
    var icon = document.createElement('i');
    icon.setAttribute('class', iconCssClass);
    icon.classList.add(OArchitectConstants.POPUP_MENU_ITEM_ICON);
    return icon;
};

/**
 * @param label - string label for action
 * @returns Element - contains string label for action
 */
OArchitectPopupMenu.prototype.createLabelElement = function (label) {
    var span = document.createElement('span');
    span.classList.add(OArchitectConstants.POPUP_MENU_ITEM_LABEL);
    span.innerHTML = label;
    return span;
};

/**
 * Add event listener 'click' to element and fires action in editor after 'click' on element
 * @param element - Element
 * @param popupMenuAction - {@link OArchitectPopupMenuAction}
 * @param cell - {@link mxCell} under mouse
 * @param event - PointerEvent with coordinates
 */
OArchitectPopupMenu.prototype.addClickActionToElement = function (element, popupMenuAction, cell, event) {
    var popup = this;
    element.addEventListener('click', function () {
        popup.editor.execute(popupMenuAction.editorActionName, cell, event);
        popup.destroy();
    });
};

/**
 * Handler for {@link OArchitectPopupMenu} which shows menu
 * @param popupMenu {@link OArchitectPopupMenu}
 * @param graph {@link mxGraph}
 * @constructor
 */
var OArchitectPopupMenuHandler = function (popupMenu, graph) {
    this.popupMenu = popupMenu;
    this.graph = graph;
};

/**
 * @type boolean if true menu will shows on mouse up
 */
OArchitectPopupMenuHandler.prototype.showMenu = false;

OArchitectPopupMenuHandler.prototype.mouseDown = function (sender, evt) {
    this.destroy();
    this.showMenu = evt.isPopupTrigger();
};

OArchitectPopupMenuHandler.prototype.mouseMove = function (sender, evt) {
    this.showMenu = false;
};

OArchitectPopupMenuHandler.prototype.mouseUp = function (sender, evt) {
    if (this.showMenu) {
        this.show(evt);
    } else this.destroy();
};

/**
 * Show popup menu
 * @param evt - {@link mxMouseEvent} which contains coordinates
 * @param cell - {@link mxCell} under mouse pointer
 */
OArchitectPopupMenuHandler.prototype.show = function (evt) {
    this.popupMenu.show(evt);
};

/**
 * Destroy popup menu
 */
OArchitectPopupMenuHandler.prototype.destroy = function () {
    this.popupMenu.destroy();
};

/**
 * Action for {@link OArchitectPopupMenu}
 * @param label - string label which will be displayed
 * @param faIconCss - string Font Awesome icon. Css class, example: 'fa fa-edit'
 * @param editorActionName - string action name for {@link mxEditor}, see {@link OArchitectActionNames}
 * @param useOnCell - boolean if true - this action uses only for cells
 * @param notOnCell - boolean if true - this action uses only not for cells. notOnCell has highest priority than useOnCell
 * @constructor
 */
var OArchitectPopupMenuAction = function (label, faIconCss, editorActionName, useOnCell, notOnCell) {
    this.label = label;
    this.faIconCss = faIconCss;
    this.editorActionName = editorActionName;
    this.useOnCell = useOnCell;
    this.notOnCell = notOnCell;
};

/**
 * Function which returns true if cell under mouse pointer is valid for this action
 * @returns boolean true by default
 */
OArchitectPopupMenuAction.prototype.isValidCell = function (cell) {
    return true;
};

OArchitectPopupMenuAction.prototype.isEnabled = function () {
    return true;
};