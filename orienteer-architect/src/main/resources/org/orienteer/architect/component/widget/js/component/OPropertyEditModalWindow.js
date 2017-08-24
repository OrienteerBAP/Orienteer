/**
 * Modal window for edit or create {@link OArchitectOProperty}
 * @param property {@link OArchitectOProperty} for create or edit
 * @param containerId id of element which contains modal window
 * @param onDestroy callback function which calls when modal window destroy
 * @param create true if create new property
 * @constructor
 */
var OPropertyEditModalWindow = function (property, containerId, onDestroy, create) {
    OClassEditModalWindow.apply(this, arguments);
    this.create = create;
};

OPropertyEditModalWindow.prototype = Object.create(OClassEditModalWindow.prototype);
OPropertyEditModalWindow.prototype.constructor = OPropertyEditModalWindow;

OPropertyEditModalWindow.prototype.orientDbTypes = OArchitectOType.types;

OPropertyEditModalWindow.prototype.createContent = function (panel, head, body) {
    var select = this.createOTypeSelect(this.create);
    var input = this.createNameInput(this.create);
    this.addValueBlock(body, input, select);
    this.addButtonBlock(body, input, select);
    this.addHeadBlock(head, this.create);
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
    this.createHeadBlock(head, create ? localizer.createProperty : localizer.editProperty,
        OArchitectConstants.FA_ALIGN_JUSTIFY);
};

OPropertyEditModalWindow.prototype.addButtonBlock = function (body, input, select) {
    var buttonBlock = this.createButtonBlock();
    var okBut = this.createOkButton(localizer.ok, input, select);
    var cancelBut = this.createCancelButton(localizer.cancel);
    buttonBlock.appendChild(okBut);
    buttonBlock.appendChild(cancelBut);
    body.appendChild(buttonBlock);
};

OPropertyEditModalWindow.prototype.createOTypeSelect = function (createNewOProperty) {
    var select = document.createElement('select');
    select.classList.add('form-control');
    var types = this.orientDbTypes;
    for (var i = 0; i < types.length; i++) {
        var option = document.createElement('option');
        option.setAttribute('value', types[i]);
        option.innerHTML = types[i];
        select.appendChild(option);
    }
    if (!createNewOProperty && this.value.type != null) {
        var index = types.indexOf(this.value.type.toUpperCase());
        if (index > -1)
            select.selectedIndex = index;
    }
    return select;
};

//TODO: validate user input
OPropertyEditModalWindow.prototype.createNameInput = function (createNewOProperty) {
    this.input = document.createElement('input');
    this.input.classList.add('form-control');
    this.input.setAttribute('type', 'text');
    if (!createNewOProperty) {
        this.input.value = this.value.name;
    }
    return this.input;
};


OPropertyEditModalWindow.prototype.createOkButton = function (label, nameField, typeSelect) {
    var button = this.newButton(label, OArchitectConstants.BUTTON_PRIMARY_CLASS);
    button.addEventListener('click', this.createOkButtonOnClickBehavior(nameField, typeSelect));
    button.style.float = 'right';
    button.style.marginRight = '10px';
    button.style.marginBottom = '10px';
    return button;
};

OPropertyEditModalWindow.prototype.createOkButtonOnClickBehavior = function (nameField, typeSelect) {
    var modal = this;
    var property = this.value;
    return function () {
        if (nameField.value.length > 0) {
            var newName = nameField.value;
            var newType = typeSelect.options[typeSelect.selectedIndex].value;
            var existsProperty = property.ownerClass.getProperty(newName);
            if (property.isValidName(newName)) {
                setNameAndType(newName, newType);
                modal.destroy(modal.OK);
            } else if (existsProperty != null && modal.create) {
                if (existsProperty.isSubClassProperty()) {
                    modal.showErrorFeedback(localizer.propertyExistsInSuperClass);
                } else modal.showErrorFeedback(localizer.propertyExistsInClass);
            } else if (property.isValidType(newType)) {
                setNameAndType(newName, newType);
                modal.destroy(modal.OK);
            }
        }

        function setNameAndType(name, type) {
            app.editor.graph.getModel().beginUpdate();
            try {
                property.setNameAndType(name, type);
                modal.afterUpdateValue(property);
            } finally {
                app.editor.graph.getModel().endUpdate();
            }
        }
    };
};

OPropertyEditModalWindow.prototype.afterUpdateValue = function (property) {};