
const OType = {
    types: ['BOOLEAN', 'INTEGER', 'SHORT', 'LONG', 'FLOAT', 'DOUBLE', 'DATETIME',
        'STRING', 'BINARY', 'EMBEDDED', 'EMBEDDEDLIST', 'EMBEDDEDSET', 'EMBEDDEDMAP',
        'LINK', 'LINKLIST', 'LINKSET', 'LINKMAP', 'BYTE', 'TRANSIENT', 'DATE', 'CUSTOM',
        'DECIMAL', 'LINKBAG', 'ANY'],

    get: function (index) {
        return this.types[index];
    },

    getAsPrettyString: function (index) {
        var result = this.types[index];
        if (result) {
            var start = result.charAt(0);
            result = result.toLowerCase();
            result = start + result.slice(1);
        }
        return result;
    },

    contains: function (type) {
        return this.types.indexOf(type.toUpperCase()) > -1;
    },

    getIndexByValue: function (value) {
        return this.types.indexOf(value.toUpperCase());
    },

    size: function () {
        return this.types.length;
    },

    clone: function () {
        return mxUtils.clone(this);
    }
};

var OClass = function(name) {
    this.name = name;
    this.properties = [];
    this.propertiesForDelete = [];
    this.superClasses = [];
};

OClass.prototype.config = function (source) {
    this.name = source.name;
    this.properties = toOProperties(this.name, source.properties);
    this.superClasses = source.superClasses;

    function toOProperties(name, sourceProperties) {
        var properties = [];
        if (sourceProperties !== null && sourceProperties.length > 0) {
            for (var i = 0; i < sourceProperties.length; i++) {
                var property = new OProperty(name);
                property.config(sourceProperties[i]);
                properties.push(property);
            }
        }
        return properties;
    }
};

OClass.prototype.addOProperty = function (oProperty) {
    if (!this.containsProperty(oProperty)) {
        this.properties.push(oProperty);
        var delIndex = this.propertiesForDelete.indexOf(oProperty);
        if (delIndex > -1) {
            this.propertiesForDelete.splice(delIndex, 1);
        }
    }
};

OClass.prototype.removeProperty = function (oProperty) {
    var index = this.getPropertyIndex(oProperty);
    if (index > -1) {
        this.properties.splice(index, 1);
        index = this.propertiesForDelete.indexOf(oProperty);
        if (index === -1) this.propertiesForDelete.push(oProperty);
    }
};

OClass.prototype.addSuperClass = function (superClass) {
    if (this.superClasses.indexOf(superClass) === -1) {
        this.superClasses.push(superClass);
    }
};

OClass.prototype.deleteSuperClass = function (superClass) {
    var index = this.superClasses.indexOf(superClass);
    if (index > -1) {
       this.superClasses.splice(index, 1);
    }
};

OClass.prototype.containsProperty = function (property) {
    var contains = this.getPropertyIndex(property) > -1;
    console.log(property + ' contains: ' + contains);
    return contains;
};

OClass.prototype.getPropertyIndex = function (property) {
    var index = -1;
    if (property !== null && property.name !== null && property.type !== null) {
        var properties = this.properties;
        for(var i = 0; i < properties.length; i++) {
            if (properties[i].name === property.name && properties[i].type === property.type) {
                index = i;
            }
        }
    }
    return index;
};

OClass.prototype.toString = function () {
    return this.name;
};

OClass.prototype.clone = function () {
    return mxUtils.clone(this);
};

var OProperty = function (oClassName, name, type) {
    this.name = name;
    this.type = type;
    this.oClassName = oClassName;
    this.subClassProperty = false;
};

OProperty.prototype.config = function (source) {
    this.name = source.name;
    this.type = OType.contains(source.type) ? source.type : null;
};

OProperty.prototype.setType = function (type) {
    if (OType.contains(type)) {
        this.type = type;
    }
};

OProperty.prototype.setName = function (name) {
    this.name = name;
};

OProperty.prototype.setOClassName = function (oClassName) {
    this.oClassName = oClassName;
};

OProperty.prototype.isSubClassProperty = function () {
    return this.subClassProperty;
};

OProperty.prototype.toString = function () {
    return this.name;
};

OProperty.prototype.clone = function () {
    return mxUtils.clone(this);
};