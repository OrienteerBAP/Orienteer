/**
 * Utility class which represents OProperty from OrientDB in {@link OArchitectEditor}
 * @param ownerClass {@link OArchitectOClass} which contains current property
 * @param name string - name of property
 * @param type string - type of property. See {@link OArchitectOType}
 * @param cell {@link mxCell} which contains current property
 * @constructor
 */
var OArchitectOProperty = function (ownerClass, name, type, cell) {
    this.ownerClass = null;
    this.name = null;
    this.type = null;
    this.linkedClass = null;
    this.subClassProperty = false;
    this.superClassExistsInEditor = false;
    this.pageUrl = null;
    this.cell = null;
    this.previousName = null;

    this.previousState = null;

    if (ownerClass != null) this.setOwnerClass(ownerClass);
    if (name != null) this.setName(name);
    if (type != null) this.setType(type);
    if (cell != null) this.setCell(cell);
};

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
 * string contains previous name of this property. Needs for correct property rename
 */
OArchitectOProperty.prototype.previousName = null;

/**
 * Config instance of {@link OArchitectOProperty} from json which is respond from database
 * @param oClass - {@link OArchitectOClass} which is owner of this property
 * @param json - json string which contains config for this property
 */
OArchitectOProperty.prototype.configFromDatabase = function (oClass, json) {
    this.setName(json.name);
    if (oClass.getProperty(this.name) == null) oClass.properties.push(this);
    this.setType(json.type);
    this.subClassProperty = json.subClassProperty;
    this.ownerClass = oClass;
    this.pageUrl = json.pageUrl;
    var linkedClassCell = OArchitectUtil.getCellByClassName(json.linkedClass);
    if (linkedClassCell != null) this.setLinkedClass(linkedClassCell.value);
    if (this.cell != null) this.setCell(this.cell);
    else oClass.createCellForProperty(this);
    oClass.notifySubClassesAboutChangesInProperty(this);
};

/**
 * INTERNAL FUNCTION. DON'T CALL IT.
 * Config instance of {@link OArchitectOProperty} from cell which is saved in xml config.
 * @param oClass - {@link OArchitectOClass} which is owner of this property
 * @param propertyCell - {@link mxCell} which contains this property
 */
OArchitectOProperty.prototype.configFromCell = function (oClass, propertyCell) {
    if (oClass.getProperty(this.name) == null) oClass.properties.push(this);
    this.ownerClass = oClass;
    this.cell = propertyCell;
    var linkedCell = OArchitectUtil.getCellByClassName(this.linkedClass);
    if (linkedCell != null)
        this.setLinkedClass(linkedCell.value);
    this.cell.parent = oClass.cell;
    oClass.notifySubClassesAboutChangesInProperty(this);
};

/**
 * Set type of this property
 * @param type - string which contains type name. See {@link OArchitectOType}
 */
OArchitectOProperty.prototype.setType = function (type) {
    if (this.isValidType(type)) {
        this.type = type;
    }
};

/**
 * Set name of this property
 * @param name - string. Can't be null
 */
OArchitectOProperty.prototype.setName = function (name) {
    if (this.isValidName(name)) {
        this.previousName = this.name;
        this.name = name;
    }
};

OArchitectOProperty.prototype.setNameAndType = function (name, type) {
    if (this.isValidName(name) || this.isValidType(type)) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        try {
            this.savePreviousState();
            this.setName(name);
            this.setType(type);
            this.updateValueInCell();
        } finally {
            graph.getModel().endUpdate();
        }
    }
};

OArchitectOProperty.prototype.isValidType = function (type) {
    return type != null && OArchitectOType.contains(type) && this.type !== type;
};

OArchitectOProperty.prototype.isValidName = function (name) {
    return name != null && this.name !== name && this.ownerClass.getProperty(name) == null;
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

OArchitectOProperty.prototype.savePreviousState = function (clear) {
    if (this.name !== null) {
        this.ownerClass.savePreviousState();
        this.previousState = clear ? null : this.toEditorConfigObject();
    } else this.previousState = null;
};

OArchitectOProperty.prototype.updateValueInCell = function () {
    this.ownerClass.updateValueInCell();
    this.setCell(this.cell);
};

OArchitectOProperty.prototype.prepareForRemove = function () {
    this.cell = null;
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
OArchitectOProperty.prototype.setLinkedClass = function (linkedClass) {
    if (this.linkedClass !== linkedClass) {
        if (linkedClass == null && this.linkedClass != null) {
            if (!this.ownerClass.existsInDb) {
                OArchitectUtil.manageEdgesBetweenCells(this.cell, this.linkedClass.cell, false);
                this.linkedClass = linkedClass;
                this.ownerClass.notifySubClassesAboutChangesInProperty(this);
            }
        } else if (linkedClass != null) {
            this.linkedClass = linkedClass;
            OArchitectUtil.manageEdgesBetweenCells(this.cell, this.linkedClass.cell, true);
            this.ownerClass.notifySubClassesAboutChangesInProperty(this);
        }
    }
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
    if (equals && this.linkedClass != null && this.linkedClass.name !== jsonProperty.linkedClass) equals = false;
    if (equals && this.linkedClass == null && jsonProperty.linkedClass != null) equals = false;
    return equals;
};

/**
 * Convert this property to json string
 * @returns json string
 */
OArchitectOProperty.prototype.toJson = function () {
    function filter(key, value) {
        if (key === 'cell') {
            value = undefined;
        } else if (key === 'ownerClass' || key === 'linkedClass') {
            value = value != null ? value.name : null;
        }
        return value;
    }

    return JSON.stringify(this, filter);
};

/**
 * Creates instance of {@link OArchitectOProperty} which can be converted to editor xml config
 * @returns {@link OArchitectOProperty} instance of this property which can be converted to editor xml config
 */
OArchitectOProperty.prototype.toEditorConfigObject = function () {
    var result = new OArchitectOProperty();
    result.ownerClass = this.ownerClass.name;
    result.name = this.name;
    result.type = this.type;
    result.linkedClass = this.linkedClass != null ? this.linkedClass.name : null;
    result.previousName = this.previousName;
    result.subClassProperty = this.subClassProperty;
    return result;
};
