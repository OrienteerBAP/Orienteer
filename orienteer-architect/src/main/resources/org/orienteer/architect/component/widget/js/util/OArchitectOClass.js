/**
 * Utility class which represents OClass from OrientDB in {@link OArchitectEditor}
 * @constructor
 */
var OArchitectOClass = function (name, cell) {
    OArchitectEditorObject.apply(this, []);
    this.name = null;
    this.properties = [];
    this.superClasses = [];
    this.subClasses = [];
    this.pageUrl = null;
    this.existsInDb = false;
    this.existsInEditor = true;
    this.removed = false;
    this.cell = null;

    this.databaseJson = null;

    if (name != null) this.setName(name);
    if (cell != null) this.setCell(cell);
};

OArchitectOClass.prototype = Object.create(OArchitectEditorObject.prototype);
OArchitectOClass.prototype.constructor = OArchitectOClass;

/**
 * Name of this class
 */
OArchitectOClass.prototype.name = null;

/**
 * Properties of this class. Contains Array with {@link OArchitectOProperty}
 */
OArchitectOClass.prototype.properties = [];

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
 * Contains previous state of this class for undo action
 */
OArchitectOClass.prototype.previousState = null;

/**
 * Config this instance from json which is respond from database
 * @param json - string which contains json data
 * @param cell
 */
OArchitectOClass.prototype.configFromJson = function (json, cell) {
    app.editor.disableConnection();
    this.name = json.name;
    this.pageUrl = json.pageUrl;
    this.databaseJson = json.databaseJson !== undefined ? json.databaseJson : null;
    setCell(this, cell);
    configProperties(this, json.properties);
    configClasses(this, json.superClasses, true);
    configClasses(this, json.subClasses, false);
    configExistsClassesLinks(this);
    this.setExistsInDb(json.existsInDb);
    this.updatePropertiesOrder();
    app.editor.enableConnection();

    function setCell(oClass, cell) {
        if (cell != null) {
            oClass.setCell(cell);
        } else if (oClass.cell !== null) {
            oClass.setCell(oClass.cell);
        }
    }

    function configProperties(oClass, jsonProperties) {
        var propertyCells = OArchitectUtil.getClassPropertiesCells(oClass);
        OArchitectUtil.forEach(jsonProperties, function (jsonProperty) {
            var property = oClass.getProperty(jsonProperty.name);
            var configured = false;
            if (property === null) {
                var cell = getPropertyCell(jsonProperty, propertyCells);
                if (cell === null) {
                    property = oClass.createProperty(jsonProperty.name, jsonProperty.type, null, jsonProperty.subClassProperty);
                } else {
                    property = cell.value instanceof OArchitectOProperty ? cell.value : new OArchitectOProperty();
                }
                property.configFromJson(oClass, jsonProperty, cell);
                configured = true;
            }
            if (!configured && property !== null) property.configFromJson(oClass, jsonProperty);
        });
    }

    function configClasses(oClass, jsonClasses, isSuperClasses) {
        OArchitectUtil.forEach(jsonClasses, function (jsonClassName) {
            var classCell = OArchitectUtil.getCellByClassName(jsonClassName);
            var configuredClass = null;
            if (classCell !== null) {
                configuredClass = classCell.value;
            } else {
                configuredClass = new OArchitectOClass();
                configuredClass.name = jsonClassName;
                configuredClass.existsInEditor = false;
            }
            if (isSuperClasses) oClass.addSuperClass(configuredClass);
            else oClass.addSubClass(configuredClass);
        });
    }

    function configExistsClassesLinks(oClass) {
        OArchitectUtil.forEach(OArchitectUtil.getAllClassesInEditor(), function (existsClass) {
            if (existsClass instanceof OArchitectOClass) {
                OArchitectUtil.forEach(existsClass.properties, function (property) {
                    if (isLinkToClass(property, oClass)) {
                        if (property.isSubClassProperty() && property.isSuperClassExistsInEditor()) {
                            property.linkedClass = oClass;
                            if (property.inverseProperty !== null) {
                                property.inverseProperty = oClass.getProperty(property.inverseProperty.name);
                            }
                        } else {
                            property.setLinkedClass(oClass);
                            if (property.inverseProperty !== null) {
                                property.setInverseProperty(oClass.getProperty(property.inverseProperty.name));
                            }
                        }
                    }
                });
                existsClass.setExistsInDb(existsClass.existsInDb);
            }
        });

        function isLinkToClass(property, oClass) {
            return property.linkedClass === oClass.name ||
                property.linkedClass instanceof OArchitectOClass && property.linkedClass.name === oClass.name;
        }
    }

    function getPropertyCell(propertyJson, propertyCells) {
        var result = null;
        OArchitectUtil.forEach(propertyCells, function (cell, trigger) {
            var name = cell.value instanceof OArchitectOProperty ? cell.value.name : JSON.parse(cell.value.json).name;
            if (name === propertyJson.name) {
                result = cell;
                trigger.stop = true;
            }
        });
        return result;
    }
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
            app.editor.graph.getModel().execute(new OClassChangeNameCommand(oClass, name));
            app.editor.graph.getModel().endUpdate();
        }
    });
};

