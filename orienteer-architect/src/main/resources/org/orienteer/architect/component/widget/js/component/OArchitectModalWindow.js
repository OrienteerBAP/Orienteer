
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
    this.errorFeedback = null;
    this.onDestroy = onDestroy;
};

/**
 * Function which calls when user press enter
 */
OArchitectModalWindow.prototype.onEnterPressed = null;

OArchitectModalWindow.prototype.show = function (x, y) {
    if (!this.isShow) {
        var id = '#' + this.containerId;
        var modal = this;
        $(id).append(this.createModalElement(x, y));
        $('#' + this.getMarkupId()).draggable({
            containment: id,
            cursor: 'move'
        });
        $(window).keydown(function (event) {
            if (event.keyCode === OArchitectConstants.ENTER_KEY) {
                if (modal.onEnterPressed != null)
                    modal.onEnterPressed();
            } else if (event.keyCode === OArchitectConstants.ESC_KEY) {
                modal.destroy(modal.CANCEL);
            }
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
        $(window).off('keydown');
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
    var panel = document.createElement('span');
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
    this.errorFeedback = this.createErrorFeedback();
    panel.appendChild(body);
    panel.appendChild(this.errorFeedback);
    this.createContent(panel, head, body);
    return panel;
};

OArchitectModalWindow.prototype.createErrorFeedback = function () {
    var div = document.createElement('div');
    div.classList.add('alert');
    div.classList.add('alert-danger');
    div.style.display = 'none';
    div.style.margin = '0 10px 10px 10px';
    return div;
};

OArchitectModalWindow.prototype.showErrorFeedback = function (msg) {
    if (this.errorFeedback != null) {
        this.errorFeedback.innerHTML = msg;
        this.errorFeedback.style.display = 'block';
    }
};

OArchitectModalWindow.prototype.hideErrorFeedback = function () {
    if (this.errorFeedback != null) {
        this.errorFeedback.style.display = 'none';
    }
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

OArchitectModalWindow.prototype.createHeadBlock = function (headElement, content, faIconCss) {
    var icon = this.createIcon(faIconCss);
    var span = this.createSpan(content);
    icon.classList.add(OArchitectConstants.MODAL_WINDOW_TITLE_ICON);
    span.classList.add(OArchitectConstants.MODAL_WINDOW_TITLE);
    headElement.appendChild(icon);
    headElement.appendChild(span);
};

OArchitectModalWindow.prototype.createIcon = function (faIconCss) {
    var icon = document.createElement('i');
    icon.setAttribute('class', faIconCss);
    return icon;
};

OArchitectModalWindow.prototype.createSpan = function (content) {
    var span = document.createElement('span');
    span.innerHTML = content;
    return span;
};

var OArchitectInfoModalWindow = function (msg, containerId) {
    OArchitectModalWindow.apply(this, arguments);
};

OArchitectInfoModalWindow.prototype = Object.create(OArchitectModalWindow.prototype);
OArchitectInfoModalWindow.prototype.constructor = OArchitectInfoModalWindow;

OArchitectInfoModalWindow.prototype.createContent = function (panel, head, body) {
    this.createHeadBlock(head, localizer.info, OArchitectConstants.FA_INFO_CIRCLE);
    body.appendChild(this.createMsgContent());
    body.appendChild(this.createOkButton());
};

OArchitectInfoModalWindow.prototype.createMsgContent = function () {
    var content = document.createElement('div');
    content.style.margin = '10px';
    content.innerHTML = this.value;
    return content;
};

OArchitectInfoModalWindow.prototype.createOkButton = function () {
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

OArchitectInfoModalWindow.prototype.onEnterPressed = function () {
    this.destroy(this.OK);
};

var OArchitectErrorModalWindow = function (msg, containerId) {
    OArchitectInfoModalWindow.apply(this, arguments);
};

OArchitectErrorModalWindow.prototype = Object.create(OArchitectInfoModalWindow.prototype);
OArchitectErrorModalWindow.prototype.constructor = OArchitectErrorModalWindow;

OArchitectErrorModalWindow.prototype.createContent = function (panel, head, body) {
    this.createHeadBlock(head, localizer.error, OArchitectConstants.FA_ERROR);
    body.appendChild(this.createMsgContent());
    body.appendChild(this.createOkButton());
};
