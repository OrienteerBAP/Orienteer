
var OArchitectValueContainer = function (value, editor) {
    this.value = value;
    this.editor = editor;
};

OArchitectValueContainer.prototype.createElement = function (maxLength, iconCss) {
    return this.createContainer(this.createLabel(maxLength), this.createIcon(iconCss));
};

OArchitectValueContainer.prototype.createContainer = function (label, icon) {
    var div = document.createElement('div');
    if (icon !== null) div.appendChild(icon);
    div.appendChild(label);
    return div;
};

OArchitectValueContainer.prototype.createLabel = function (maxLength) {
    var label = this.value;
    if (label.length > maxLength) {
        label = label.slice(0, maxLength - 5) + '...';
    }
    var span = document.createElement('span');
    span.innerHTML = mxUtils.htmlEntities(label);
    return span;
};

OArchitectValueContainer.prototype.createIcon = function (cssClass) {
    var icon = document.createElement('i');
    icon.setAttribute('class', cssClass);
    icon.setAttribute('aria-hidden', 'true');
    return icon;
};

OArchitectValueContainer.prototype.addClickListenerForAction = function (element, action) {
    var editor = this.editor;
    var cell = this.cell;
    element.addEventListener('click', function (event) {
        editor.execute(action, cell, event);
    });
};

var OClassContainer = function (oClass, editor, cell) {
    OArchitectValueContainer.apply(this, arguments);
    this.cell = cell;
};

OClassContainer.prototype = Object.create(OArchitectValueContainer.prototype);
OClassContainer.prototype.constructor = OClassContainer;

OClassContainer.prototype.createElement = function (maxLength) {
    return this.createContainer(this.createLabel(maxLength), this.createEditIcon());
};

OClassContainer.prototype.createContainer = function (label, editElement) {
    var container = OArchitectValueContainer.prototype.createContainer.apply(this, arguments);
    container.addEventListener('mouseover', function () {
        editElement.style.visibility = 'visible';
        editElement.style.cursor = 'pointer';
    });
    container.addEventListener('mouseout', function () {
        editElement.style.visibility = 'hidden';
        editElement.style.cursor = 'default';
    });
    return container;
};

OClassContainer.prototype.createEditIcon = function () {
    var editElement = this.createIcon(OArchitectConstants.FA_EDIT);
    editElement.style.visibility = 'hidden';
    editElement.style.marginRight = '5px';
    this.addClickListenerForAction(editElement, OArchitectActionNames.EDIT_OCLASS_ACTION);
    return editElement;
};

var OPropertyContainer = function (property, editor, cell) {
    OArchitectValueContainer.apply(this, arguments);
    this.cell = cell;
};

OPropertyContainer.prototype = Object.create(OArchitectValueContainer.prototype);
OPropertyContainer.prototype.constructor = OPropertyContainer;

OPropertyContainer.prototype.createElement = function (maxLength) {
    var editProperty = !this.value.isSubClassProperty() ? this.createEditOPropertyElement() : null;
    var deleteProperty = !this.value.isSubClassProperty() ? this.createDeleteOPropertyElement() : null;
    var label = this.createLabel(maxLength);
    return this.createContainer(label, editProperty, deleteProperty);
};

OPropertyContainer.prototype.createContainer = function (label, editProperty, deleteProperty) {
    var container = document.createElement('div');
    if (editProperty !== null && deleteProperty !== null) {
        container.addEventListener('mouseover', function () {
            editProperty.style.visibility = 'visible';
            editProperty.style.cursor = 'pointer';
            deleteProperty.style.visibility = 'visible';
            deleteProperty.style.cursor = 'pointer';
        });
        container.addEventListener('mouseout', function () {
            editProperty.style.visibility = 'hidden';
            editProperty.style.cursor = 'default';
            deleteProperty.style.visibility = 'hidden';
            deleteProperty.style.cursor = 'default';
        });
    }
    if (editProperty !== null) container.appendChild(editProperty);
    container.appendChild(label);
    if (deleteProperty !== null) container.appendChild(deleteProperty);
    return container;
};

OPropertyContainer.prototype.createLabel = function (maxLength) {
    var name = this.value.name;
    var type = this.value.type;
    var typeLength = type.length;
    var nameLength = name.length;
    if (typeLength + nameLength > maxLength) {
        name = name.slice(0, maxLength - typeLength - 5) + '...';
    }
    var span = document.createElement('span');
    span.innerHTML = mxUtils.htmlEntities(name + ' (', false) + mxUtils.htmlEntities(type + ')', false);
    return span;
};

OPropertyContainer.prototype.createEditOPropertyElement = function () {
    var editElement = this.createIcon(OArchitectConstants.FA_EDIT);
    editElement.style.visibility = 'hidden';
    editElement.style.marginRight = '5px';
    this.addClickListenerForAction(editElement, OArchitectActionNames.EDIT_OPROPERTY_ACTION);
    return editElement;
};

OPropertyContainer.prototype.createDeleteOPropertyElement = function () {
    var deleteElement = this.createIcon(OArchitectConstants.FA_DELETE);
    deleteElement.style.visibility = 'hidden';
    deleteElement.style.marginLeft = '5px';
    this.addClickListenerForAction(deleteElement, OArchitectActionNames.DELETE_OPROPERTY_ACTION);
    return deleteElement;
};
