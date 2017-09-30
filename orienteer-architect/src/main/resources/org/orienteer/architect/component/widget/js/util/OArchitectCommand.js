
var OArchitectCommand = function () {};

OArchitectCommand.prototype.executeCommands = function (commands) {
    OArchitectUtil.forEach(commands, function (command) {
        command.execute();
    });
};

OArchitectCommand.prototype.execute = function () {
    app.editor.beginUnsaveActions();
    app.editor.disableConnection();
    this.executeCommand();
    app.editor.enableConnection();
    app.editor.endUnsaveActions();
};

OArchitectCommand.prototype.executeCommand = function () {};

/**
 * Commands for properties
 */
var OPropertyCreateCommand = function (property, oClass, remove) {
    OArchitectCommand.apply(this, []);
    this.property = property;
    this.oClass = oClass;
    this.remove = remove !== undefined ? remove : false;
};

OPropertyCreateCommand.prototype = Object.create(OArchitectCommand.prototype);
OPropertyCreateCommand.prototype.constructor = OPropertyCreateCommand;

OPropertyCreateCommand.prototype.executeCommand = function () {
    if (!this.remove) {
        if (this.property.cell !== null) {
            this.oClass.properties.push(this.property);
            this.oClass.createCellForProperty(this.property);
            this.oClass.notifySubClassesAboutChangesInProperty(this.property);
        } else this.property = this.oClass.createProperty(this.property.name, this.property.type, this.property.cell);
        this.property.removed = false;
        this.remove = true;
    } else {
        this.property.removed = true;
        this.oClass.removeProperty(this.property);
        this.remove = false;
    }
};


var OPropertyNameAndTypeChangeCommand = function (property, name, type) {
    OArchitectCommand.apply(this, []);
    this.property = property;
    this.name = name !== undefined ? name : null;
    this.type = type !== undefined ? type : null;
};

OPropertyNameAndTypeChangeCommand.prototype = Object.create(OArchitectCommand.prototype);
OPropertyNameAndTypeChangeCommand.prototype.constructor = OPropertyNameAndTypeChangeCommand;

OPropertyNameAndTypeChangeCommand.prototype.executeCommand = function () {
    this.property = this.init(this.property);
    var tmpName = this.property.name;
    var tmpType = this.property.type;
    this.property.setName(this.name);
    this.property.setType(this.type);
    this.name = tmpName;
    this.type = tmpType;
    this.property.updateValueInCell();
};

OPropertyNameAndTypeChangeCommand.prototype.init = function (property) {
    if (property.isRemoved()) {
        property = getProperty(property.name, property.ownerClass.name);
    }

    function getProperty(propertyName, className) {
        var cell = OArchitectUtil.getCellByClassName(className);
        return cell !== null ? cell.value.getProperty(propertyName) : null;
    }
    return property;
};

var OPropertyLinkChangeCommand = function (property, linkedClass, remove) {
    OPropertyNameAndTypeChangeCommand.apply(this, [property, null]);
    this.linkedClass = linkedClass;
    this.remove = remove;
    this.removed = false;
};

OPropertyLinkChangeCommand.prototype = Object.create(OPropertyNameAndTypeChangeCommand.prototype);
OPropertyLinkChangeCommand.prototype.constructor = OPropertyLinkChangeCommand;

OPropertyLinkChangeCommand.prototype.executeCommand = function () {
    var prop = this.init(this.property);
    this.property = prop !== null ? prop : this.property;
    if (this.remove && !this.removed) {
        this.linkedClass = this.property.linkedClass;
        this.property.setLinkedClass(null);
        this.removed = true;
    } else {
        var tmp = this.property.linkedClass;
        this.property.setLinkedClass(this.linkedClass);
        this.linkedClass = tmp;
        this.removed = false;
    }
};

var OPropertyInverseChangeCommand = function (property, inversePropertyEnable, inverseProperty, remove) {
    OPropertyNameAndTypeChangeCommand.apply(this, [property]);
    this.inversePropertyEnable = inversePropertyEnable;
    this.inverseProperty = inverseProperty;
    this.remove = remove != null ? remove : false;
    this.removed = false;
};

OPropertyInverseChangeCommand.prototype = Object.create(OPropertyNameAndTypeChangeCommand.prototype);
OPropertyInverseChangeCommand.prototype.constructor = OPropertyInverseChangeCommand;

