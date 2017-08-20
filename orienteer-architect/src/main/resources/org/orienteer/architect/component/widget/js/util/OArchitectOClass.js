/**
 * Utility class which represents OClass from OrientDB in {@link OArchitectEditor}
 * @constructor
 */
var OArchitectOClass = function() {
    this.name = null;
    this.properties = [];
    this.propertiesForDelete = [];
    this.superClasses = [];
    this.subClasses = [];
    this.pageUrl = null;
    this.existsInDb = false;
    this.existsInEditor = true;
    this.cell = null;
    this.configuredFromEditorConfig = false;
};

/**
 * boolean true if class has been removed from editor
 */
OArchitectOClass.prototype.removed = false;

/**
 * Config this instance from json which is respond from database
 * @param json - string which contains json data
 */
OArchitectOClass.prototype.configFromDatabase = function (json) {
    OArchitectOClassConfigurator.configOClassFromDatabase(this, json);
};

/**
 * INTERNAL FUNCTION. DON'T CALL IT.
 * Config this class from cell which is saved in xml config.
 * @param classCell - {@link mxCell} which will be used to config
 */
OArchitectOClass.prototype.configFromEditorConfig = function (classCell) {
    OArchitectOClassConfigurator.configOClassFromEditorConfig(this, classCell);
};

/**
 * Set name of this class.
 * Checks if class with current name exists in database or exists in editor.
 * @param name - sting which contains new name
 * @param callback - function which will be call after all checks
 *                 - params of function this class and optional message
 */
OArchitectOClass.prototype.setName = function (name, callback) {
    var jsonObj = {
        existsClassName: name
    };
    var oClass = this;
    app.requestIfOClassExists(JSON.stringify(jsonObj), function (exists) {
        var msg = null;
        if (oClass.existsInDb || !exists) {
            if (!OArchitectUtil.existsOClassInGraph(app.editor.graph, name)) {
                oClass.name = name;
            } else if (name !== oClass.name) msg = localizer.classExistsInEditor;
        } else msg = localizer.classExistsInDatabase;
        if (callback != null) callback(oClass, msg);
    });
};

/**
 * Set and create new cell for this class.
 * @param cell {@link mxCell}
 */
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

/**
 * Create new {@link OArchitectOProperty} for this class and save it in this class
 * @param name - string name of property
 * @param type - string type of property
 * @param cell - {@link mxCell} which contains property
 * @param subClass - boolean true if creates subclass property
 * @returns {@link OArchitectOProperty}
 * @throws {@link Error} if property with given name already exists
 */
OArchitectOClass.prototype.createProperty = function (name, type, cell, subClass, superClassExistsInEditor) {
    var property = null;
    if (name != null && type != null) {
        property = this.getProperty(name);
        if (property != null)
            throw new Error('OProperty with name: ' + name + ' already exists!');
        property = new OArchitectOProperty(this, name, type, cell);
        property.subClassProperty = subClass;
        property.superClassExistsInEditor = superClassExistsInEditor;
        this.properties.push(property);
        this.createCellForProperty(property);
        this.notifySubClassesAboutChangesInProperty(property);
    }
    return property;
};

/**
 * Remove {@link OArchitectOProperty} from this class.
 * @param oProperty - {@link OArchitectOProperty} for remove
 */
OArchitectOClass.prototype.removeProperty = function (oProperty) {
    var index = this.getPropertyIndex(oProperty);
    if (index > -1) {
        var property = this.properties[index];
        this.properties.splice(index, 1);
        if (this.existsInDb) {
            index = this.propertiesForDelete.indexOf(oProperty);
            if (index === -1) this.propertiesForDelete.push(oProperty);
        }
        this.removeCellFromProperty(property);
        this.notifySubClassesAboutChangesInProperty(property, true);
    }
};

/**
 * Add new superclass for this class.
 * Creates new connection between this class and new superclass
 * @param superClass {@link OArchitectOClass}
 */
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
        this.addPropertiesFromSuperClass(classForAdd);
        classForAdd.addSubClass(this);
        if (this.cell != null && classForAdd.cell != null)
            OArchitectUtil.manageEdgesBetweenCells(this.cell, classForAdd.cell, true);
    }
};

/**
 * Add new subclass for this class.
 * Creates new connection between this class and new subclass
 * @param subClass {@link OArchitectOClass}
 */
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
        if (this.cell != null && classForAdd.cell != null)
            OArchitectUtil.manageEdgesBetweenCells(classForAdd.cell, this.cell, true);
    }
};

