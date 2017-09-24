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
    var input = this.createNameInput(this.create);
    var typeSelect = this.createOTypeSelect(this.create, this.value, this.orientDbTypes);
    var inverseBlock = new OPropertyInverseBlock(this.value, this);
    inverseBlock.setDisabled(input.disabled);
    this.addValueBlock(body, input, typeSelect, inverseBlock.createElement(typeSelect));
    this.addButtonBlock(body, input, typeSelect, inverseBlock);
    this.addHeadBlock(head, this.create);
};

OPropertyEditModalWindow.prototype.addValueBlock = function (body, input, select, inverseBlock) {
    var valueBlock = this.createValueBlock();
    valueBlock.appendChild(this.createLabel(localizer.name + ':'));
    valueBlock.appendChild(input);
    valueBlock.appendChild(this.createLabel(localizer.type + ':'));
    valueBlock.appendChild(select);
    valueBlock.appendChild(inverseBlock);
    body.appendChild(valueBlock);
};

OPropertyEditModalWindow.prototype.addHeadBlock = function (head, create) {
    this.createHeadBlock(head, (create ? localizer.createProperty : localizer.editProperty) + ' (' + this.value.ownerClass.name + ')',
        OArchitectConstants.FA_ALIGN_JUSTIFY);
};

OPropertyEditModalWindow.prototype.addButtonBlock = function (body, input, typeSelect, inverseBlock) {
    var buttonBlock = this.createButtonBlock();
    var okBut = this.createOkButton(localizer.ok, input, typeSelect, inverseBlock);
    var cancelBut = this.createCancelButton(localizer.cancel);
    buttonBlock.appendChild(okBut);
    buttonBlock.appendChild(cancelBut);
    body.appendChild(buttonBlock);
};

OPropertyEditModalWindow.prototype.createOTypeSelect = function (createNewOProperty, property, types) {
    var select = this.createSelect(types);
    if (!createNewOProperty && property.type !== null) {
        select.disabled = !property.canModifyNameAndType();
        var index = types.indexOf(property.type.toUpperCase());
        if (index > -1)
            select.selectedIndex = index;
    } else select.selectedIndex = 0;
    return select;
};

OPropertyEditModalWindow.prototype.createNameInput = function (createNewOProperty) {
    var input = document.createElement('input');
    input.classList.add('form-control');
    input.setAttribute('type', 'text');
    if (createNewOProperty === false) {
        input.value = this.value.name;
        input.disabled = !this.value.canModifyNameAndType();
    }

    setTimeout(function () {
        if (!input.disabled)
            input.focus();
    }, 100);

    return input;
};

OPropertyEditModalWindow.prototype.createSelect = function (values) {
    var select = document.createElement('select');
    var modal = this;
    select.classList.add('form-control');
    OArchitectUtil.forEach(values, function (value) {
        select.appendChild(modal.createSelectOption(value));
    });
    return select;
};

OPropertyEditModalWindow.prototype.createSelectOption = function (value) {
    var option = document.createElement('option');
    option.setAttribute('value', value);
    option.innerHTML = value;
    return option;
};

OPropertyEditModalWindow.prototype.createOkButton = function (label, nameField, typeSelect, inverseBlock) {
    var button = this.newButton(label, OArchitectConstants.BUTTON_PRIMARY_CLASS);
    this.onEnterPressed = this.createOkButtonOnClickBehavior(nameField, typeSelect, inverseBlock);
    button.addEventListener('click', this.onEnterPressed);
    button.style.float = 'right';
    button.style.marginRight = '10px';
    button.style.marginBottom = '10px';
    return button;
};

OPropertyEditModalWindow.prototype.createOkButtonOnClickBehavior = function (nameField, typeSelect, inverseBlock) {
    var modal = this;
    var property = this.value;
    return function () {
        if (nameField.value.length > 0) {
            var name = nameField.value;
            if (OArchitectConstants.NAMING_PATTERN.test(name)) {
                action(name);
            } else modal.showErrorFeedback(localizer.propertyNameForbidden);
        } else {
            modal.showErrorFeedback(localizer.propertyEmptyName);
        }

        function action(name) {
            var type = typeSelect.options[typeSelect.selectedIndex].value;
            var existsProperty = property.ownerClass.getProperty(name);
            if (property.canUpdate(name, type, inverseBlock.inverseProperty, inverseBlock.inversePropertyEnable)) {
                updateProperty(name, type, inverseBlock.enableInverseProperty, inverseBlock.inverseProperty);
                modal.destroy(modal.OK);
            } else if (name === property.name && type === property.type) {
                modal.destroy(modal.OK);
            } else if (existsProperty != null && modal.create) {
                if (existsProperty.isSubClassProperty()) {
                    modal.showErrorFeedback(localizer.propertyExistsInSuperClass);
                } else modal.showErrorFeedback(localizer.propertyExistsInClass);
            }
        }

        function updateProperty(name, type, inversePropertyEnable, inverseProperty) {
            // app.editor.graph.getModel().beginUpdate();
            // try {
                property.updateProperty(name, type, inversePropertyEnable, inverseProperty);
                modal.afterUpdateValue(property);
            // } finally {
            //     app.editor.graph.getModel().endUpdate();
            // }
        }
    };
};

OPropertyEditModalWindow.prototype.afterUpdateValue = function (property) {};


