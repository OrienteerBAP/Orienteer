/**
 * Utility class which represents OClass from OrientDB in {@link OArchitectEditor}
 * @constructor
 */
var OArchitectOClass = function (name, cell) {
    this.name = null;
    this.properties = [];
    this.propertiesForDelete = [];
    this.superClasses = [];
    this.subClasses = [];
    this.pageUrl = null;
    this.existsInDb = false;
    this.existsInEditor = true;
    this.removed = false;
    this.cell = null;

    this.previousState = null;
    this.nextState = null;

    this.databaseJson = null;

    if (name != null) this.setName(name);
    if (cell != null) this.setCell(cell);
};

/**
 * Name of this class
 */
OArchitectOClass.prototype.name = null;

/**
 * Properties of this class. Contains Array with {@link OArchitectOProperty}
 */
OArchitectOClass.prototype.properties = [];

/**
 * NOT SUPPORTED. IN DEVELOPMENT.
 * Properties for delete from OrientDB of this class. Contains Array with {@link OArchitectOProperty}
 */
OArchitectOClass.prototype.propertiesForDelete = [];

/**
 * Superclasses of this class. Contains Array with {@link OArchitectOClass}
 */
OArchitectOClass.prototype.superClasses = [];

/**
 * Subclasses of this class. Contains Array with {@link OArchitectOClass}
 */
OArchitectOClass.prototype.subClasses = [];

/**
 * String URL to class page in Orienteer
 */
OArchitectOClass.prototype.pageUrl = null;

/**
 * Boolean indicates if this class exists in database. Default is false
 */
OArchitectOClass.prototype.existsInDb = false;

/**
 * Boolean indicates if this class exists in editor. Default is true
 */
OArchitectOClass.prototype.existsInEditor = true;

/**
 * Boolean indicates if this class have been removed from editor.
 */
OArchitectOClass.prototype.removed = false;

/**
 * {@link mxCell} which contains this class
 */
OArchitectOClass.prototype.cell = null;

/**
 * Boolean indicates if this class have been configured from cell (Undo action or from editor ml config)
 */
OArchitectOClass.prototype.configuredFromCell = false;

/**
 * Contains previous state of this class for undo action
 */
OArchitectOClass.prototype.previousState = null;

/**
 * Config this instance from json which is respond from database
 * @param json - string which contains json data
 */
OArchitectOClass.prototype.configFromJson = function (json) {
    OArchitectOClassConfigurator.configOClassFromJson(this, json);
};

/**
 * INTERNAL FUNCTION. DON'T CALL IT.
 * Config this class from cell which is saved in xml config.
 * @param classCell - {@link mxCell} which will be used to config
 */
OArchitectOClass.prototype.configFromCell = function (classCell) {
    OArchitectOClassConfigurator.configOClassFromCell(this, classCell);
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
                setName(oClass);
            } else if (name !== oClass.name) msg = localizer.classExistsInEditor;
        } else msg = localizer.classExistsInDatabase;
        if (callback != null) callback(oClass, msg);

        function setName(oClass) {
            app.editor.graph.getModel().beginUpdate();
            oClass.saveState(true, true);
            oClass.name = name;
            oClass.updateValueInCell(true, true);
            app.editor.graph.getModel().endUpdate();
        }
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
 * Save current state of class for undo action
 */
OArchitectOClass.prototype.saveState = function (superClasses, subClasses) {
    if (this.name !== null) {
        this.previousState = this.toEditorConfigObject();
        console.warn('save oclass state, previous state: ', this.previousState);
        if (superClasses) saveState(this.superClasses);
        if (subClasses) saveState(this.subClasses);
    } else this.previousState = null;

    function saveState(classes) {
        OArchitectUtil.forEach(classes, function (oClass) {
            oClass.saveState();
        });
    }
};

/**
 * Update class value in class cell
 */
OArchitectOClass.prototype.updateValueInCell = function (superClasses, subClasses) {
    this.setCell(this.cell);
    this.changePropertiesOrder();
    if (superClasses) updateValueInCell(this.superClasses);
    if (subClasses) updateValueInCell(this.subClasses);

    function updateValueInCell(classes) {
        OArchitectUtil.forEach(classes, function (oClass) {
            oClass.updateValueInCell();
        });
    }
};