/**
 * Remove superclass from this class
 * @param superClass {@link OArchitectOClass}
 */
OArchitectOClass.prototype.removeSuperClass = function (superClass) {
    var index = this.superClasses.indexOf(superClass);
    if (index > -1) {
        this.superClasses.splice(index, 1);
        this.removeSuperClassProperties(superClass);
        superClass.removeSubClass(this);
    }
};

/**
 * Remove subclass from this class
 * @param subClass {@link OArchitectOClass}
 */
OArchitectOClass.prototype.removeSubClass = function (subClass) {
    var index = this.subClasses.indexOf(subClass);
    if (index > -1) {
        this.subClasses.splice(index, 1);
        subClass.removeSuperClass(this);
    }
};

/**
 * Create new {@link mxCell} in current class for given property
 * @param property {@link OArchitectOProperty}
 */
OArchitectOClass.prototype.createCellForProperty = function (property) {
    var graph = app.editor.graph;
    graph.getModel().beginUpdate();
    try {
        property.setCell(OArchitectUtil.createOPropertyVertex(property));
        graph.addCell(property.cell, this.cell);
    } finally {
        graph.getModel().endUpdate();
    }
};

/**
 * Remove {@link mxCell} from given property
 * @param property {@link OArchitectOProperty}
 */
OArchitectOClass.prototype.removeCellFromProperty = function (property) {
    if (property.cell != null) {
        OArchitectUtil.removeCell(property.cell, true);
    }
};

/**
 * Add properties from given superclass to this class and subclasses
 * @param superClass - {@link OArchitectOClass}
 */
OArchitectOClass.prototype.addPropertiesFromSuperClass = function (superClass) {
    var oClass = this;
    OArchitectUtil.forEach(superClass.properties, function (superClassProperty) {
        var property = oClass.createSubClassPropertyFromTemplate(superClassProperty);
        if (property !== null) {
            oClass.properties.push(property);
            oClass.notifySubClassesAboutChangesInProperty(property);
        }
    });
};

/**
 * Remove superclass properties from this class and subclasses.
 * If superclass exists in database properties will not be removed from this class and subclasses instead will be
 * create new cells for this properties in this class
 * @param superClass - {@link OArchitectOClass}
 */
OArchitectOClass.prototype.removeSuperClassProperties = function (superClass) {
    var oClass = this;
    OArchitectUtil.forEach(superClass.properties, function (superClassProperty) {
        var index = oClass.removeSuperClassProperty(superClassProperty);
        if (index > -1) {
            oClass.notifySubClassesAboutChangesInProperty(superClassProperty, true);
        }
    });
};

/**
 * Remove superclass property from this class.
 * If superclass exists in database property will not be removed from this class instead new cell for this property
 * will be created
 * @param superClassProperty {@link OArchitectOProperty} superclass property
 * @returns {number} index of property in properties array or -1 if superClassProperty don't exists in this class
 */
OArchitectOClass.prototype.removeSuperClassProperty = function (superClassProperty) {
    var index = this.getPropertyIndex(superClassProperty);
    if (index > -1) {
        var property = this.properties[index];
        var notRemove = superClassProperty.ownerClass.removed && superClassProperty.ownerClass.existsInDb && property.isSubClassProperty() && property.cell == null;
        if (notRemove) notRemove = this.existsInDb;
        if (notRemove) {
            property.superClassExistsInEditor = false;
            this.createCellForProperty(property);
        } else this.properties.splice(index, 1);
    }
    return index;
};

/**
 * Create subclass property from template property. If property with given name already exists in this class it's will be
 * changed and it's cell will be removed
 * @param templateProperty - {@link OArchitectOProperty} - template
 * @returns {@link OArchitectOProperty} if was be created new property or null if property with given name already exists in class
 */
OArchitectOClass.prototype.createSubClassPropertyFromTemplate = function (templateProperty) {
    var property = this.getProperty(templateProperty.name);
    var needForReturn = true;
    if (property === null) property = this.getProperty(templateProperty.previousName);
    if (property !== null) {
        property.setType(templateProperty.type);
        needForReturn = false;
        if (property.cell != null) {
            OArchitectUtil.removeCell(property.cell, true);
            property.cell = null;
        }
    } else {
        property = new OArchitectOProperty(this, templateProperty.name, templateProperty.type);
    }
    property.subClassProperty = true;
    property.linkedClass = templateProperty.linkedClass;
    property.superClassExistsInEditor = templateProperty.ownerClass.cell !== null;
    return needForReturn ? property : null;
};