var OPropertyInverseBlock = function (property, modal) {
    this.property = property;
    this.modal = modal;
    this.inverseProperty = null;
    this.enableInverseProperty = false;
    this.emptyInverseProperties = false;
    this.disabled = false;
    this.EMPTY = '';
};

OPropertyInverseBlock.prototype.inverseProperty        = null;
OPropertyInverseBlock.prototype.emptyInverseProperties = false;
OPropertyInverseBlock.prototype.enableInverseProperty  = false;
OPropertyInverseBlock.prototype.EMPTY                  = '';
OPropertyInverseBlock.prototype.disabled               = false;

OPropertyInverseBlock.prototype.createElement = function (typeSelect) {
    var checkbox = this.createCheckbox();
    var select = this.createInversePropertySelect();
    var mainBlock = this.createMainBlock(checkbox);
    var inverseBlock = this.createInverseBlock(select);
    mainBlock.appendChild(inverseBlock);
    switchDisplay(this);
    addEvents(this);

    function switchDisplay(inverse) {
        inverse.switchDisplay(OArchitectOType.isLink(typeSelect.value), mainBlock);
        inverse.switchDisplay(checkbox.checked, inverseBlock);
    }

    function addEvents(inverse) {
        checkbox.addEventListener('change', function () {
            inverse.switchDisplay(checkbox.checked, inverseBlock);
            inverse.enableInverseProperty = checkbox.checked;
        });
        typeSelect.addEventListener('change', function () {
            inverse.switchDisplay(OArchitectOType.isLink(typeSelect.value), mainBlock);
        });
    }

    return mainBlock;
};

OPropertyInverseBlock.prototype.createMainBlock = function (checkbox) {
    var div = document.createElement('div');
    div.appendChild(this.modal.createLabel(localizer.inverseEnable + ':'));
    div.appendChild(checkbox);
    return div;
};

OPropertyInverseBlock.prototype.createInverseBlock = function (select) {
    var div = document.createElement('div');
    if (this.isEmptyInverseProperties()) {
        div.appendChild(this.modal.createLabel(localizer.noAvailableInverseProperties + '!'));
    } else {
        div.appendChild(this.modal.createLabel(localizer.inverse + ':'));
        div.appendChild(select);
    }
    return div;
};

OPropertyInverseBlock.prototype.createCheckbox = function () {
    var checkbox = document.createElement('input');
    checkbox.setAttribute('type', 'checkbox');
    checkbox.classList.add('form-control');
    checkbox.style.width = 'auto';
    checkbox.style.height = 'auto';
    checkbox.style.marginLeft = '10px';
    if (!this.modal.create) {
        checkbox.checked = this.property.isInverseProperty();
    }
    this.enableInverseProperty = checkbox.checked;
    checkbox.disabled = this.disabled;
    return checkbox;
};


OPropertyInverseBlock.prototype.createInversePropertySelect = function () {
    var inverseBlock = this;
    var select = this.modal.createSelect();
    var linkedClass = this.property.linkedClass;
    var inverseValidProperties = linkedClass instanceof OArchitectOClass ? linkedClass.getAvailableInverseProperties() : [];
    var inverseProperty = this.property.inverseProperty;
    var propertyPresent = false;
    if (inverseValidProperties.length === 0 && inverseProperty !== null) {
        inverseValidProperties.push(inverseProperty);
    }
    this.clearSelectAndAddProperties(inverseValidProperties, select);
    inverseBlock.inverseProperty = inverseProperty;
    if (inverseProperty !== null && linkedClass !== null && inverseValidProperties.length > 0) {
        for (var i = 0; i < inverseValidProperties.length; i++) {
            if (inverseValidProperties[i].name === inverseProperty.name) {
                select.selectedIndex = i + 1;
                propertyPresent = true;
                break;
            }
        }
    }
    if (inverseValidProperties.length === 0 || this.disabled && !propertyPresent) {
        this.emptyInverseProperties = true;
    }

    select.addEventListener('change', function () {
         var json = select.options[select.selectedIndex].value;
         if (json !== inverseBlock.EMPTY) {
             inverseBlock.inverseProperty = OArchitectUtil.getPropertyFromJson(json);
         }
    });

    select.disabled = this.disabled;

    return select;
};

OPropertyInverseBlock.prototype.clearSelectAndAddProperties = function (properties, select) {
    this.clearSelect(select);
    select.appendChild(this.modal.createSelectOption(this.EMPTY));
    this.addPropertiesToSelect(properties, select);
};

OPropertyInverseBlock.prototype.addPropertiesToSelect = function (properties, select) {
    OArchitectUtil.forEach(properties, function (property) {
        var option = document.createElement('option');
        option.setAttribute('value', property.toJson());
        option.innerHTML = property.name + ' (' + localizer.type + ': ' + property.type + ')';
        select.appendChild(option);
    });
};

OPropertyInverseBlock.prototype.clearSelect = function (select) {
    while (select.options.length > 0) {
        select.remove(0);
    }
};

OPropertyInverseBlock.prototype.isInversePropertyEnabled = function () {
    return this.enableInverseProperty;
};

OPropertyInverseBlock.prototype.isEmptyInverseProperties = function () {
    return this.emptyInverseProperties;
};

OPropertyInverseBlock.prototype.setDisabled = function (disabled) {
    this.disabled = disabled;
};

OPropertyInverseBlock.prototype.switchDisplay = function (show, block) {
    if (show) {
        block.style.display = 'block';
    } else {
        block.style.display = 'none';
    }
};