/**
 * Utility class which represents OProperty from OrientDB in {@link OArchitectEditor}
 * @param ownerClass {@link OArchitectOClass} which contains current property
 * @param name string - name of property
 * @param type string - type of property. See {@link OArchitectOType}
 * @param cell {@link mxCell} which contains current property
 * @constructor
 */
var OArchitectOProperty = function (ownerClass, name, type, cell) {
    OArchitectEditorObject.apply(this, [true]);

    this.ownerClass = null;
    this.name = name != null ? name : null;
    this.type = type != null ? type : null;
    this.linkedClass = null;
    this.subClassProperty = false;
    this.superClassExistsInEditor = false;
    this.pageUrl = null;
    this.cell = null;
    this.previousName = null;

    this.previousState = null;
    this.nextState = null;
    this.removed = false;
    this.inverseProperty = null;
    this.inversePropertyEnable = false;
    this.existsInDb = false;
    this.databaseJson = null;

    this.notSetLinkedClass = false;
    this.inverseLock = false;

    this.order = 0;

    if (ownerClass != null) this.setOwnerClass(ownerClass);
    if (cell != null) this.setCell(cell);
};

OArchitectOProperty.prototype = Object.create(OArchitectEditorObject.prototype);
OArchitectOProperty.prototype.constructor = OArchitectOProperty;

/**
 * {@link OArchitectOClass} which contains this property
 */
OArchitectOProperty.prototype.ownerClass = null;

/**
 * string name of this property
 */
OArchitectOProperty.prototype.name = null;

/**
 * string type of this property. See {@link OArchitectOType}
 */
OArchitectOProperty.prototype.type = null;

/**
 * {@link OArchitectOClass} in which this property is linked
 */
OArchitectOProperty.prototype.linkedClass = null;

OArchitectOProperty.prototype.inverseProperty = null;

/**
 * boolean true if this property is inherited property from superclass. Default false.
 */
OArchitectOProperty.prototype.subClassProperty = false;

/**
 * boolean true if superclass of this property exists in editor. Default false.
 * Uses for resolving view property dependencies in classes cells
 */
OArchitectOProperty.prototype.superClassExistsInEditor = false;

/**
 * string url to property page. Uses for redirect user to property page
 */
OArchitectOProperty.prototype.pageUrl = null;

/**
 * {@link mxCell} which contains this property.
 */
OArchitectOProperty.prototype.cell = null;

/**
 * Save previous state of property for undo
 */
OArchitectOProperty.prototype.previousState = null;

/**
 * boolean indicates if this property is removed. If true this property already removed
 */
OArchitectOProperty.prototype.removed = false;

/**
 * string contains previous name of this property. Needs for correct property rename
 */
OArchitectOProperty.prototype.previousName = null;

/**
 * number - order for display properties in Orienteer
 */
OArchitectOProperty.prototype.order = 0;

/**
 * Config instance of {@link OArchitectOProperty} from json which is respond from database
 * @param oClass - {@link OArchitectOClass} which is owner of this property
 * @param json - json string which contains config for this property
 * @param cell
 */
