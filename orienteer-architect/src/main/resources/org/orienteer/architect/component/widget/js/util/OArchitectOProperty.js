var OArchitectOProperty = function (ownerClass, name, type, cell) {
    this.ownerClass = null;
    this.name = null;
    this.type = null;
    this.linkedClass = null;
    this.subClassProperty = false;
    this.previousName = null;
    this.cell = null;

    if (ownerClass != null) this.setOwnerClassName(ownerClass);
    if (name != null) this.setName(name);
    if (type != null) this.setType(type);
    if (cell != null) this.setCell(cell);
};

OArchitectOProperty.prototype.configFromJSON = function (oClass, json) {
    this.setName(json.name);
    this.setType(json.type);
    this.subClassProperty = json.subClassProperty;
    this.ownerClass = oClass;
    var linkedClassCell = OArchitectUtil.getCellByClassName(json.linkedClass);
    if (linkedClassCell != null)
        this.setLinkedClass(linkedClassCell.value);
    oClass.properties.push(this);
    oClass.changeProperties(oClass, [this], this.subClassProperty, false);
};

OArchitectOProperty.prototype.configFromEditorConfig = function (oClass, propertyCell) {
    this.ownerClass = oClass;
    this.cell = propertyCell;
    var linkedCell = OArchitectUtil.getCellByClassName(this.linkedClass);
    if (linkedCell != null)
        this.setLinkedClass(linkedCell.value);
    oClass.properties.push(this);
    oClass.changeProperties(oClass, [this], this.subClassProperty, false);
};

OArchitectOProperty.prototype.setType = function (type) {
    if (OArchitectOType.contains(type)) {
        this.type = type;
    }
};

OArchitectOProperty.prototype.setName = function (name) {
    if (name != null) {
        this.previousName = this.name;
        this.name = name;
    } else console.warn('Can\'t set name of property: ' + name);
};

OArchitectOProperty.prototype.setOwnerClassName = function (ownerClass) {
    this.ownerClass = ownerClass;
};

OArchitectOProperty.prototype.setCell = function (cell) {
    if (cell != null) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        try {
            this.cell = cell;
            graph.getModel().setValue(this.cell, this);
        } finally {
            graph.getModel().endUpdate();
        }
    }
};

OArchitectOProperty.prototype.notifySubClassesPropertiesAboutChanges = function () {
    this.ownerClass.changeProperties(this.ownerClass, [this], this.subClassProperty, false);
};

OArchitectOProperty.prototype.isSubClassProperty = function () {
    return this.subClassProperty;
};

OArchitectOProperty.prototype.isLink = function () {
    return OArchitectOType.isLink(this.type);
};

OArchitectOProperty.prototype.canConnect = function () {
    var result = false;
    if (this.type !== null && OArchitectOType.isLink(this.type)) {
        result = this.linkedClass == null && !this.subClassProperty;
    }
    return result;
};

OArchitectOProperty.prototype.canDisconnect = function () {
    return !this.subClassProperty;
};

OArchitectOProperty.prototype.setLinkedClass = function (linkedClass) {
    if (this.linkedClass !== linkedClass) {
        if (linkedClass == null && this.linkedClass != null) {
            OArchitectUtil.manageEdgesBetweenCells(this.cell, this.linkedClass.cell, false);
        } else if (linkedClass != null) {
            OArchitectUtil.manageEdgesBetweenCells(this.cell, linkedClass.cell, true);
        }
        this.linkedClass = linkedClass;
        this.ownerClass.changeProperties(this.ownerClass, [this]);
    }
};

OArchitectOProperty.prototype.toString = function () {
    return this.name;
};

OArchitectOProperty.prototype.toJson = function () {
    function filter(key, value) {
        if (key === 'cell') {
            value = undefined;
        } else if (key === 'ownerClass' || key === 'linkedClass') {
            value = value != null ? value.name : null;
        }
        return value;
    }

    return JSON.stringify(this, filter);
};

OArchitectOProperty.prototype.toEditorConfigObject = function () {
    var result = new OArchitectOProperty(this.ownerClass.name, this.name, this.type, null);
    result.linkedClass = this.linkedClass != null ? this.linkedClass.name : null;
    result.previousName = this.previousName;
    result.subClassProperty = this.subClassProperty;
    return result;
};