/**
 * Create new {@link OArchitectOProperty} for this class and save it in this class
 * @param name - string name of property
 * @param type - string type of property
 * @param cell - {@link mxCell} which contains property
 * @param subClass - boolean true if creates subclass property
 * @param superClassExistsInEditor
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
        property.removed = true;
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
        if (!classForAdd.existsInEditor || classForAdd.existsInDb && classForAdd.removed) {
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
    var index = this.getSuperClassIndex(superClass);
    if (index > -1) {
        this.removeSuperClassProperties(superClass);
        if (!superClass.existsInDb || !this.existsInDb) {
            this.superClasses.splice(index, 1);
            superClass.removeSubClass(this);
        }
    }
};

/**
 * Remove subclass from this class
 * @param subClass {@link OArchitectOClass}
 */
OArchitectOClass.prototype.removeSubClass = function (subClass) {
    var index = this.getSubClassIndex(subClass);
    if (index > -1) {
        if (!subClass.existsInDb || !this.existsInDb) {
            this.subClasses.splice(index, 1);
            subClass.removeSuperClass(this);
        }
    }
};

/**
 * Create new {@link mxCell} in current class for given property
 * @param property {@link OArchitectOProperty}
 */
OArchitectOClass.prototype.createCellForProperty = function (property) {
    if (property != null && !property.superClassExistsInEditor) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        try {
            property.setCell(OArchitectUtil.createOPropertyVertex(property));
            graph.addCell(property.cell, this.cell);
        } finally {
            graph.getModel().endUpdate();
        }
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
        var property = oClass.updateSubClassPropertyFromTemplate(superClassProperty);
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
            var property = oClass.properties[index];
            if (property == null)
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
    var superClass = superClassProperty.ownerClass;
    var index = -1;
    if (this.containsInSuperClassHierarchy(superClass)) {
        index = this.getPropertyIndex(superClassProperty);
        if (index > -1) {
            var property = this.properties[index];
            var notRemove = superClassProperty.ownerClass.removed && superClassProperty.ownerClass.existsInDb && property.isSubClassProperty() && property.cell == null;
            if (notRemove) notRemove = this.existsInDb;
            if (notRemove) {
                property.superClassExistsInEditor = false;
                this.createCellForProperty(property);
                if (property.linkedClass != null)
                    OArchitectUtil.manageEdgesBetweenCells(property.cell, property.linkedClass.cell, true);
            } else this.properties.splice(index, 1);
        }
    }
    return index;
};

/**
 * Create subclass property from template property. If property with given name already exists in this class it's will be
 * changed and it's cell will be removed
 * @param templateProperty - {@link OArchitectOProperty} - template
 * @returns {@link OArchitectOProperty} if was be created new property or null if property with given name already exists in class
 */