OArchitectOProperty.prototype.configFromJson = function (oClass, json, cell) {
    if (this.isValidName(json.name)) this.name = json.name;
    if (oClass.getProperty(this.name) === null) {
        oClass.properties.push(this);
        console.warn('push property to class');
    }
    if (this.isValidType(json.type)) this.type = json.type;
    this.subClassProperty = json.subClassProperty;
    this.ownerClass = oClass;
    this.pageUrl = json.pageUrl;
    this.order = json.order;

    setCell(this, oClass, cell);
    if (json.linkedClass != null) setLinkedClass(this, json);

    this.setInversePropertyEnable(json.inversePropertyEnable);
    if (this.inversePropertyEnable && json.inverseProperty != null) setInverseProperty(this, json);

    this.setExistsInDb(json.existsInDb);
    oClass.notifySubClassesAboutChangesInProperty(this);

    function setCell(property, oClass, cell) {
        property.setCell(property.cell !== null ? property.cell : cell != null ? cell : null);
        if (property.cell === null) oClass.createCellForProperty(this);
    }

    function setLinkedClass(property, json) {
        console.warn(property.name, ' - set linked class: ', json.linkedClass);
        var linkedCell = OArchitectUtil.getCellByClassName(json.linkedClass);
        if (linkedCell !== null) {
            property.setLinkedClass(linkedCell.value);
        } else property.linkedClass = json.linkedClass;
    }

    function setInverseProperty(property, json) {
        var inverse = null;
        console.warn(property.name, ' - set inverse property: ', json.inverseProperty);
        if (property.linkedClass instanceof OArchitectOClass) {
            inverse = property.linkedClass.getProperty(json.inverseProperty.name);
        }

        if (inverse !== null) {
            property.setInverseProperty(inverse);
        } else {
            property.inverseProperty = new OArchitectOProperty();
            property.inverseProperty.name = json.inverseProperty.name;
            property.inverseProperty.type = json.inverseProperty.type;
            property.inverseProperty.linkedClass = json.inverseProperty.linkedClass;
            property.inverseProperty.inverseProperty = json.inverseProperty.inverseProperty;
            property.inverseProperty.existsInDb = json.inverseProperty.existsInDb;
        }
    }
};

// /**
//  * Config instance of {@link OArchitectOProperty} from cell which is saved in xml config.
//  * @param oClass - {@link OArchitectOClass} which is owner of this property
//  * @param propertyCell - {@link mxCell} which contains this property
//  */
// OArchitectOProperty.prototype.configFromCell = function (oClass, propertyCell) {
//     if (oClass.getProperty(this.name) === null) oClass.properties.push(this);
//     this.ownerClass = oClass;
//     this.cell = propertyCell;
//     var linkedCell = OArchitectUtil.getCellByClassName(this.linkedClass);
//     if (linkedCell !== null) {
//         this.setLinkedClass(linkedCell.value);
//     }
//     this.removed = false;
//     this.cell.parent = oClass.cell;
//     this.setInversePropertyEnable(this.inversePropertyEnable);
//     if (this.inverseProperty !== null) {
//         var property = null;
//         var name = this.inverseProperty;
//         OArchitectUtil.forEach(app.editor.graph.getChildVertices(linkedCell), function (cell, trigger) {
//             if (cell.value.name === name) {
//                 property = cell.value;
//                 trigger.stop = true;
//             }
//         });
//         if (property !== null) {
//             this.setInverseProperty(property);
//         }
//     } else this.setInverseProperty(null);
//     oClass.notifySubClassesAboutChangesInProperty(this);
// };

/**
 * Set type of this property
 * @param type - string which contains type name. See {@link OArchitectOType}
 */
OArchitectOProperty.prototype.setType = function (type) {
    if (this.canModifyNameAndType() && this.isValidType(type)) {
        var previousType = this.type;
        this.type = type;
        if (OArchitectOType.isLink(previousType) && !OArchitectOType.isLink(this.type)) {
            this.setLinkedClass(null);
        }
    }
};

/**
 * Set name of this property
 * @param name - string. Can't be null
 */
OArchitectOProperty.prototype.setName = function (name) {
    if (this.canModifyNameAndType() && this.isValidName(name)) {
        this.previousName = this.name;
        this.name = name;
    }
};

/**
 * Set name and type for this property and update property value in property cell and class value in class cell.
 * @param name - string name
 * @param type - string type
 * @param inversePropertyEnable
 * @param inverseProperty
 */
OArchitectOProperty.prototype.updateProperty = function (name, type, inversePropertyEnable, inverseProperty) {
    if (this.canUpdate(name, type, inversePropertyEnable, inverseProperty)) {
        var graph = app.editor.graph;
        // graph.getModel().beginUpdate();
        // try {
            this.saveState();
            this.setName(name);
            this.setType(type);
            this.ownerClass.notifySubClassesAboutChangesInProperty(this);
            this.setInversePropertyEnable(inversePropertyEnable);
            if (inversePropertyEnable) this.setInverseProperty(inverseProperty);
            // this.updateValueInCell();
        // } finally {
        //     graph.getModel().endUpdate();
        // }
    }
};