OPropertyInverseChangeCommand.prototype.executeCommand = function () {
    var prop = this.init(this.property);
    if (prop !== null) this.property = prop;
    if (this.inverseProperty !== null) this.inverseProperty = this.init(this.inverseProperty);

    if (this.remove && !this.removed) {
        var tmp = this.property.inversePropertyEnable;
        this.inverseProperty = this.property.inverseProperty;
        this.property.setInversePropertyEnable(this.inversePropertyEnable);
        this.property.setInverseProperty(null);
        this.inversePropertyEnable = tmp;
        this.removed = true;
    } else {
        var previousInversePropertyEnable = this.property.inversePropertyEnable;
        var previousInverseProperty = this.property.inverseProperty;
        this.property.setInversePropertyEnable(this.inversePropertyEnable);
        this.property.setInverseProperty(this.inverseProperty/*, !app.editor.undoOrRedoRuns*/);
        this.inversePropertyEnable = previousInversePropertyEnable;
        this.inverseProperty = previousInverseProperty;
        this.removed = false;
    }
};

/**
 * Commands for classes
 **/

var OClassCreateCommand = function (oClass, x, y, remove) {
    OArchitectCommand.apply(this, []);
    this.oClass = oClass;
    this.x = x;
    this.y = y;
    this.removed = remove !== undefined ? !remove : false;
};

OClassCreateCommand.prototype = Object.create(OArchitectCommand.prototype);
OClassCreateCommand.prototype.constructor = OClassCreateCommand;

OClassCreateCommand.prototype.executeCommand = function () {
    var graph = app.editor.graph;
    if (this.oClass.cell === null || this.removed) {
        this.removed = false;
        this.oClass.removed = false;
        if (this.oClass.cell === null) {
            this.oClass.setCell(OArchitectUtil.createOClassVertex(this.oClass, this.x, this.y));
            graph.addCell(this.oClass.cell, graph.getDefaultParent());
        } else {
            graph.addCell(this.oClass.cell, graph.getDefaultParent());
            this.oClass.updateValueInCell(true, true);
            OArchitectUtil.updateAllCells();
        }
    } else if (this.oClass.cell !== null) {
        OArchitectUtil.removeCell(this.oClass.cell, true);
        this.removed = true;
    }
};

var OClassChangeNameCommand = function (oClass, name) {
    this.oClass = oClass;
    this.name = name;
};

OClassChangeNameCommand.prototype = Object.create(OArchitectCommand.prototype);
OClassChangeNameCommand.prototype.constructor = OClassChangeNameCommand;

OClassChangeNameCommand.prototype.executeCommand = function () {
    var tmp = this.oClass.name;
    this.oClass.name = this.name;
    this.name = tmp;
    this.oClass.updateValueInCell(true, true);
};

var OClassInheritanceCommand = function (subClass, superClass, remove) {
    OArchitectCommand.apply(this, []);
    this.subClass = subClass;
    this.superClass = superClass;
    this.remove = remove != null ? remove : false;
};

OClassInheritanceCommand.prototype = Object.create(OArchitectCommand.prototype);
OClassInheritanceCommand.prototype.constructor = OClassInheritanceCommand;

OClassInheritanceCommand.prototype.executeCommand = function () {
    var graph = app.editor.graph;

    graph.getModel().beginUpdate();

    if (this.remove) {
        this.subClass.removeSuperClass(this.superClass);
    } else {
        this.subClass.addSuperClass(this.superClass);
    }
    this.subClass.updateValueInCell();
    this.superClass.updateValueInCell();
    this.remove = !this.remove;
    graph.getModel().endUpdate();
};

/**
 * Macro commands
 **/

var OArchitectRemoveCommand = function () {
    this.removed = false;
    this.commands = null;
};

OArchitectRemoveCommand.prototype = Object.create(OArchitectCommand.prototype);
OArchitectRemoveCommand.prototype.constructor = OArchitectRemoveCommand;

OArchitectRemoveCommand.prototype.prepareCommandsForExistsInDb = function () {
    var removed = this.removed;
    OArchitectUtil.forEach(this.commands, function (command) {
        if (command instanceof OPropertyLinkChangeCommand) {
            command.linkedClass = removed ? command.property.linkedClass : null;
        } else if (command instanceof OPropertyInverseChangeCommand) {
            command.inverseProperty = removed ? command.property.inverseProperty : null;
        }
    });
};

var OPropertyRemoveCommand = function (property) {
    OArchitectRemoveCommand.apply(this, []);
    this.property = property;
};

OPropertyRemoveCommand.prototype = Object.create(OArchitectRemoveCommand.prototype);
OPropertyRemoveCommand.prototype.constructor = OPropertyRemoveCommand;

OPropertyRemoveCommand.prototype.executeCommand = function () {
    var property = this.property;

    if (this.commands === null) {
        this.commands = this.getRemovePropertyCommands(property);
    } else OArchitectUtil.inverseArray(this.commands);

    if (this.property.existsInDb) {
        this.prepareCommandsForExistsInDb();
    }
    this.executeCommands(this.commands);
    this.removed = !this.removed;
};

