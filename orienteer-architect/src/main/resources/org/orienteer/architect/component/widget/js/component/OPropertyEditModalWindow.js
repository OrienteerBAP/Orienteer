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
    this.orientDbTypes = OArchitectOType.types;
};

OPropertyEditModalWindow.prototype = Object.create(OClassEditModalWindow.prototype);
OPropertyEditModalWindow.prototype.constructor = OPropertyEditModalWindow;

OPropertyEditModalWindow.prototype.orientDbTypes = OArchitectOType.types;

OPropertyEditModalWindow.prototype.createContent = function (panel, head, body) {
    var nameAndTypeBlock = new OPropertyNameAndTypeBlock(this.value, this.create, this.orientDbTypes, this);
    var inverseBlock = new OPropertyInverseBlock(this.value, this);
    inverseBlock.setDisabled(!this.value.canModifyInverseProperty());
    body.appendChild(nameAndTypeBlock.createElement());
    body.appendChild(inverseBlock.createElement(nameAndTypeBlock.typeSelect));
    this.addButtonBlock(body, nameAndTypeBlock, inverseBlock);
    this.addHeadBlock(head, this.create);
};

OPropertyEditModalWindow.prototype.addHeadBlock = function (head, create) {
    this.createHeadBlock(head, (create ? localizer.createProperty : localizer.editProperty) + ' (' + this.value.ownerClass.name + ')',
        OArchitectConstants.FA_ALIGN_JUSTIFY);
};