/**
 * Set and create new cell for this class.
 * @param cell {@link mxCell}
 */
OArchitectOClass.prototype.setCell = function (cell) {
    if (cell !== null) {
        var graph = app.editor.graph;
        this.cell = cell;
        graph.getModel().setValue(this.cell, this);
    }
};


/**
 * Update class value in class cell
 */
OArchitectOClass.prototype.updateValueInCell = function (superClasses, subClasses) {
    this.setCell(this.cell);
    this.updatePropertiesOrder();
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
    if (name !== null && type !== null) {
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
        property.removed = true;
        this.removeCellFromProperty(property);
        this.notifySubClassesAboutChangesInProperty(property, true);
    }
};

/**
 * Add new superclass for this class.
 * Creates new connection between this class and new superclass
 * @param superClass {@link OArchitectOClass}
 * @param createEdge
 */
OArchitectOClass.prototype.addSuperClass = function (superClass, createEdge) {
    createEdge = createEdge == null ? true : createEdge;
    var index = this.getSuperClassIndex(superClass);
    var classForAdd = index !== -1 ? this.superClasses[index] : null;
    if (classForAdd != null) {
        if (!classForAdd.existsInEditor || classForAdd.existsInDb && classForAdd.removed) {
            this.superClasses.splice(index, 1);
            classForAdd = superClass;
        } else classForAdd = null;
    } else classForAdd = superClass;

    if (classForAdd !== null) {
        this.superClasses.push(classForAdd);
        this.addPropertiesFromSuperClass(classForAdd);
        classForAdd.addSubClass(this, createEdge);
    }

    if (this.cell !== null && superClass.cell !== null && this.getSuperClassIndex(superClass) !== -1 && createEdge) {
        OArchitectUtil.manageEdgesBetweenCells(this.cell, superClass.cell, true, true);
    }
};

/**
 * Add new subclass for this class.
 * Creates new connection between this class and new subclass
 * @param subClass {@link OArchitectOClass}
 * @param createEdge
 */
OArchitectOClass.prototype.addSubClass = function (subClass, createEdge) {
    createEdge = createEdge == null ? true : createEdge;
    var index = this.getSubClassIndex(subClass);
    var classForAdd = index > -1 ? this.subClasses[index] : null;
    if (classForAdd != null) {
        if (!classForAdd.existsInEditor) {
            this.subClasses.splice(index, 1);
            classForAdd = subClass;
        } else classForAdd = null;
    } else classForAdd = subClass;

    if (classForAdd !== null) {
        this.subClasses.push(classForAdd);
        classForAdd.addSuperClass(this, createEdge);
    }

    if (this.cell !== null && subClass.cell !== null && this.getSubClassIndex(subClass) !== -1 && createEdge) {
        OArchitectUtil.manageEdgesBetweenCells(subClass.cell, this.cell, true);
    }
};

/**
 * Remove superclass from this class
 * @param superClass {@link OArchitectOClass}
 */
