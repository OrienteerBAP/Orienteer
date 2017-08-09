/**
 * Modal window for edit or create {@link OArchitectOProperty}
 * @param property {@link OArchitectOProperty} for create or edit
 * @param containerId id of element which contains modal window
 * @param create true if create new property
 * @constructor
 */
var OPropertyEditModalWindow = function (property, containerId, create) {
    OClassEditModalWindow.apply(this, arguments);
    this.create = create;
};

OPropertyEditModalWindow.prototype = Object.create(OClassEditModalWindow.prototype);
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

OPropertyEditModalWindow.prototype.createOTypeSelect = function (createNewOProperty) {
    var select = document.createElement('select');
    select.classList.add('form-control');
    for (var i = 0; i < OArchitectOType.size(); i++) {
        var option = document.createElement('option');
        option.setAttribute('value', OArchitectOType.get(i));
        option.innerHTML = OArchitectOType.get(i);
        select.appendChild(option);
    }
    if (!createNewOProperty) {
        if (OArchitectOType.contains(this.value.type)) select.selectedIndex = OArchitectOType.getIndexByValue(this.value.type);
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
    var button = this.newButton(label, OArchitectConstants.BUTTON_PRIMARY_CLASS);
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
