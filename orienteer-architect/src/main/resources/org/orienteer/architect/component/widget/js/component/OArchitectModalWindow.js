
var modalWindowCounter = 0;

/**
 * Modal window
 * @param value value which will be store in modal window
 * @param containerId container id of modal window
 * @param onDestroy callback function which calls when modal window destroy
 * @constructor
 */
var OArchitectModalWindow = function (value, containerId, onDestroy) {
    this.CANCEL = 'CANCEL';
    this.OK     = 'OK';

    this.value = value;
    this.containerId = containerId;
    this.isShow = false;
    this.markupId = null;
    this.onDestroy = onDestroy;
};

OArchitectModalWindow.prototype.show = function (x, y) {
    if (!this.isShow) {
        var id = '#' + this.containerId;
        $(id).append(this.createModalElement(x, y));
        $('#' + this.getMarkupId()).draggable({
            containment: id
        });
        this.isShow = true;
        this.internalOnShow();
    } else throw new Error('Scheme editor modal window is already show!');
};

OArchitectModalWindow.prototype.destroy = function (event) {
    if (this.isShow) {
        modalWindowCounter--;
        this.internalOnDestroy(this.value, event);
        $('#' + this.getMarkupId()).remove();
        this.markupId = null;
        this.isShow = false;
    } else throw new Error('Can\'t destroy modal window, because it is not show!');
};

OArchitectModalWindow.prototype.internalOnDestroy = function (value, event) {
    app.editor.keyHandler.handler.setEnabled(true);
    if (this.onDestroy != null) this.onDestroy(value, event);
};

OArchitectModalWindow.prototype.internalOnShow = function () {
    app.editor.keyHandler.handler.setEnabled(false);
};


OArchitectModalWindow.prototype.createModalElement = function (x, y) {
    var panel = document.createElement('div');
    panel.style.width = '300px';
    panel.style.position = 'absolute';
    panel.classList.add('panel');
    panel.classList.add('panel-default');
    panel.setAttribute('id', this.getMarkupId());
    panel.style.left = x + 'px';
    panel.style.top = y + 'px';
    var head = document.createElement('div');
    head.classList.add('panel-heading');
    var body = document.createElement('div');
    body.classList.add('panel-body');
    panel.appendChild(head);
    panel.appendChild(body);
    this.createContent(panel, head, body);
    return panel;
};

OArchitectModalWindow.prototype.getMarkupId = function () {
    if (!this.markupId) {
        this.markupId = 'schemeEditorModalWindow' + modalWindowCounter;
        modalWindowCounter++;
    }
    return this.markupId;
};

OArchitectModalWindow.prototype.createContent = function (panel, head, body) {
    head.innerHTML = 'No head content';
    body.innerHTML = 'No body content';
};

OArchitectModalWindow.prototype.newButton = function (label, typeCssClass) {
    var but = document.createElement('a');
    but.classList.add('btn');
    but.classList.add(typeCssClass);
    but.classList.add('btn-sm');
    but.innerHTML = label;
    return but;
};


var InfoModalWindow = function (msg, containerId) {
    OArchitectModalWindow.apply(this, arguments);
};

InfoModalWindow.prototype = Object.create(OArchitectModalWindow.prototype);
InfoModalWindow.prototype.constructor = InfoModalWindow;

InfoModalWindow.prototype.createContent = function (panel, head, body) {
    head.innerHTML = localizer.info;
    body.appendChild(this.createMsgContent());
    body.appendChild(this.createOkButton());
};

InfoModalWindow.prototype.createMsgContent = function () {
    var content = document.createElement('div');
    content.style.margin = '10px';
    content.innerHTML = this.value;
    return content;
};

InfoModalWindow.prototype.createOkButton = function () {
    var ok = this.newButton(localizer.ok, OArchitectConstants.BUTTON_PRIMARY_CLASS);
    var modal = this;
    ok.addEventListener('click', function () {
        modal.destroy(modal.OK);
    });
    ok.style.float = 'right';
    ok.style.marginRight = '10px';
    ok.style.marginBottom = '10px';
    return ok;
};