OArchitectOClass.prototype.removeSuperClass = function (superClass) {
    var index = this.getSuperClassIndex(superClass);
    if (index > -1) {
        var unsavedInheritance =  OArchitectUtil.isUnsavedInheritance(this, superClass);
        this.removeSuperClassProperties(superClass, unsavedInheritance);
        if (!superClass.existsInDb || !this.existsInDb || unsavedInheritance) {
            this.superClasses.splice(index, 1);
            superClass.removeSubClass(this);
        }

        if (this.cell !== null && superClass.cell !== null) {
            OArchitectUtil.manageEdgesBetweenCells(this.cell, superClass.cell, false, true);
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
        if (!subClass.existsInDb || !this.existsInDb || OArchitectUtil.isUnsavedInheritance(subClass, this)) {
            this.subClasses.splice(index, 1);
            subClass.removeSuperClass(this);
        }
        if (this.cell !== null && subClass.cell !== null) {
            OArchitectUtil.manageEdgesBetweenCells(subClass.cell, this.cell, false, true);
        }
    }
};

/**
 * Create new {@link mxCell} in current class for given property
 * @param property {@link OArchitectOProperty}
 */
OArchitectOClass.prototype.createCellForProperty = function (property) {
    if (property !== null && !property.superClassExistsInEditor) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        if (property.cell === null) {
            property.setCell(OArchitectUtil.createOPropertyVertex(property));
        } else property.setCell(property.cell);
        graph.addCell(property.cell, this.cell);
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
OArchitectOClass.prototype.removeSuperClassProperties = function (superClass, unsavedInheritance) {
    var oClass = this;
    OArchitectUtil.forEach(superClass.properties, function (superClassProperty) {
        var index = oClass.removeSuperClassProperty(superClassProperty, unsavedInheritance);
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
OArchitectOClass.prototype.removeSuperClassProperty = function (superClassProperty, unsavedInheritance) {
    var superClass = superClassProperty.ownerClass;
    var index = -1;
    if (this.containsInSuperClassHierarchy(superClass)) {
        index = this.getPropertyIndex(superClassProperty);
        if (index > -1) {
            var property = this.properties[index];
            var notRemove = !unsavedInheritance && superClassProperty.ownerClass.removed && superClassProperty.ownerClass.existsInDb && property.isSubClassProperty() && property.cell == null;
            if (notRemove) notRemove = this.existsInDb;
            if (notRemove) {
                property.superClassExistsInEditor = false;
                this.createCellForProperty(property);
                if (property.linkedClass != null)
                    OArchitectUtil.manageEdgesBetweenCells(property.cell, property.linkedClass.cell, true);
            } else {
                this.properties.splice(index, 1);
            }
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
 * Update properties order in class
 */
OArchitectOClass.prototype.updatePropertiesOrder = function () {
    if (this.cell !== null) {
        changeOClassPropertiesOrder(this);
        app.editor.graph.constrainChildCells(this.cell);

        function changeOClassPropertiesOrder(oClass) {
            var properties = OArchitectUtil.getOrderValidProperties(oClass.properties);
            var children = oClass.cell.children;
            sortPropertiesByOrder(properties);
            var counter = 0;
            for (var i = 0; i < properties.length; i++) {
                var property = properties[i];
                if (!property.isRemoved()) {
                    var index = getCellIndex(children, properties[i]);
                    if (index > -1) {
                        var tmp = children[counter];
                        children[counter] = children[index];
                        children[index] = tmp;
                        counter++;
                    }
                }
            }
        }

        function sortPropertiesByOrder(properties) {
            properties.sort(function (prop1, prop2) {
                return prop1.getOrder() > prop2.getOrder();
            });
        }

        function getCellIndex(cells, property) {
            for (var i = 0; i < cells.length; i++) {
                var cell = cells[i];
                if (property.name === cell.value.name)
                    return i;
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
        var ownerClass = this;
        var edgesInDb = [];
        var edges = [];
        addClassesEdges(this.superClasses, this.cell, edgesInDb, edges);
        addClassesEdges(this.subClasses, this.cell, edgesInDb, edges);
        setPropertiesExistsInDb(this);

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
                if (oClass.cell !== null) {
                    var existsInDb = ownerClass.isSuperClassExistsInDb(oClass) || ownerClass.isSubClassExistsInDb(oClass);
                    var e = app.editor.graph.getEdgesBetween(oClass.cell, cell);
                    OArchitectUtil.forEach(e, function (edge) {
                        if (existsInDb || edge.value !== OArchitectConstants.UNSAVED_INHERITANCE) {
                            if (edge.value === OArchitectConstants.UNSAVED_INHERITANCE)
                                edge.setValue(OArchitectConstants.SAVED_INHERITANCE);
                            edgesInDb.push(edge);
                        } else edges.push(edge);
                    });
                }
            });
        }

        function setPropertiesExistsInDb(oClass) {
            OArchitectUtil.forEach(oClass.properties, function (property) {
                property.setExistsInDb(property.existsInDb);
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

OArchitectOClass.prototype.isSuperClassExistsInDb = function (superClass) {
    return this.isClassExistsInDb(superClass.name, true);
};

OArchitectOClass.prototype.isSubClassExistsInDb = function (subClass) {
    return this.isClassExistsInDb(subClass.name, false);
};

OArchitectOClass.prototype.isClassExistsInDb = function (className, isSuperClass) {
    var exists = false;
    if (this.databaseJson !== null) {
        OArchitectUtil.forEach(isSuperClass ? this.databaseJson.superClasses : this.databaseJson.subClasses, function (searchName, trigger) {
            if (className === searchName) {
                exists = true;
                trigger.stop = true;
            }
        });
    }
    return exists;
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
        if (key === 'cell' || key === 'databaseJson') {
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