var OArchitectOProperty = function (ownerClass, name, type) {
    this.ownerClass = null;
    this.name = null;
    this.type = null;
    this.linkedClassName = null;
    this.subClassProperty = false;
    this.previousName = null;
    this.previousType = null;

    if (ownerClass != null) this.setOwnerClassName(ownerClass);
    if (name != null) this.setName(name);
    if (type != null) this.setType(type);
};

OArchitectOProperty.prototype.config = function (source) {
    this.name = source.name;
    this.type = OArchitectOType.contains(source.type) ? source.type : null;
    this.linkedClassName = source.linkedClassName;
    this.subClassProperty = source.subClassProperty;
};

OArchitectOProperty.prototype.setType = function (type) {
    if (OArchitectOType.contains(type)) {
        this.previousType = this.type;
        this.type = type;
        OArchitectUtil.changeAllSubProperties(app.editor.graph, this.ownerClass);
    }
};

OArchitectOProperty.prototype.setName = function (name) {
    if (name != null) {
        this.previousName = this.name;
        this.name = name;
        OArchitectUtil.changeAllSubProperties(app.editor.graph, this.ownerClass);
    } else console.warn('Can\'t set name of property: ' + name);
};


OArchitectOProperty.prototype.setOwnerClassName = function (ownerClass) {
    this.ownerClass = ownerClass;
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

OArchitectOProperty.prototype.getPreviousOPropertyState = function () {
    var previous = this.clone();

    return previous;
};

OArchitectOProperty.prototype.toString = function () {
    return this.name;
};

OArchitectOProperty.prototype.clone = function () {
    return mxUtils.clone(this);
};