OArchitectOProperty.prototype.canUpdate = function (name, type, inversePropertyEnable, inverseProperty) {
    return this.isValidName(name) || this.isValidType(type)
        || this.isValidInverseProperty(inverseProperty) || this.inversePropertyEnable !== inversePropertyEnable;
};

/**
 * Checks if given type is valid for this property
 * @param type - string with OrientDB type
 * @return boolean true if type if valid
 */
OArchitectOProperty.prototype.isValidType = function (type) {
    return type != null && OArchitectOType.contains(type) && this.type !== type;
};

/**
 * Checks if given name is valid for this property
 * @param name - string property name
 * @return boolean true if name is valid
 */
OArchitectOProperty.prototype.isValidName = function (name) {
    var valid = name != null && this.name !== name;
    if (valid && this.ownerClass != null) valid = this.ownerClass.getProperty(name) == null;
    return valid;
};

OArchitectOProperty.prototype.isValidLink = function (link) {
    return link === null || link !== undefined && link instanceof OArchitectOClass && link !== this.linkedClass;
};

OArchitectOProperty.prototype.isValidInverseProperty = function (property) {
    return property === null || property !== undefined && property instanceof OArchitectOProperty && this.inverseProperty !== property;
};

/**
 * Set owner class of this property
 * @param ownerClass - {@link OArchitectOClass} which is owner class of this property
 */
OArchitectOProperty.prototype.setOwnerClass = function (ownerClass) {
    if (ownerClass != null) {
        this.ownerClass = ownerClass;
    }
};

/**
 * Set cell of this property
 * @param cell - {@link mxCell} which contains this property
 */
OArchitectOProperty.prototype.setCell = function (cell) {
    if (cell !== null) {
        var graph = app.editor.graph;
        // graph.getModel().beginUpdate();
        // try {
            this.cell = cell;
            graph.getModel().setValue(this.cell, this);
        // } finally {
        //     graph.getModel().endUpdate();
        // }
    }
};

/**
 * Save previous state of this property for undo action
 */
OArchitectOProperty.prototype.saveState = function () {
    // if (!this.isRemoved() && !this.ownerClass.isRemoved() && this.name !== null) {
    //     this.ownerClass.saveState();
    //     this.previousState = this.toEditorConfigObject();
    // } else this.previousState = null;
};

/**
 * Update property in property cell and update class in class cell
 */
OArchitectOProperty.prototype.updateValueInCell = function () {
    if (this.databaseJson !== null) {
        if (!this.equalsWithJsonLink(this.databaseJson)) {
            if (this.existsInDb) this.setExistsInDb(false);
        } else if (!this.existsInDb) this.setExistsInDb(true);
    }
    this.ownerClass.updateValueInCell();
    this.setCell(this.cell);
};

/**
 * Checks if this property is property from {@link OArchitectOProperty#ownerClass} super class.
 * @returns boolean true if is subclass property
 */
OArchitectOProperty.prototype.isSubClassProperty = function () {
    return this.subClassProperty;
};

/**
 * Checks if superclass of this property exists in editor
 * @returns boolean - true if exists
 */
OArchitectOProperty.prototype.isSuperClassExistsInEditor = function () {
    return this.superClassExistsInEditor;
};

/**
 * @returns boolean true if {@link OArchitectOProperty#type} is link type
 */
OArchitectOProperty.prototype.isLink = function () {
    return OArchitectOType.isLink(this.type);
};

/**
 * @returns boolean true if {@link OArchitectOProperty#cell} can connect to some {@link OArchitectOClass#cell}
 */
OArchitectOProperty.prototype.canConnect = function () {
    var result = false;
    if (this.type !== null && OArchitectOType.isLink(this.type)) {
        result = this.linkedClass == null && !this.subClassProperty;
    }
    return result;
};

/**
 * @returns boolean true if {@link OArchitectOProperty#cell} can disconnect from some {@link OArchitectOClass#cell}
 */
OArchitectOProperty.prototype.canDisconnect = function () {
    return !this.subClassProperty;
};

/**
 * Set {@link OArchitectOProperty#linkedClass} for this property
 * @param linkedClass {@link OArchitectOClass} which will be linked class for this property
 */
