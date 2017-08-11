var OArchitectOClass = function() {
    this.name = null;
    this.properties = [];
    this.propertiesForDelete = [];
    this.superClasses = [];
    this.subClasses = [];
    this.existsInDb = false;
    this.existsInEditor = true;
    this.cell = null;
    this.configuredFromEditorConfig = false;
};

OArchitectOClass.prototype.configFromJSON = function (json) {
    OArchitectOClassConfigurator.configOClassFromJSON(this, json);
};

OArchitectOClass.prototype.configFromEditorConfig = function (classCell) {
    OArchitectOClassConfigurator.configOClassFromEditorConfig(this, classCell);
};

OArchitectOClass.prototype.setName = function (name, callback) {
    var jsonObj = {
        existsClassName: name
    };
    var oClass = this;
    app.requestIfOClassExists(JSON.stringify(jsonObj), function (exists) {
        var msg = '';
        if (oClass.existsInDb || !exists) {
            if (!OArchitectUtil.existsOClassInGraph(app.editor.graph, name)) {
                oClass.name = name;
            } else if (name !== oClass.name) msg = localizer.classExistsInEditor;
        } else msg = localizer.classExistsInDatabase;
        if (callback != null) callback(oClass, msg);
    });
};

OArchitectOClass.prototype.setCell = function (cell) {
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

OArchitectOClass.prototype.createProperty = function (name, type, cell, subClass) {
    var property = null;
    if (name != null && type != null) {
        property = this.getProperty(name);
        if (property != null)
            throw new Error('OProperty with name: ' + name + ' already exists!');
        property = new OArchitectOProperty(this, name, type, cell);
        this.properties.push(property);
        this.changeProperties(this, [property], subClass, false);
    }
    return property;
};

OArchitectOClass.prototype.getOrCreateProperty = function (name, type, cell) {
    var property = null;
    if (name != null && type != null) {
        property = this.getProperty(name);
        if (property == null) {
            property = this.createProperty(name, type, cell);
        }
    }
    return property;
};

OArchitectOClass.prototype.removeProperty = function (oProperty, subClass) {
    var index = this.getPropertyIndex(oProperty);
    if (index > -1) {
        var property = this.properties[index];
        this.properties.splice(index, 1);
        if (this.existsInDb) {
            index = this.propertiesForDelete.indexOf(oProperty);
            if (index === -1) this.propertiesForDelete.push(oProperty);
        }
        this.changeProperties(this, [property], subClass, true);
    }
};

OArchitectOClass.prototype.addSuperClass = function (superClass) {
    var index = this.getSuperClassIndex(superClass);
    var classForAdd = index !== -1 ? this.superClasses[index] : null;
    if (classForAdd != null) {
        if (!classForAdd.existsInEditor) {
            this.superClasses.splice(index, 1);
            classForAdd = superClass;
        } else classForAdd = null;
    } else classForAdd = superClass;

    if (classForAdd != null) {
        this.superClasses.push(classForAdd);
        this.changeProperties(this, classForAdd.properties, true, false);
        classForAdd.addSubClass(this);
        if (this.cell != null && classForAdd.cell != null)
            OArchitectUtil.manageEdgesBetweenCells(this.cell, classForAdd.cell, true);
    }
};

OArchitectOClass.prototype.addSubClass = function (subClass) {
    var index = this.getSubClassIndex(subClass);
    var classForAdd = index > -1 ? this.subClasses[index] : null;
    if (classForAdd != null) {
        if (!classForAdd.existsInEditor) {
            this.subClasses.splice(index, 1);
            classForAdd = subClass;
        } else classForAdd = null;
    } else classForAdd = subClass;

    if (classForAdd != null) {
        this.subClasses.push(classForAdd);
        classForAdd.addSuperClass(this);
    }
};

OArchitectOClass.prototype.removeSuperClass = function (superClass) {
    var index = this.superClasses.indexOf(superClass);
    if (index > -1) {
        this.superClasses.splice(index, 1);
        this.changeProperties(this, superClass.properties, true, true);
        superClass.removeSubClass(this);
    }
};

OArchitectOClass.prototype.removeSubClass = function (subClass) {
    var index = this.subClasses.indexOf(subClass);
    if (index > -1) {
        this.subClasses.splice(index, 1);
        subClass.removeSuperClass(this);
    }
};

OArchitectOClass.prototype.changeProperties = function (oClass, changedProperties, isSubClassProperty, remove) {
    var cellsForUpdate = [];
    var propertiesForUpdate = [];
    if (remove && !isSubClassProperty) {
        addSuperClassCells(cellsForUpdate);
    }
    if (oClass.existsInEditor) {
        applyChanges(oClass, cellsForUpdate, propertiesForUpdate);
        applyGraphChanges(oClass, cellsForUpdate, propertiesForUpdate);
    }

    function applyChanges(oClass, cellsForUpdate, propertiesForUpdate) {
        changePropertiesForClass(oClass, cellsForUpdate, propertiesForUpdate, isSubClassProperty);
        OArchitectUtil.forEach(oClass.subClasses, function (subClass) {
            if (subClass.existsInEditor) {
                subClass.changeProperties(subClass, changedProperties, true, remove);
                changePropertiesForClass(subClass, cellsForUpdate, propertiesForUpdate, true);
            }
        });
    }

    function changePropertiesForClass(classForChanges, cellsForUpdate, propertiesForUpdate, isSubClass) {
        OArchitectUtil.forEach(changedProperties, function (changedProperty) {
            var property = classForChanges.getProperty(changedProperty.previousName);
            if (property == null) property = classForChanges.getProperty(changedProperty.name);

            if (remove && property != null) {
                cellsForUpdate.push(property.cell);
                if (isSubClass) classForChanges.removeProperty(property, isSubClass);
            } else if (!remove) {
                if (property == null) {
                    property = classForChanges.createProperty(changedProperty.name, changedProperty.type, null, isSubClass);
                } else if (property.name === changedProperty.previousName) {
                    property.setName(changedProperty.name);
                    property.setType(changedProperty.type);
                }
                if (property.cell == null) {
                    property.cell = OArchitectUtil.createOPropertyVertex(property);
                    cellsForUpdate.push(property.cell);
                }
                property.setLinkedClass(changedProperty.linkedClass);
                property.subClassProperty = isSubClass;
                propertiesForUpdate.push(property);
            }
        });
    }

    function applyGraphChanges(oClass, cellsForUpdate, propertiesForUpdate) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        try {
            if (remove) {
                graph.removeCells(cellsForUpdate, true);
            } else {
                graph.addCells(cellsForUpdate, oClass.cell);
            }
            OArchitectUtil.forEach(propertiesForUpdate, function (property) {
                graph.getModel().setValue(property.cell, property);
            });
        } finally {
            graph.getModel().endUpdate();
        }
    }

    function addSuperClassCells(cellsForUpdate) {
        OArchitectUtil.forEach(changedProperties, function (prop) {
            cellsForUpdate.push(prop.cell);
        });
    }
};

