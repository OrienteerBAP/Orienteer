
var OArchitectValueContainer = function (value, editor) {
    this.value = value;
    this.editor = editor;
};

OArchitectValueContainer.prototype.createElement = function (maxLength, iconCss) {
    return this.createContainer(this.createLabel(this.value, maxLength), this.createIcon(iconCss));
};

OArchitectValueContainer.prototype.createContainer = function (label, icon) {
    var div = document.createElement('div');
    if (icon !== null) div.appendChild(icon);
    div.appendChild(label);
    return div;
};

OArchitectValueContainer.prototype.createLabel = function (value, maxLength) {
    if (value.length > maxLength) {
        value = value.slice(0, maxLength - 5) + '...';
    }
    var span = document.createElement('span');
    span.innerHTML = mxUtils.htmlEntities(value);
    return span;
};

OArchitectValueContainer.prototype.createIcon = function (cssClass) {
    var icon = document.createElement('i');
    icon.setAttribute('class', cssClass);
    icon.setAttribute('aria-hidden', 'true');
    return icon;
};

OArchitectValueContainer.prototype.createExternalLink = function (existsInDb) {
    var a = document.createElement('a');
    a.classList.add('editor-icon');
    a.appendChild(this.createIcon(OArchitectConstants.FA_EXTERNAL_LINK));
    if (existsInDb) {
        var url = this.value.pageUrl;
        if (url != null && url.length > 0) {
            a.setAttribute('target', '_blank');
            a.setAttribute('href', url);
        }
    }
    return a;
};

OArchitectValueContainer.prototype.addClickListenerForAction = function (element, action) {
    var editor = this.editor;
    var cell = this.cell;
    mxEvent.redirectMouseEvents(element, editor.graph, null, function (evt) {
        var mouseEvent = new mxMouseEvent(evt, new mxCellState(editor.graph.getModel(), cell));
        editor.graph.fireMouseEvent(mxEvent.MOUSE_DOWN, mouseEvent);
        editor.execute(action, cell, mouseEvent);
    });
};

var OClassContainer = function (oClass, editor, cell) {
    OArchitectValueContainer.apply(this, arguments);
    this.cell = cell;
};

OClassContainer.prototype = Object.create(OArchitectValueContainer.prototype);
OClassContainer.prototype.constructor = OClassContainer;

OClassContainer.prototype.createElement = function (maxLength) {
    return this.createContainer(this.createLabel(this.value.name, maxLength), this.createLinkOrEditOClassElement());
};

OClassContainer.prototype.createContainer = function (label, editElement) {
    var container = OArchitectValueContainer.prototype.createContainer.apply(this, arguments);
    if (editElement != null) {
        container.addEventListener('mouseover', function () {
            editElement.style.visibility = 'visible';
            editElement.style.cursor = 'pointer';
        });
        container.addEventListener('mouseout', function () {
            editElement.style.visibility = 'hidden';
            editElement.style.cursor = 'default';
        });
    }
    return container;
};

OClassContainer.prototype.createLinkOrEditOClassElement = function () {
    var element = null;
    if (this.value.existsInDb) {
        element = this.createExternalLink(this.value.existsInDb);
        element.setAttribute('title', localizer.goToOClassPage);
    } else if (app.canUpdate) {
        element = this.createIcon(OArchitectConstants.FA_EDIT);
        this.addClickListenerForAction(element, OArchitectActionNames.EDIT_OCLASS_ACTION);
    }
    if (element !== null) {
        element.style.visibility = 'hidden';
        element.style.marginRight = '5px';
    }
    return element;
};


var OPropertyContainer = function (property, editor, cell) {
    OArchitectValueContainer.apply(this, arguments);
    this.cell = cell;
};

OPropertyContainer.prototype = Object.create(OArchitectValueContainer.prototype);
OPropertyContainer.prototype.constructor = OPropertyContainer;

OPropertyContainer.prototype.createElement = function (maxLength) {
    var link = this.createLinkOPropertyIcon();
    var editProperty = this.createEditOPropertyIcon();
    var deleteProperty = !this.value.isSubClassProperty() ? this.createDeleteOPropertyIcon() : null;
    var label = this.createLabel(maxLength);
    return this.createContainer(label, link, editProperty, deleteProperty);
};

OPropertyContainer.prototype.createContainer = function (label, link, editProperty, deleteProperty) {
    var container = document.createElement('div');
    if (link !== null) container.appendChild(link);
    if (editProperty !== null) container.appendChild(editProperty);
    container.appendChild(label);
    if (deleteProperty !== null) container.appendChild(deleteProperty);

    container.addEventListener('mouseover', function () {
        showElement(link);
        showElement(editProperty);
        showElement(deleteProperty);
    });

    container.addEventListener('mouseout', function () {
        hideElement(link);
        hideElement(editProperty);
        hideElement(deleteProperty);
    });

    function showElement(element) {
        if (element !== null) {
            element.style.visibility = 'visible';
            element.style.cursor = 'pointer';
        }
    }

    function hideElement(element) {
        if (element !== null) {
            element.style.visibility = 'hidden';
            element.style.cursor = 'default';
        }
    }

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

OPropertyContainer.prototype.createLinkOPropertyIcon = function () {
    var element = null;
    if (this.value.existsInDb) {
        element = this.createExternalLink(this.value.existsInDb);
        element.setAttribute('title', localizer.goToOPropertyPage);
    }
    if (element !== null) {
        element.style.visibility = 'hidden';
        element.style.marginRight = '5px';
    }
    return element;
};

OPropertyContainer.prototype.createEditOPropertyIcon = function () {
    var element = null;
    if (app.canUpdate) {
        element = this.createIcon(OArchitectConstants.FA_EDIT);
        this.addClickListenerForAction(element, OArchitectActionNames.EDIT_OPROPERTY_ACTION);
    }
    if (element !== null) {
        element.style.visibility = 'hidden';
        element.style.marginRight = '5px';
    }
    return element;
};

OPropertyContainer.prototype.createDeleteOPropertyIcon = function () {
    var deleteElement = null;
    if (!this.value.existsInDb && app.canUpdate) {
        deleteElement = this.createIcon(OArchitectConstants.FA_DELETE);
        deleteElement.style.visibility = 'hidden';
        deleteElement.style.marginLeft = '5px';
        this.addClickListenerForAction(deleteElement, OArchitectActionNames.DELETE_OPROPERTY_ACTION);
    }
    return deleteElement;
};