OArchitectOClass.prototype.updateSubClassPropertyFromTemplate = function (templateProperty) {
    var property = this.getProperty(templateProperty.name);
    var needForReturn = true;
    if (property === null) property = this.getProperty(templateProperty.previousName);
    if (property !== null) {
        property.setName(templateProperty.name);
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
    console.warn(this.name, ' - template inverse property: ', templateProperty.inverseProperty);
    property.inversePropertyEnable = templateProperty.inversePropertyEnable;
    property.inverseProperty = templateProperty.inverseProperty;
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
            property = subClass.updateSubClassPropertyFromTemplate(templateProperty);
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
        if (subClass instanceof OArchitectOClass) {
            subClass.saveState();
            action(subClass);
            subClass.updateValueInCell();
            subClass.applyActionToAllSubClasses(action);
        }
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


OArchitectOClass.prototype.containsInSuperClassHierarchy = function (oClass) {
    var contains = this.superClasses.indexOf(oClass) > -1;
    if (!contains) {
        OArchitectUtil.forEach(this.superClasses, function (superClass, trigger) {
            contains = superClass.containsInSuperClassHierarchy(oClass);
            if (contains) trigger.stop = true;
        });
    }
    return contains;
};


OArchitectOClass.prototype.getAvailableInverseProperties = function () {
    var result = [];
    OArchitectUtil.forEach(this.properties, function (property) {
        if (property.isLink()) {
            result.push(property);
        }
    });
    return result;
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

/**
 * Change properties order in class
 * @param event - event for change properties order
 * if event === mxEvent.CELLS_MOVED order changes from class cell otherwise order changes from database class config
 */
OArchitectOClass.prototype.changePropertiesOrder = function (event) {
    if (this.cell !== null) {
        changeOrder(this);

        function changeOrder(oClass) {
            if (event === mxEvent.CELLS_MOVED) {
                changeMovePropertiesOrder(oClass);
            } else changeOClassPropertiesOrder(oClass);
            app.editor.graph.constrainChildCells(oClass.cell);
        }

        function changeMovePropertiesOrder(oClass) {
            var orderStep = oClass.getPropertyOrderStep();
            var properties = OArchitectUtil.getOrderValidProperties(oClass.properties);
            var order = OArchitectUtil.getPropertyWithMinOrder(properties);
            var children = oClass.cell.children;
            for (var i = 0; i < children.length; i++) {
                var index = getPropertyIndex(children[i].value, properties);
                properties[index].setOrder(order);
                order += orderStep;
            }
        }

        function changeOClassPropertiesOrder(oClass) {
            var properties = OArchitectUtil.getOrderValidProperties(oClass.properties);
            var children = oClass.cell.children;
            sortPropertiesByOrder(properties);
            for (var i = 0; i < properties.length; i++) {
                var index = getCellIndex(children, properties[i]);
                var tmp = children[i];
                children[i] = children[index];
                children[index] = tmp;
            }
        }

        function sortPropertiesByOrder(properties) {
            properties.sort(function (prop1, prop2) {
                return prop1.getOrder() > prop2.getOrder();
            });
        }

        function getCellIndex(cells, property) {
            for (var i = 0; i < cells.length; i++) {
                if (property.name === cells[i].value.name)
                    return i;
            }
            return -1;
        }

        function getPropertyIndex(property, properties) {
            for (var i = 0; i < properties.length; i++) {
                if (properties[i].name === property.name) {
                    return i;
                }
            }
            return -1;
        }
    }
};

OArchitectOClass.prototype.getPropertyOrderStep = function () {
    return 10;
};

/**
 * @return boolean true if class removed from editor
 */
OArchitectOClass.prototype.isRemoved = function () {
    return this.removed;
};

OArchitectOClass.prototype.toString = function () {
    return this.name;
};

OArchitectOClass.prototype.setExistsInDb = function (existsInDb) {
    this.existsInDb = existsInDb;
    if (this.cell != null) {
        var edgesInDb = [];
        var edges = [];
        addClassesEdges(this.superClasses, this.cell, edgesInDb, edges);
        addClassesEdges(this.subClasses, this.cell, edgesInDb, edges);


        if (this.existsInDb) {
            app.editor.graph.setCellStyle(OArchitectConstants.OCLASS_EXISTS_STYLE, [this.cell]);
            app.editor.graph.setCellStyle(OArchitectConstants.OCLASS_EXISTS_CONNECTION_STYLE, edgesInDb);
            app.editor.graph.setCellStyle(OArchitectConstants.OCLASS_CONNECTION_STYLE, edges);
        } else {
            app.editor.graph.setCellStyle(OArchitectConstants.OCLASS_STYLE, [this.cell]);
            app.editor.graph.setCellStyle(OArchitectConstants.OCLASS_CONNECTION_STYLE, edgesInDb);
        }

        function addClassesEdges(classes, cell, edgesInDb, edges) {
            OArchitectUtil.forEach(classes, function (oClass) {
                if (oClass.cell != null) {
                    var e = app.editor.graph.getEdgesBetween(oClass.cell, cell);
                    OArchitectUtil.forEach(e, function (edge) {
                        if (edge.value !== OArchitectConstants.UNSAVED_INHERITANCE) {
                            edgesInDb.push(edge);
                        } else edges.push(edge);
                    });
                }
            });
        }
    }
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

OArchitectOClass.prototype.setDatabaseJson = function (json) {
    var oClass = this;
    this.databaseJson = json;
    OArchitectUtil.forEach(json.properties, function (jsonProperty) {
        oClass.getProperty(jsonProperty.name).databaseJson = jsonProperty;
    });
};

/**
 * Convert current class to json string
 * @returns json string
 */
OArchitectOClass.prototype.toJson = function () {
    function jsonFilter(key, value) {
        if (key === 'cell' || key === 'previousState' || key === 'nextState' || key === 'databaseJson') {
            value = undefined;
        } else if (key === 'superClasses' || key === 'subClasses') {
            var classes = [];
            OArchitectUtil.forEach(value, function (oClass) {
                classes.push(oClass.name);
            });
            value = classes;
        } else if (key === 'ownerClass' || key === 'linkedClass') {
            if (value != null) {
                if (value instanceof OArchitectOClass) {
                    value = value.name;
                }
            }
        } else if (key === 'inverseProperty') {
            if (value !== null) {
                var prop = new OArchitectOProperty();
                if (value instanceof OArchitectOProperty) {
                    prop.name = value.name;
                    prop.type = value.type;
                    prop.ownerClass = value.ownerClass instanceof OArchitectOClass ? value.ownerClass.name : null;
                } else prop.name = value;
                value = prop;
            }
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
    result.pageUrl = this.pageUrl;
    result.previousState = this.previousState;
    function toEditorProperties(properties) {
        var editorProperties = [];
        OArchitectUtil.forEach(properties, function (property) {
            editorProperties.push(property.toEditorConfigObject());
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