var OArchitectOProperty = function (ownerClass, name, type, cell) {
    this.ownerClass = null;
    this.name = null;
    this.type = null;
    this.linkedClass = null;
    this.subClassProperty = false;
    this.cell = null;
    this.previousName = null;

    if (ownerClass != null) this.setOwnerClassName(ownerClass);
    if (name != null) this.setName(name);
    if (type != null) this.setType(type);
    if (cell != null) this.setCell(cell);
};

OArchitectOProperty.prototype.config = function (source) {
    this.name = source.name;
    this.type = OArchitectOType.contains(source.type) ? source.type : null;
    this.linkedClass = source.linkedClass;
    this.subClassProperty = source.subClassProperty;
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
        this.cell = cell;
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
        result = this.linkedClass == null;
    }
    return result;
};

OArchitectOProperty.prototype.setLinkedClass = function (linkClass) {
    this.linkedClass = linkClass;
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

OArchitectOProperty.prototype.clone = function () {
    return mxUtils.clone(this);
};