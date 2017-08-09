var OArchitectOProperty = function (oClassName, name, type) {
    this.oClassName = oClassName;
    this.name = name;
    this.type = type;
    this.linkedClassName = null;
    this.subClassProperty = false;
};

OArchitectOProperty.prototype.config = function (source) {
    this.name = source.name;
    this.type = OArchitectOType.contains(source.type) ? source.type : null;
    this.linkedClassName = source.linkedClassName;
    this.subClassProperty = source.subClassProperty;
};

OArchitectOProperty.prototype.setType = function (type) {
    if (OArchitectOType.contains(type)) {
        this.type = type;
    }
};

OArchitectOProperty.prototype.setName = function (name) {
    this.name = name;
};

OArchitectOProperty.prototype.setOClassName = function (oClassName) {
    this.oClassName = oClassName;
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
        result = this.linkedClassName == null;
    }
    return result;
};

OArchitectOProperty.prototype.setLinkedClassName = function (linkClassName) {
    this.linkedClassName = linkClassName;
};

OArchitectOProperty.prototype.toString = function () {
    return this.name;
};

OArchitectOProperty.prototype.clone = function () {
    return mxUtils.clone(this);
};