/**
 * Modal window for create or edit {@link OArchitectOClass}
 * @param oClass {@link OArchitectOClass} for edit
 * @param containerId id of element which contains modal window
 * @param onDestroy callback function which calls when modal window destroy
 * @param create true if create new class
 * @constructor
 */
var OClassEditModalWindow = function (oClass, containerId, onDestroy, create) {
    OArchitectModalWindow.apply(this, arguments);
    this.create = create;
    this.input = null;
};

OClassEditModalWindow.prototype = Object.create(OArchitectModalWindow.prototype);
OClassEditModalWindow.prototype.constructor = OClassEditModalWindow;

OClassEditModalWindow.prototype.createContent = function (panel, head, body) {
    var input = this.createNameInput(this.create);
    this.addValueBlock(body, input);
    this.addButtonBlock(body, input);
    this.addHeadBlock(head, this.create);
};

OClassEditModalWindow.prototype.createValueBlock = function () {
    var div = document.createElement('div');
    div.style.margin = '10px';
    return div;
};

OClassEditModalWindow.prototype.createButtonBlock = function () {
    var div = document.createElement('div');
    return div;
};

OClassEditModalWindow.prototype.addValueBlock = function (body, input) {
    var valueBlock = this.createValueBlock();
    valueBlock.appendChild(this.createLabel(localizer.name + ':'));
    valueBlock.appendChild(input);
    body.appendChild(valueBlock);
};

OClassEditModalWindow.prototype.addHeadBlock = function (head, create) {
    this.createHeadBlock(head, create ? localizer.createClass : localizer.editClass,
        OArchitectConstants.FA_FILE_O);
};

OClassEditModalWindow.prototype.addButtonBlock = function (body, input) {
    var buttonBlock = this.createButtonBlock();
    var okBut = this.createOkButton(localizer.ok, input);
    var cancelBut = this.createCancelButton(localizer.cancel);

    buttonBlock.appendChild(okBut);
    buttonBlock.appendChild(cancelBut);
    body.appendChild(buttonBlock);
};

OClassEditModalWindow.prototype.createNameInput = function (createNewOClass) {
    this.input = document.createElement('input');
    this.input.classList.add('form-control');
    this.input.setAttribute('type', 'text');
    if (!createNewOClass) {
        this.input.value = this.value.name;
    }

    return this.input;
};

OClassEditModalWindow.prototype.createOkButton = function (label, nameField) {
    var button = this.newButton(label, OArchitectConstants.BUTTON_PRIMARY_CLASS);
    this.onEnterPressed = this.createOkButtonOnClickBehavior(nameField);
    button.addEventListener('click', this.onEnterPressed);
    button.style.float = 'right';
    button.style.marginRight = '10px';
    button.style.marginBottom = '10px';
    return button;
};

OClassEditModalWindow.prototype.createCancelButton = function (label) {
    var button = this.newButton(label, OArchitectConstants.BUTTON_DANGER_CLASS);
    var modal = this;
    button.addEventListener('click', function () {
        modal.destroy(modal.CANCEL);
    });
    button.style.float = 'left';
    button.style.marginLeft = '10px';
    button.style.marginBottom = '10px';
    return button;
};

OClassEditModalWindow.prototype.createLabel = function (label) {
    var element = document.createElement('label');
    element.innerHTML = label;
    return element;
};

OClassEditModalWindow.prototype.internalOnShow = function () {
    if (this.input != null) {
        this.input.focus();
    }
    OArchitectModalWindow.prototype.internalOnShow.apply(this, arguments);
};

OClassEditModalWindow.prototype.createOkButtonOnClickBehavior = function (nameField) {
    var modal = this;
    return function () {
        if (nameField.value.length > 0) {
            var name = nameField.value;
            if (OArchitectConstants.NAMING_PATTERN.test(name)) {
                modal.value.setName(name, function (oClass, msg) {
                    if (oClass.name === name) {
                        modal.destroy(modal.OK);
                    } else modal.showErrorFeedback(msg);
                });
            } else modal.showErrorFeedback(localizer.classNameForbidden);
        } else modal.showErrorFeedback(localizer.classEmptyName);
    };
};