/**
 * Notify subclasses about changes in property
 * @param templateProperty - {@link OArchitectOProperty} that was changed
 * @param remove - boolean true if need to delete templateProperty from subclasses
 */
OArchitectOClass.prototype.notifySubClassesAboutChangesInProperty = function (templateProperty, remove) {
    var action = function (subClass) {
        var property = null;
        if (remove) {
            subClass.removeSuperClassProperty(templateProperty);
        } else {
            property = subClass.createSubClassPropertyFromTemplate(templateProperty);
            if (property !== null) subClass.properties.push(property);
        }
    };
    this.applyActionToAllSubClasses(action);
};

/**
 * Apply action for all subclasses
 * @param action - Function which will be executed on every subclass
 */
OArchitectOClass.prototype.applyActionToAllSubClasses = function (action) {
    OArchitectUtil.forEach(this.subClasses, function (subClass) {
        action(subClass);
        subClass.applyActionToAllSubClasses(action);
    });
};

/**
 * @returns boolean - true if thic class is subclass
 */
OArchitectOClass.prototype.isSubClass = function () {
    return this.superClasses.length > 0;
};

/**
 * @param superClass - {@link OArchitectOClass}
 * @returns boolean - true if this class contains in superClasses superClass
 */
OArchitectOClass.prototype.containsSuperClass = function (superClass) {
    return this.superClasses.indexOf(superClass) > -1;
};

/**
 * @param name - string name of property
 * @returns {@link OArchitectOProperty} with given name or null
 */
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

/**
 * @param property - {@link OArchitectOProperty} which will be search
 * @returns number - index of property in properties array of this class
 */
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

/**
 * @param superClass - {@link OArchitectOClass} for search
 * @returns number - index of class in superclasses array of this class
 */
OArchitectOClass.prototype.getSuperClassIndex = function (superClass) {
    return this.getClassIndex(this.superClasses, superClass);
};

/**
 * @param subClass - {@link OArchitectOClass} for search
 * @returns number - index of class in subclasses array of this class
 */
OArchitectOClass.prototype.getSubClassIndex = function (subClass) {
    return this.getClassIndex(this.subClasses, subClass);
};

/**
 * @param classes - array of {@link OArchitectOClass}
 * @param searchClass - {@link OArchitectOClass}
 * @returns number - index of searchClass in classes
 */
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

/**
 * Checks if given json class is equals with current {@link OArchitectOClass} instance
 * @param jsonClass - json class
 * @returns boolean - true if equals
 */
OArchitectOClass.prototype.equalsWithJsonClass = function (jsonClass) {
    var equals = true;
    if (equals && jsonClass.name !== this.name) equals = false;
    if (equals && jsonClass.superClasses.length !== this.superClasses.length) equals = false;
    if (equals && jsonClass.subClasses.length !== this.subClasses.length) equals = false;
    if (equals && jsonClass.properties.length !== this.properties.length) equals = false;
    if (equals && jsonClass.pageUrl !== this.pageUrl) equals = false;
    if (equals) {
        equals = checkProperties(jsonClass.properties, this.properties);
        if (equals) equals = checkClassNames(jsonClass.superClasses, OArchitectUtil.toClassNames(this.superClasses));
        if (equals) equals = checkClassNames(jsonClass.subClasses, OArchitectUtil.toClassNames(this.subClasses));
    }

    function checkClassNames(jsonClassNames, oClassClassesNames) {
        var equals = true;
        jsonClassNames.sort();
        oClassClassesNames.sort();
        OArchitectUtil.forEach(jsonClassNames, function (jsonClassName, trigger) {
            if (oClassClassesNames.indexOf(jsonClassName) === -1) {
                equals = false;
                trigger.stop = true;
            }
        });
        return equals;
    }

    function checkProperties(jsonProperties, oClassProperties) {
        var equals = false;
        OArchitectUtil.forEach(jsonProperties, function (jsonProperty, trigger) {
            equals = false;
            OArchitectUtil.forEach(oClassProperties, function (property, trigger) {
                if (jsonProperty.name === property.name) {
                    equals = property.equalsWithJsonProperty(jsonProperty);
                    trigger.stop = true;
                }
            });

            if (equals === false) {
                trigger.stop = true;
            }
        });

        return equals;
    }

    return equals;
};

/**
 * Convert current class to json string
 * @returns json string
 */
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

/**
 * @returns {@link OArchitectOClass} instance of this class which can be converted to editor xml config
 */
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