OPropertyEditModalWindow.prototype.addButtonBlock = function (body, nameAndTypeBlock, inverseBlock) {
    var buttonBlock = this.createButtonBlock();
    var okBut = this.createOkButton(localizer.ok, nameAndTypeBlock, inverseBlock);
    var cancelBut = this.createCancelButton(localizer.cancel);
    buttonBlock.appendChild(okBut);
    buttonBlock.appendChild(cancelBut);
    body.appendChild(buttonBlock);
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

OPropertyEditModalWindow.prototype.createOkButton = function (label, nameAndTypeBlock, inverseBlock) {
    var button = this.newButton(label, OArchitectConstants.BUTTON_PRIMARY_CLASS);
    this.onEnterPressed = this.createOkButtonOnClickBehavior(nameAndTypeBlock, inverseBlock);
    button.addEventListener('click', this.onEnterPressed);
    button.style.float = 'right';
    button.style.marginRight = '10px';
    button.style.marginBottom = '10px';
    return button;
};

OPropertyEditModalWindow.prototype.createOkButtonOnClickBehavior = function (nameAndTypeBlock, inverseBlock) {
    var modal = this;
    var property = this.value;
    return function () {
        if (nameAndTypeBlock.name !== null) {
            var name = nameAndTypeBlock.name;
            if (OArchitectConstants.NAMING_PATTERN.test(name)) {
                action(name);
            } else modal.showErrorFeedback(localizer.propertyNameForbidden);
        } else {
            modal.showErrorFeedback(localizer.propertyEmptyName);
        }

        function action(name) {
            var type = nameAndTypeBlock.type;
            var existsProperty = property.ownerClass.getProperty(name);
            if (existsProperty !== null && modal.create) {
                if (existsProperty.isSubClassProperty()) {
                    modal.showErrorFeedback(localizer.propertyExistsInSuperClass);
                } else modal.showErrorFeedback(localizer.propertyExistsInClass);
            } else if (property.canUpdate(name, type, inverseBlock.getInverseProperty(), inverseBlock.inversePropertyEnable)) {
                updateProperty(name, type, inverseBlock.enableInverseProperty, inverseBlock.getInverseProperty());
                modal.destroy(modal.OK);
            } else if (name === property.name && type === property.type) {
                modal.destroy(modal.OK);
            }
        }

        function updateProperty(name, type, inversePropertyEnable, inverseProperty) {
            var tempProperty = new OArchitectOProperty();
            tempProperty.name = name;
            tempProperty.type = type;
            tempProperty.inversePropertyEnable = inversePropertyEnable;
            tempProperty.inverseProperty = inverseProperty;
            modal.updateProperty(property, tempProperty);
            modal.afterUpdateValue(property);
        }
    };
};

OPropertyEditModalWindow.prototype.updateProperty = function (property, propertyWithChanges) {};

OPropertyEditModalWindow.prototype.afterUpdateValue = function (property) {};


var OPropertyNameAndTypeBlock = function (property, create, types, modal) {
    this.property = property;
    this.create = create;
    this.types = types;
    this.modal = modal;
    this.name = null;
    this.type = null;
    this.typeSelect = null;
    this.nameInput = null;
    this.block = null;
};

OPropertyNameAndTypeBlock.prototype.name = null;
OPropertyNameAndTypeBlock.prototype.type = null;
OPropertyNameAndTypeBlock.prototype.types = null;
OPropertyNameAndTypeBlock.prototype.modal = null;

OPropertyNameAndTypeBlock.prototype.createElement = function () {
    this.block = this.modal.createValueBlock();
    this.block.appendChild(this.modal.createLabel(localizer.name + ':'));
    this.block.appendChild(this.nameInput = this.createNameInput(this.property));
    this.block.appendChild(this.modal.createLabel(localizer.type + ':'));
    this.block.appendChild(this.createOTypeSelect(this.property));
    return this.block;
};

OPropertyNameAndTypeBlock.prototype.show = function () {
    this.block.style.display = 'block';
};

OPropertyNameAndTypeBlock.prototype.hide = function () {
    this.block.style.display = 'none';
};

OPropertyNameAndTypeBlock.prototype.createNameInput = function (property) {
    var input = document.createElement('input');
    var block = this;
    input.classList.add('form-control');
    input.setAttribute('type', 'text');
    if (!this.create) {
        input.value = property.name;
        input.disabled = !property.canModifyNameAndType();
        this.name = property.name;
    }

    setTimeout(function () {
        if (!input.disabled)
            input.focus();
    }, 100);

    input.addEventListener('input', function () {
        block.name = input.value;
    });
    return input;
};

OPropertyNameAndTypeBlock.prototype.createOTypeSelect = function (property) {
    var select = this.modal.createSelect(this.types);
    var block = this;
    if (!this.create && property.type !== null) {
        select.disabled = !property.canModifyNameAndType();
        var index = this.types.indexOf(property.type.toUpperCase());
        if (index > -1)
            select.selectedIndex = index;
        this.type = select.options[select.selectedIndex].value;
    } else select.selectedIndex = 0;
    block.type = select.options[select.selectedIndex].value;

    select.addEventListener('change', function () {
        block.type = select.options[select.selectedIndex].value;
    });
    this.typeSelect = select;
    return select;
};

var OPropertyInverseBlock = function (property, modal) {
    this.property = property;
    this.modal = modal;
    this.inverseProperty = null;
    this.enableInverseProperty = false;
    this.emptyInverseProperties = false;
    this.disabled = false;
    this.EMPTY = '';
    this.createInversePropertyBlock = null;
    this.needCreateInverseProperty = false;
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
    this.createInversePropertyBlock = new OPropertyNameAndTypeBlock(new OArchitectOProperty(), true, OArchitectOType.linkTypes, this.modal);
    this.needCreateInverseProperty = this.property.linkedClass !== null && checkbox.checked;
    mainBlock.appendChild(inverseBlock);
    mainBlock.appendChild(this.createInversePropertyBlock.createElement());
    switchDisplay(this);
    addEvents(this);

    function switchDisplay(inverse) {
        inverse.switchDisplay(OArchitectOType.isLink(typeSelect.value), mainBlock);
        inverse.switchDisplay(checkbox.checked, inverseBlock);
        switchCreateInverseProperty(checkbox, inverse);
    }

    function addEvents(inverse) {
        checkbox.addEventListener('change', function () {
            inverse.switchDisplay(checkbox.checked, inverseBlock);
            inverse.enableInverseProperty = checkbox.checked;
            inverse.needCreateInverseProperty = inverse.property.linkedClass !== null && checkbox.checked;
            switchCreateInverseProperty(checkbox, inverse);
        });
        typeSelect.addEventListener('change', function () {
            inverse.switchDisplay(OArchitectOType.isLink(typeSelect.value), mainBlock);
        });
        select.addEventListener('change', function () {
            switchDisplay(inverse);
        });
        inverse.createInversePropertyBlock.nameInput.addEventListener('input', function () {
            var name = inverse.createInversePropertyBlock.name;
            inverse.switchDisplay(name === null || name.length === 0, select);
        });
    }

    function switchCreateInverseProperty(checkbox, inverse) {
        if (checkbox.checked && isShowCreateInverseProperty(select, inverse)) {
            inverse.createInversePropertyBlock.show();
        } else {
            inverse.createInversePropertyBlock.hide();
        }
    }

    function isShowCreateInverseProperty(select, inverse) {
        return select.options[select.selectedIndex].value.length === 0 && inverse.needCreateInverseProperty;
    }

    return mainBlock;
};

OPropertyInverseBlock.prototype.createMainBlock = function (checkbox) {
    var div = this.modal.createValueBlock();
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
        } else inverseBlock.inverseProperty = null;
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

OPropertyInverseBlock.prototype.getInverseProperty = function () {
    if (!this.enableInverseProperty) this.inverseProperty = null;
    if (this.needCreateInverseProperty && this.createInversePropertyBlock.name !== null) {
        this.inverseProperty = new OArchitectOProperty();
        this.inverseProperty.ownerClass = this.property.linkedClass;
        this.inverseProperty.name = this.createInversePropertyBlock.name;
        this.inverseProperty.type = this.createInversePropertyBlock.type;
        this.inverseProperty.inversePropertyEnable = true;
        this.inverseProperty.linkedClass = this.property.ownerClass;
    }
    return this.inverseProperty;
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