OArchitectOProperty.prototype.setAndSaveLinkedClass = function (linkedClass) {
    var edge = null;
    if (this.canModifyLink() && this.isValidLink(linkedClass)) {
        this.saveState();
        edge = this.setLinkedClass(linkedClass);
        this.updateValueInCell();
    }
    return edge;
};

OArchitectOProperty.prototype.setLinkedClass = function (linkedClass) {
    var edge = null;
    if (this.canModifyLink() && this.isValidLink(linkedClass)) {
        if (linkedClass == null && this.linkedClass !== null) {
            OArchitectUtil.manageEdgesBetweenCells(this.cell, this.linkedClass.cell, false);
            this.linkedClass = linkedClass;
            this.setInverseProperty(null);
            this.ownerClass.notifySubClassesAboutChangesInProperty(this);
        } else if (linkedClass != null) {
            this.linkedClass = linkedClass;
            edge = OArchitectUtil.manageEdgesBetweenCells(this.cell, this.linkedClass.cell, true);
            this.ownerClass.notifySubClassesAboutChangesInProperty(this);
        }
    }
    return edge;
};

OArchitectOProperty.prototype.isInverseProperty = function () {
    return this.inversePropertyEnable;
};

OArchitectOProperty.prototype.setInversePropertyEnable = function (enable) {
    if (!enable && this.inverseProperty !== null) {
        this.setInverseProperty(null);
    }
    this.inversePropertyEnable = enable;
    this.ownerClass.notifySubClassesAboutChangesInProperty(this);
};

OArchitectOProperty.prototype.setInverseProperty = function (property, createConnection) {
    var edge = null;
    if (this.isInverseProperty() || property === null) {
        createConnection = createConnection == null ? true : createConnection;
        this.inverseLock = true;
        if (property !== null) {
            this.inverseProperty = property;
            if (property.inverseProperty !== null && this.name === property.inverseProperty.name) {
                manageEdgeBetweenPropertyClasses(this, property, false);
                if (createConnection) {
                    edge = OArchitectUtil.manageEdgesBetweenCells(this.cell, property.cell, true, true);
                    edge.setValue('inverse');
                }
            }
        } else if (this.inverseProperty !== null && !this.existsInDb) {
            if (this.inverseProperty.inverseProperty !== null && this.name === this.inverseProperty.inverseProperty.name) {
                OArchitectUtil.manageEdgesBetweenCells(this.cell, this.inverseProperty.cell, false, true);
                manageEdgeBetweenPropertyClasses(this, this.inverseProperty, true);
            }
            this.inverseProperty = null;
        }
        this.ownerClass.notifySubClassesAboutChangesInProperty(this);
        this.inverseLock = false;
    }

    function manageEdgeBetweenPropertyClasses(property, inverse, create) {
        property.notSetLinkedClass = true;
        inverse.notSetLinkedClass = true;
        OArchitectUtil.manageEdgesBetweenCells(property.cell, inverse.ownerClass.cell, create);
        OArchitectUtil.manageEdgesBetweenCells(inverse.cell, property.ownerClass.cell, create);
        property.notSetLinkedClass = false;
        inverse.notSetLinkedClass = false;
    }
    return edge;
};

/**
 * @return boolean true if property has been already removed from class
 */
OArchitectOProperty.prototype.isRemoved = function () {
    return this.removed;
};

OArchitectOProperty.prototype.canModifyNameAndType = function () {
    var modify = !this.isRemoved() && this.ownerClass != null && !this.existsInDb;
    if (modify) modify = !this.ownerClass.isRemoved();
    return modify;
};

OArchitectOProperty.prototype.canModifyLink = function () {
    var modify = !this.isRemoved() && this.ownerClass != null && !this.notSetLinkedClass;
    if (modify) modify = !this.ownerClass.isRemoved();
    return modify;
};

OArchitectOProperty.prototype.toString = function () {
    return this.name;
};

/**
 * Checks if given json property is equals with current {@link OArchitectOProperty} instance
 * @param jsonProperty - json property
 * @returns boolean - true if equals
 */
