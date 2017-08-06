
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

OClass.prototype.addOProperty = function(oProperty) {
    if (this.properties.indexOf(oProperty) === -1) {
        this.properties.push(oProperty);
    }
};

OClass.prototype.getProperties = function() {
    return this.properties;
};

OClass.prototype.getProperty = function(name) {
    return this.properties[name];
};

OClass.prototype.removeProperty = function(name) {
    delete this.properties[name];
};

OClass.prototype.addSuperClass = function (superClass) {
    if (this.superClasses.indexOf(superClass) === -1) {
        this.superClasses.push(superClass);
    }
};

OClass.prototype.removeSuperClass = function (superClass) {
    var index = this.superClasses.indexOf(superClass);
    if (index > -1) {
       this.superClasses.splice(index, 1);
    }
};

OClass.prototype.toString = function () {
    return this.name;
};

OClass.prototype.clone = function () {
    return mxUtils.clone(this);
};

var OProperty = function (oClassName, name, type) {
    this.oClassName = oClassName;
    this.name = name;
    this.type = type;
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

OProperty.prototype.toString = function () {
    return this.name;
};

OProperty.prototype.clone = function () {
    return mxUtils.clone(this);
};