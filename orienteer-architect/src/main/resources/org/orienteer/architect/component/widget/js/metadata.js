
var OClass = function(name) {
  this.name = name;
  this.properties = [];
  this.superClasses = [];
};

OClass.prototype.addProperty = function(name, type, value) {
    this.properties[name] = new Object();
    this.properties[name].type = type;
    this.properties[name].value = value;
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
    return JSON.stringify(this);
};

OClass.prototype.clone = function () {
    return mxUtils.clone(this);
};