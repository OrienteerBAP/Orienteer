var OArchitectOClass = function(name) {
    this.name = null;
    this.properties = [];
    this.propertiesForDelete = [];
    this.superClassesNames = [];
    this.existsInDb = false;

    if (name != null) this.setName(name);
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

OArchitectOClass.prototype.setName = function (name, callback) {
    var jsonObj = {
        existsClassName: name
    };
    var oClass = this;
    app.requestIfOClassExists(JSON.stringify(jsonObj), function (exists) {
        var msg = '';
        if (!exists) {
            if (!OArchitectUtil.existsOClassInGraph(app.editor.graph, name)) {
                var cells = OArchitectUtil.getOClassSubClassesCells(app.editor.graph, oClass);
                renameSubClasses(app.editor.graph, oClass.name, name, cells);
                oClass.name = name;
            } else msg = localizer.classExistsInEditor;
        } else msg = localizer.classExistsInDatabase;
        if (callback != null) callback(oClass, msg);
    });

    function renameSubClasses(graph, oldSuperClassName, newSuperClassName, cells) {
        graph.getModel().beginUpdate();
        try {
            OArchitectUtil.forEach(cells, function (cell) {
                var subClass = cell.value;
                subClass.removeSuperClassName(oldSuperClassName);
                subClass.addSuperClassName(newSuperClassName);
                graph.getModel().setValue(cell, subClass);
            });
        } finally {
            graph.getModel().endUpdate();
        }
    }
};

OArchitectOClass.prototype.createProperty = function (name, type) {
    var property = this.getProperty(name);
    if (property != null)
        throw new Error('OProperty with name: ' + name + ' already exists!');
    property = new OArchitectOProperty(this.name, name, type);
    this.properties.push(property);
    return property;
};

OArchitectOClass.prototype.putProperty = function (oProperty) {
    var index = this.getPropertyIndex(oProperty);
    var oCass = this;
    if (index > -1) {
        this.properties[index] = oProperty;
    } else addProperty(oProperty);

    OArchitectUtil.changeAllSubProperties(app.editor.graph, oCass);

    function addProperty(oProperty) {
        if (!oCass.containsProperty(oProperty)) {
            oCass.properties.push(oProperty);
            var delIndex = oCass.propertiesForDelete.indexOf(oProperty);
            if (delIndex > -1) {
                oCass.propertiesForDelete.splice(delIndex, 1);
            }
        }
    }
};

OArchitectOClass.prototype.removeProperty = function (oProperty) {
    var index = this.getPropertyIndex(oProperty);
    if (index > -1) {
        this.properties.splice(index, 1);
        index = this.propertiesForDelete.indexOf(oProperty);
        if (index === -1) this.propertiesForDelete.push(oProperty);
        OArchitectUtil.changeAllSubProperties(app.editor.graph, this);
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

OArchitectOClass.prototype.getProperty = function (name) {
    var property = null;
    if (name != null) {
        for (var i = 0; i < this.properties.length; i++) {
            if (this.properties[i].name === name) {
                property = this.properties[i];
                break;
            }
        }
    }
    return property;
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