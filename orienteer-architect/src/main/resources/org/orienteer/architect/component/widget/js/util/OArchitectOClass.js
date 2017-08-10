var OArchitectOClass = function(name, cell) {
    this.name = null;
    this.properties = [];
    this.propertiesForDelete = [];
    this.superClasses = [];
    this.subClasses = [];
    this.existsInDb = false;
    this.cell = null;
    this.configuredFromEditorConfig = false;

    if (name != null) this.setName(name);
    if (cell != null) this.setCell(cell);
};

OArchitectOClass.prototype.config = function (source) {
    this.name = source.name;
    this.properties = toOProperties(this, source.properties);
    this.superClasses = source.superClasses;

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

OArchitectOClass.prototype.configFromEditorConfig = function (classCell) {
    if (!this.configuredFromEditorConfig) {
        var currentClass = this;
        currentClass.configuredFromEditorConfig = true;
        configure();

        function configure() {
            var graph = app.editor.graph;
            var superClassesNames = currentClass.superClasses;
            var subClassesNames = currentClass.subClasses;
            currentClass.superClasses = [];
            currentClass.subClasses = [];
            currentClass.properties = [];
            currentClass.setCell(classCell);
            configureProperties(OArchitectUtil.getClassPropertiesCells(currentClass));
            configureClasses(graph, OArchitectUtil.getCellsByClassNames(superClassesNames), true);
            configureClasses(graph, OArchitectUtil.getCellsByClassNames(subClassesNames), false);
        }

        function configureClasses(graph, classCells, isSuperClasses) {
            OArchitectUtil.forEach(classCells, function (classCell) {
                var oClass = classCell.value;
                oClass.configFromEditorConfig(classCell);
                if (isSuperClasses) {
                    OArchitectConnector.connect(graph, currentClass.cell, oClass.cell);
                } else {
                    OArchitectConnector.connect(graph, oClass.cell, currentClass.cell);
                }
            });
        }

        function configureProperties(propertiesCells) {
            OArchitectUtil.forEach(propertiesCells, function (propertyCell) {
                var property = propertyCell.value;
                property.configFromEditorConfig(propertyCell);
                currentClass.properties.push(property);
            });
        }
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
                var cells = OArchitectUtil.getSubClassesCells(app.editor.graph, oClass);
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
                subClass.removeSuperClass(oldSuperClassName);
                subClass.addSuperClass(newSuperClassName);
                graph.getModel().setValue(cell, subClass);
            });
        } finally {
            graph.getModel().endUpdate();
        }
    }
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
    if (this.superClasses.indexOf(superClass) === -1) {
        this.superClasses.push(superClass);
        this.changeProperties(this, superClass.properties, true, false);
        superClass.addSubClass(this);
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

OArchitectOClass.prototype.addSubClass = function (subClass) {
    if (this.subClasses.indexOf(subClass) === -1) {
        this.subClasses.push(subClass);
        subClass.addSuperClass(this);
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
    applyChanges(oClass, cellsForUpdate, propertiesForUpdate);
    applyGraphChanges(oClass, cellsForUpdate, propertiesForUpdate);

    function applyChanges(oClass, cellsForUpdate, propertiesForUpdate) {
        changePropertiesForClass(oClass, cellsForUpdate, propertiesForUpdate, isSubClassProperty);
        OArchitectUtil.forEach(oClass.subClasses, function (subClass) {
            subClass.changeProperties(subClass, changedProperties, true, remove);
            changePropertiesForClass(subClass, cellsForUpdate, propertiesForUpdate, true);
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
                property.subClassProperty = isSubClass;
                propertiesForUpdate.push(property);
            }
        });
    }

    function applyGraphChanges(oClass, cellsForUpdate, propertiesForUpdate) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        try {
            OArchitectUtil.forEach(propertiesForUpdate, function (property) {
                graph.getModel().setValue(property.cell, property);
            });
            if (remove) {
                graph.removeCells(cellsForUpdate, true);
            } else {
                graph.addCells(cellsForUpdate, oClass.cell);
            }
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
        } else if (key === 'properties' || key === 'propertiesForDelete') {
            var properties = [];
            OArchitectUtil.forEach(value, function (property) {
                properties.push(property.toJson());
            });
            value = properties;
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