OPropertyRemoveCommand.prototype.getRemovePropertyCommands = function (property) {
    var commands = [];
    if (property.inverseProperty !== null) {
        var inverseProp = property.inverseProperty;
        if (property === inverseProp.inverseProperty) {
            commands.push(new OPropertyInverseChangeCommand(
                inverseProp, inverseProp.inversePropertyEnable, null));
            if (inverseProp.linkedClass === property.ownerClass) {
                commands.push(new OPropertyLinkChangeCommand(inverseProp, null));
            }
        }
        commands.push(new OPropertyInverseChangeCommand(property, property.inversePropertyEnable, null));
    }
    if (property.linkedClass !== null) {
        commands.push(new OPropertyLinkChangeCommand(property, null));
    }
    commands.push(new OPropertyCreateCommand(property, property.ownerClass, true));
    return commands;
};


var OClassRemoveCommand = function (oClass) {
    OArchitectRemoveCommand.apply(this, []);
    this.oClass = oClass;
};

OClassRemoveCommand.prototype = Object.create(OArchitectRemoveCommand.prototype);
OClassRemoveCommand.prototype.constructor = OClassRemoveCommand;

OClassRemoveCommand.prototype.executeCommand = function () {
    if (this.commands === null) {
        this.commands = this.getRemoveOClassCommands();
    } else OArchitectUtil.inverseArray(this.commands);
    if (this.oClass.existsInDb) {
        this.prepareCommandsForExistsInDb();
    }
    this.executeCommands(this.commands);
    this.removed = !this.removed;
    this.oClass.removed = this.removed;
};

OClassRemoveCommand.prototype.getRemoveOClassCommands = function () {
    var commands = [];
    var geometry = this.oClass.cell.geometry;
    addRemoveEdgesCommand(this.oClass.cell);
    addRemovePropertiesCommand(this.oClass.properties);
    commands.push(new OClassCreateCommand(this.oClass, geometry.x, geometry.y, true));

    function addRemovePropertiesCommand(properties) {
        OArchitectUtil.forEach(properties, function (property) {
            if (!property.isSubClassProperty() || !property.isSuperClassExistsInEditor()) {
                commands.push(new OPropertyRemoveCommand(property));
            }
        });
    }

    function addRemoveEdgesCommand(cell) {
        var edges = app.editor.graph.getEdges(cell);
        OArchitectUtil.forEach(edges, function (edge) {
            commands.push(new OConnectionManageCommand(edge.source, edge.target, true));
        });
    }

    return commands;
};

/**
 * Connection command
 **/
var OConnectionManageCommand = function (sourceCell, targetCell, remove) {
    OArchitectRemoveCommand.apply(this, [remove == null ? false : remove]);
    this.sourceCell = sourceCell;
    this.targetCell = targetCell;
    this.remove = remove == null ? false : remove;
};

OConnectionManageCommand.prototype = Object.create(OArchitectRemoveCommand.prototype);
OConnectionManageCommand.prototype.constructor = OConnectionManageCommand;

OConnectionManageCommand.prototype.executeCommand = function () {
    if (this.commands === null) {
        this.commands = this.getCommands(this.remove);
        if (this.remove) {
            OArchitectUtil.inverseArray(this.commands);
        }
    } else OArchitectUtil.inverseArray(this.commands);

    if (this.sourceCell.value.existsInDb && this.remove) {
        this.prepareCommandsForExistsInDb();
    }
    this.executeCommands(this.commands);
    this.removed = !this.removed;
};

OConnectionManageCommand.prototype.getCommands = function (remove) {
    var commands = [];
    var sourceValue = this.sourceCell.value;
    var targetValue = this.targetCell.value;

    if (sourceValue instanceof OArchitectOProperty && this.targetCell.value instanceof OArchitectOClass) {
        commands.push(new OPropertyLinkChangeCommand(sourceValue, targetValue, remove));
    }

    if (isInverse(sourceValue, this.targetCell.value)) {
        commands.push(new OPropertyInverseChangeCommand(sourceValue, sourceValue.inversePropertyEnable, targetValue, remove));
    } else if (isSyncInverse(sourceValue, this.targetCell.value)) {
        commands.push(new OPropertyInverseChangeCommand(sourceValue, sourceValue.inversePropertyEnable, targetValue, remove));
    } else if (sourceValue instanceof OArchitectOClass) {
        commands.push(new OClassInheritanceCommand(sourceValue, targetValue, remove));
    }

    function isInverse(value, targetValue) {
        return value instanceof OArchitectOProperty && targetValue instanceof OArchitectOClass && value.inverseProperty !== null;
    }

    function isSyncInverse(value, targetValue) {
        return targetValue instanceof OArchitectOProperty && isInverse(value, targetValue.ownerClass) && isInverse(targetValue, value.ownerClass);
    }

    return commands;
};