
var modalWindowCounter = 0;

var SchemeEditorModalWindow = function (value, containerId) {
    this.CANCEL = 'CANCEL';
    this.OK     = 'OK';

    this.value = value;
    this.containerId = containerId;
    this.isShow = false;
    this.markupId = null;
};

SchemeEditorModalWindow.prototype.show = function (x, y) {
    if (!this.isShow) {
        var id = '#' + this.containerId;
        $(id).append(this.createModalElement(x, y));
        $('#' + this.getMarkupId()).draggable({
            containment: id
        });
        this.isShow = true;
    } else throw new Error('Scheme editor modal window is already show!');
};

SchemeEditorModalWindow.prototype.destroy = function (event) {
    if (this.isShow) {
        modalWindowCounter--;
        this.onDestroy(this.value, event);
        $('#' + this.getMarkupId()).remove();
        this.markupId = null;
        this.isShow = false;
    } else throw new Error('Can\'t destroy modal window, because it is not show!');
};

SchemeEditorModalWindow.prototype.onDestroy = function (value, event) {};

SchemeEditorModalWindow.prototype.createModalElement = function (x, y) {
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

SchemeEditorModalWindow.prototype.getMarkupId = function () {
    if (!this.markupId) {
        this.markupId = 'schemeEditorModalWindow' + modalWindowCounter;
        modalWindowCounter++;
    }
    return this.markupId;
};

SchemeEditorModalWindow.prototype.createContent = function (panel, head, body) {
    head.innerHTML = 'No head content';
    body.innerHTML = 'No body content';
};

SchemeEditorModalWindow.prototype.newButton = function (label, typeCssClass) {
    var but = document.createElement('a');
    but.classList.add('btn');
    but.classList.add(typeCssClass);
    but.classList.add('btn-sm');
    but.innerHTML = label;
    return but;
};


var OPropertyEditModalWindow = function (property, containerId, create) {
    SchemeEditorModalWindow.apply(this, arguments);
    this.create = create;
};

OPropertyEditModalWindow.prototype = Object.create(SchemeEditorModalWindow.prototype);
OPropertyEditModalWindow.prototype.constructor = OPropertyEditModalWindow;

OPropertyEditModalWindow.prototype.createContent = function (panel, head, body) {
    var select = this.createOTypeSelect(this.create);
    var input = this.createNameInput(this.create);
    this.addValueBlock(body, input, select);
    this.addButtonBlock(body, input, select);
    this.addHeadBlock(head);
};

OPropertyEditModalWindow.prototype.addValueBlock = function (body, input, select) {
    var valueBlock = this.createValueBlock();
    valueBlock.appendChild(this.createLabel(localizer.name + ':'));
    valueBlock.appendChild(input);
    valueBlock.appendChild(this.createLabel(localizer.type + ':'));
    valueBlock.appendChild(select);
    body.appendChild(valueBlock);
};

OPropertyEditModalWindow.prototype.addHeadBlock = function (head, create) {
    head.innerHTML = create ? localizer.createProperty : localizer.editProperty;
};

OPropertyEditModalWindow.prototype.addButtonBlock = function (body, input, select) {
    var buttonBlock = this.createButtonBlock();
    var okBut = this.createOkButton(localizer.ok, input, select);
    var cancelBut = this.createCancelButton(localizer.cancel);
    buttonBlock.appendChild(cancelBut);
    buttonBlock.appendChild(okBut);
    body.appendChild(buttonBlock);
};

OPropertyEditModalWindow.prototype.createValueBlock = function () {
    var div = document.createElement('div');
    div.style.margin = '10px';
    return div;
};

OPropertyEditModalWindow.prototype.createButtonBlock = function () {
    var div = document.createElement('div');
    return div;
};

OPropertyEditModalWindow.prototype.createOTypeSelect = function (createNewOProperty) {
    var select = document.createElement('select');
    select.classList.add('form-control');
    for (var i = 0; i < OType.size(); i++) {
        var option = document.createElement('option');
        option.setAttribute('value', OType.get(i));
        option.innerHTML = OType.get(i);
        select.appendChild(option);
    }
    if (!createNewOProperty) {
        if (OType.contains(this.value.type)) select.selectedIndex = OType.getIndexByValue(this.value.type);
    }
    return select;
};

//TODO: validate user input
OPropertyEditModalWindow.prototype.createNameInput = function (createNewOProperty) {
    var input = document.createElement('input');
    input.classList.add('form-control');
    input.setAttribute('type', 'text');
    if (!createNewOProperty) {
        input.value = this.value.name;
    }
    return input;
};

OPropertyEditModalWindow.prototype.createOkButton = function (label, nameField, typeSelect) {
    var button = this.newButton(label, BUTTON_PRIMARY_CLASS);
    var modal = this;
    button.addEventListener('click', function () {
        if (nameField.value.length > 0) {
            modal.value.setName(nameField.value);
            modal.value.setType(typeSelect.options[typeSelect.selectedIndex].value);
            modal.destroy(modal.OK);
        }
    });
    button.style.float = 'right';
    button.style.marginRight = '10px';
    button.style.marginBottom = '10px';
    return button;
};

OPropertyEditModalWindow.prototype.createCancelButton = function (label) {
    var button = this.newButton(label, BUTTON_DANGER_CLASS);
    var modal = this;
    button.addEventListener('click', function () {
        modal.destroy(modal.CANCEL);
    });
    button.style.float = 'left';
    button.style.marginLeft = '10px';
    button.style.marginBottom = '10px';
    return button;
};

OPropertyEditModalWindow.prototype.createLabel = function (label) {
    var element = document.createElement('label');
    element.innerHTML = label;
    return element;
};


var InfoModalWindow = function (msg, containerId) {
    SchemeEditorModalWindow.apply(this, arguments);
};

InfoModalWindow.prototype = Object.create(SchemeEditorModalWindow.prototype);
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
    var ok = this.newButton(localizer.ok, BUTTON_PRIMARY_CLASS);
    var modal = this;
    ok.addEventListener('click', function () {
        modal.destroy(modal.OK);
    });
    ok.style.float = 'right';
    ok.style.marginRight = '10px';
    ok.style.marginBottom = '10px';
    return ok;
};