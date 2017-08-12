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
    this.previousName = null;
    this.cell = null;

    if (ownerClass != null) this.setOwnerClassName(ownerClass);
    if (name != null) this.setName(name);
    if (type != null) this.setType(type);
    if (cell != null) this.setCell(cell);
};

/**
 * Config instance of {@link OArchitectOProperty} from json which is respond from database
 * @param oClass - {@link OArchitectOClass} which is owner of this property
 * @param json - json string which contains config for this property
 */
OArchitectOProperty.prototype.configFromDatabase = function (oClass, json) {
    this.setName(json.name);
    this.setType(json.type);
    this.subClassProperty = json.subClassProperty;
    this.ownerClass = oClass;
    var linkedClassCell = OArchitectUtil.getCellByClassName(json.linkedClass);
    if (linkedClassCell != null)
        this.setLinkedClass(linkedClassCell.value);
    oClass.properties.push(this);
    oClass.changeProperties(oClass, [this], this.subClassProperty, false);
};

/**
 * INTERNAL FUNCTION. DON'T CALL IT.
 * Config instance of {@link OArchitectOProperty} from cell which is saved in xml config.
 * @param oClass - {@link OArchitectOClass} which is owner of this property
 * @param propertyCell - {@link mxCell} which contains this property
 */
OArchitectOProperty.prototype.configFromEditorConfig = function (oClass, propertyCell) {
    this.ownerClass = oClass;
    this.cell = propertyCell;
    var linkedCell = OArchitectUtil.getCellByClassName(this.linkedClass);
    if (linkedCell != null)
        this.setLinkedClass(linkedCell.value);
    this.cell.parent = oClass.cell;
    oClass.properties.push(this);
    oClass.changeProperties(oClass, [this], this.subClassProperty, false);
};

/**
 * Set type of this property
 * @param type - string which contains type name. See {@link OArchitectOType}
 */
OArchitectOProperty.prototype.setType = function (type) {
    if (OArchitectOType.contains(type)) {
        this.type = type;
    }
};

/**
 * Set name of this property
 * @param name - string. Can't be null
 */
OArchitectOProperty.prototype.setName = function (name) {
    if (name != null) {
        this.previousName = this.name;
        this.name = name;
    }
};

/**
 * Set owner class of this property
 * @param ownerClass - {@link OArchitectOClass} which is owner class of this property
 */
OArchitectOProperty.prototype.setOwnerClassName = function (ownerClass) {
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

/**
 * Update {@link OArchitectOProperty#ownerClass} sub classes about changes of this property
 */
OArchitectOProperty.prototype.notifySubClassesPropertiesAboutChanges = function () {
    this.ownerClass.changeProperties(this.ownerClass, [this], this.subClassProperty, false);
};

/**
 * Checks if this property is property from {@link OArchitectOProperty#ownerClass} super class.
 * @returns boolean true if is subclass property
 */
OArchitectOProperty.prototype.isSubClassProperty = function () {
    return this.subClassProperty;
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
            OArchitectUtil.manageEdgesBetweenCells(this.cell, this.linkedClass.cell, false);
        } else if (linkedClass != null) {
            OArchitectUtil.manageEdgesBetweenCells(this.cell, linkedClass.cell, true);
        }
        this.linkedClass = linkedClass;
        this.ownerClass.changeProperties(this.ownerClass, [this]);
    }
};

OArchitectOProperty.prototype.toString = function () {
    return this.name;
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
    var result = new OArchitectOProperty(this.ownerClass.name, this.name, this.type, null);
    result.linkedClass = this.linkedClass != null ? this.linkedClass.name : null;
    result.previousName = this.previousName;
    result.subClassProperty = this.subClassProperty;
    return result;
};