OArchitectOProperty.prototype.equalsWithJsonProperty = function (jsonProperty) {
    var equals = true;
    if (this.name !== jsonProperty.name) equals = false;
    if (equals && this.type !== jsonProperty.type) equals = false;
    if (equals && this.pageUrl !== jsonProperty.pageUrl) equals = false;
    if (equals && this.subClassProperty != jsonProperty.subClassProperty) equals = false;
    if (equals && this.order !== jsonProperty.order) equals = false;
    return equals && this.equalsWithJsonLink(jsonProperty);
};

OArchitectOProperty.prototype.equalsWithJsonLink = function (jsonProperty) {
    var equals = true;
    if (equals && this.linkedClass != null && this.linkedClass.name !== jsonProperty.linkedClass) equals = false;
    if (equals && this.linkedClass == null && jsonProperty.linkedClass != null) equals = false;
    if (equals && this.inverseProperty != null && this.inverseProperty.name !== jsonProperty.inverseProperty) equals = false;
    if (equals && this.inverseProperty == null && jsonProperty.inverseProperty != null) equals = false;
    return equals;
};

OArchitectOProperty.prototype.setExistsInDb = function (existsInDb) {
    this.existsInDb = existsInDb;
    if (this.cell != null) {
        var edges = this.linkedClass !== null ? getEdges(this.cell, this.linkedClass.cell) : [];
        var inverseEdges = getInverseEdges(this);
        if (this.existsInDb) {
            app.editor.graph.setCellStyle(OArchitectConstants.OPROPERTY_EXISTS_STYLE, [this.cell]);
            app.editor.graph.setCellStyle(OArchitectConstants.OPROPERTY_EXISTS_CONNECTION_STYLE, edges);
            app.editor.graph.setCellStyle(OArchitectConstants.OPROPERTY_EXISTS_INVERSE_CONNECTION_STYLE, inverseEdges);
        } else {
            app.editor.graph.setCellStyle(OArchitectConstants.OPROPERTY_STYLE, [this.cell]);
            app.editor.graph.setCellStyle(OArchitectConstants.OPROPERTY_CONNECTION_STYLE, edges);
            app.editor.graph.setCellStyle(OArchitectConstants.OPROPERTY_INVERSE_CONNECTION_STYLE, inverseEdges);
        }

    }

    function getInverseEdges(property) {
        var result = [];
        if (property.isInverseProperty() && property.inverseProperty !== null
            && property === property.inverseProperty.inverseProperty && property.inverseProperty.cell != null) {
            result = getEdges(property.cell, property.inverseProperty.cell);
        }
        return result;
    }

    function getEdges(cell1, cell2) {
        var result = [];
        if (cell1 !== null && cell2 !== null)
            result = app.editor.graph.getEdgesBetween(cell1, cell2);
        return result;
    }

};

OArchitectOProperty.prototype.setOrder = function (order) {
    this.order = order;
};

OArchitectOProperty.prototype.getOrder = function () {
    return this.order;
};

/**
 * Convert this property to json string
 * @returns json string
 */
OArchitectOProperty.prototype.toJson = function () {
    function filter(key, value) {
        if (key === 'cell' || key === 'previousState' || key === 'nextState' || key === 'databaseJson') {
            value = undefined;
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

    return JSON.stringify(this, filter);
};

// /**
//  * Creates instance of {@link OArchitectOProperty} which can be converted to editor xml config
//  * @returns {@link OArchitectOProperty} instance of this property which can be converted to editor xml config
//  */
// OArchitectOProperty.prototype.toEditorConfigObject = function () {
//     var result = new OArchitectOProperty();
//     result.ownerClass = this.ownerClass instanceof OArchitectOClass ? this.ownerClass.name : this.ownerClass;
//     result.name = this.name;
//     result.type = this.type;
//     result.linkedClass = this.linkedClass instanceof OArchitectOClass ? this.linkedClass.name : this.linkedClass;
//     result.previousName = this.previousName;
//     result.subClassProperty = this.subClassProperty;
//     result.inverseProperty = this.inverseProperty instanceof OArchitectOProperty ? this.inverseProperty.name : this.inverseProperty;
//     result.existsInDb = this.existsInDb;
//     result.pageUrl = this.pageUrl;
//     result.order = this.order;
//     result.inversePropertyEnable = this.inversePropertyEnable;
//     return result;
// };