OArchitectOClass.prototype.isSubClass = function () {
    return this.superClasses.length > 0;
};

OArchitectOClass.prototype.containsSuperClass = function (superClass) {
    return this.superClasses.indexOf(superClass) > -1;
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

OArchitectOClass.prototype.getSuperClassIndex = function (superClass) {
    return this.getClassIndex(this.superClasses, superClass);
};

OArchitectOClass.prototype.getSubClassIndex = function (subClass) {
    return this.getClassIndex(this.subClasses, subClass);
};

OArchitectOClass.prototype.getClassIndex = function (classes, searchClass) {
    var index = -1;
    if (classes != null && classes.length > 0 && searchClass != null && searchClass.name != null) {
        for (var i = 0; i < classes.length; i++) {
            if (classes[i].name === searchClass.name) {
                index = i;
                break;
            }
        }
    }
    return index;
};

OArchitectOClass.prototype.toString = function () {
    return this.name;
};

OArchitectOClass.prototype.toJson = function () {
    function jsonFilter(key, value) {
        if (key === 'cell') {
            value = undefined;
        } else if (key === 'superClasses' || key === 'subClasses') {
            var classes = [];
            OArchitectUtil.forEach(value, function (oClass) {
                classes.push(oClass.name);
            });
            value = classes;
        } else if (key === 'ownerClass' || key === 'linkedClass') {
            value = value != null ? value.name : null;
        }

        return value;
    }
    return JSON.stringify(this, jsonFilter);
};

OArchitectOClass.prototype.toEditorConfigObject = function () {
    var result = new OArchitectOClass();
    result.name = this.name;
    result.properties = toEditorProperties(this.properties);
    result.propertiesForDelete = toEditorProperties(this.propertiesForDelete);
    result.superClasses = toEditorClasses(this.superClasses);
    result.subClasses = toEditorClasses(this.subClasses);
    result.existsInDb = this.existsInDb;

    function toEditorProperties(properties) {
        var editorProperties = [];
        OArchitectUtil.forEach(properties, function (property) {
            editorProperties.push(property);
        });
        return editorProperties;
    }

    function toEditorClasses(classes) {
        var editorClasses = [];
        OArchitectUtil.forEach(classes, function (oClass) {
            editorClasses.push(oClass.name);
        });
        return editorClasses;
    }
    return result;
};
