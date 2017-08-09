var OArchitectOClass = function(name) {
    this.name = name;
    this.properties = [];
    this.propertiesForDelete = [];
    this.superClassesNames = [];
    this.existsInDb = false;
};

OArchitectOClass.prototype.config = function (source) {
    this.name = source.name;
    this.properties = toOProperties(this, source.properties);
    this.superClassesNames = source.superClassesNames;

    function toOProperties(oClass, sourceProperties) {
        var properties = [];
        if (sourceProperties !== null && sourceProperties.length > 0) {
            for (var i = 0; i < sourceProperties.length; i++) {
                var property = new OArchitectOProperty(oClass.name);
                property.config(sourceProperties[i]);
                properties.push(property);
            }
        }
        return properties;
    }
};

OArchitectOClass.prototype.addOProperty = function (oProperty) {
    if (!this.containsProperty(oProperty)) {
        this.properties.push(oProperty);
        var delIndex = this.propertiesForDelete.indexOf(oProperty);
        if (delIndex > -1) {
            this.propertiesForDelete.splice(delIndex, 1);
        }
    }
};

OArchitectOClass.prototype.putOProperty = function (oProperty) {
    var index = this.getPropertyIndex(oProperty);
    if (index > -1) {
        this.properties[index] = oProperty;
    } else this.addOProperty(oProperty);
};

OArchitectOClass.prototype.removeProperty = function (oProperty) {
    var index = this.getPropertyIndex(oProperty);
    if (index > -1) {
        var isSubClassProperty = this.properties[index].isSubClassProperty();
        this.properties.splice(index, 1);
        if (!isSubClassProperty) {
            index = this.propertiesForDelete.indexOf(oProperty);
            if (index === -1) this.propertiesForDelete.push(oProperty);
        }
    }
};

OArchitectOClass.prototype.addSuperClassName = function (superClassName) {
    if (this.superClassesNames.indexOf(superClassName) === -1) {
        this.superClassesNames.push(superClassName);
    }
};

OArchitectOClass.prototype.removeSuperClassName = function (superClassName) {
    var index = this.superClassesNames.indexOf(superClassName);
    if (index > -1) {
        this.superClassesNames.splice(index, 1);
    }
};

OArchitectOClass.prototype.containsSuperClass = function (superClassName) {
    return this.superClassesNames.indexOf(superClassName) > -1;
};

OArchitectOClass.prototype.containsProperty = function (property) {
    return this.getPropertyIndex(property) > -1;
};

OArchitectOClass.prototype.getPropertyIndex = function (property) {
    var index = -1;
    if (property !== null && property.name != null && property.type != null) {
        var properties = this.properties;
        for(var i = 0; i < properties.length; i++) {
            if (properties[i].name === property.name && properties[i].type === property.type) {
                index = i;
            }
        }
    }
    return index;
};

OArchitectOClass.prototype.toString = function () {
    return this.name;
};

OArchitectOClass.prototype.clone = function () {
    return mxUtils.clone(this);
};