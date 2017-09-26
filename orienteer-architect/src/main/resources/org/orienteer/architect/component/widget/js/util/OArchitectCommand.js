
var OArchitectCommand = function () {};

OArchitectCommand.prototype.executeCommands = function (commands) {
    OArchitectUtil.forEach(commands, function (command) {
        command.execute();
    });
};

OArchitectCommand.prototype.execute = function () {
    console.log('execute architect command');
};

// var OArchitectObjectCreateCommand = function (edges) {
//     this.edges = edges;
// };
//
// OArchitectObjectCreateCommand.prototype.updateEdges = function (mainCell) {
//     OArchitectUtil.forEach(this.edges, function (edge) {
//         var cell = null;
//         if (edge.source.value === mainCell.value) {
//             if (edge.target.value instanceof OArchitectOProperty) {
//                 cell = getPropertyCell(edge.target.value.name, edge.target.value.ownerClass.name);
//             } else {
//                 cell = getClassCell(edge.target.value.name);
//             }
//             if (cell !== null) {
//                 // OArchitectConnector.connect(mainCell, cell);
//                 OArchitectConnector.disable = true;
//                 OArchitectUtil.manageEdgesBetweenCells(mainCell, cell, true);
//                 OArchitectConnector.disable = false;
//             }
//         } else if (edge.target.value === mainCell.value) {
//             if (edge.source.value instanceof OArchitectOProperty) {
//                 cell = getPropertyCell(edge.source.value.name, edge.source.value.ownerClass.name);
//             } else {
//                 cell = getClassCell(edge.source.value.name);
//             }
//             if (cell !== null) {
//                 OArchitectConnector.disable = true;
//                 OArchitectUtil.manageEdgesBetweenCells(cell, mainCell, true);
//                 OArchitectConnector.disable = false;
//             }
//         }
//     });
//
//     function getClassCell(className) {
//         return OArchitectUtil.getCellByClassName(className);
//     }
//
//     function getPropertyCell(propertyName, className) {
//         var classCell = OArchitectUtil.getCellByClassName(className);
//         var property = classCell !== null ? classCell.value.getProperty(propertyName) : null;
//         return property !== null ? property.cell : null;
//     }
// };

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

OPropertyCreateCommand.prototype.execute = function () {
    OArchitectConnector.disable();
    app.editor.saveActions = false;
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
    app.editor.saveActions = true;
    OArchitectConnector.enable();
};


var OPropertyNameAndTypeChangeCommand = function (property, name, type) {
    OArchitectCommand.apply(this, []);
    this.property = property;
    this.name = name !== undefined ? name : null;
    this.type = type !== undefined ? type : null;
};

OPropertyNameAndTypeChangeCommand.prototype = Object.create(OArchitectCommand.prototype);
OPropertyNameAndTypeChangeCommand.prototype.constructor = OPropertyNameAndTypeChangeCommand;

OPropertyNameAndTypeChangeCommand.prototype.execute = function () {
    this.property = this.init(this.property);
    app.editor.saveActions = false;
    var tmpName = this.property.name;
    var tmpType = this.property.type;
    this.property.setName(this.name);
    this.property.setType(this.type);
    this.name = tmpName;
    this.type = tmpType;
    this.property.updateValueInCell();
    app.editor.saveActions = true;
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

var OPropertyLinkChangeCommand = function (property, linkedClass) {
    OPropertyNameAndTypeChangeCommand.apply(this, [property, null]);
    this.linkedClass = linkedClass;
};

OPropertyLinkChangeCommand.prototype = Object.create(OPropertyNameAndTypeChangeCommand.prototype);
OPropertyLinkChangeCommand.prototype.constructor = OPropertyLinkChangeCommand;

OPropertyLinkChangeCommand.prototype.execute = function () {
    this.property = this.init(this.property);
    app.editor.saveActions = false;
    // OArchitectConnector.disable();
    var tmp = this.property.linkedClass;
    this.property.setLinkedClass(this.linkedClass);
    // OArchitectConnector.enable();
    this.linkedClass = tmp;
    app.editor.saveActions = true;
};

var OPropertyInverseChangeCommand = function (property, inversePropertyEnable, inverseProperty) {
    OPropertyNameAndTypeChangeCommand.apply(this, [property]);
    this.inversePropertyEnable = inversePropertyEnable;
    this.inverseProperty = inverseProperty;
};

OPropertyInverseChangeCommand.prototype = Object.create(OPropertyNameAndTypeChangeCommand.prototype);
OPropertyInverseChangeCommand.prototype.constructor = OPropertyInverseChangeCommand;

OPropertyInverseChangeCommand.prototype.execute = function () {
    console.warn('inverse change command');
    app.editor.saveActions = false;
    this.property = this.init(this.property);
    if (this.inverseProperty !== null) this.inverseProperty = this.init(this.inverseProperty);
    var previousInversePropertyEnable = this.property.inversePropertyEnable;
    var previousInverseProperty = this.property.inverseProperty;
    this.property.setInversePropertyEnable(this.inversePropertyEnable);
    this.property.setInverseProperty(this.inverseProperty/*, !app.editor.undoOrRedoRuns*/);
    this.inversePropertyEnable = previousInversePropertyEnable;
    this.inverseProperty = previousInverseProperty;
    app.editor.saveActions = true;
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

OClassCreateCommand.prototype.execute = function () {
    app.editor.saveActions = false;
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
    app.editor.saveActions = true;
};


// OClassCreateCommand.prototype.updateProperties = function () {
//     var command = this;
//     OArchitectUtil.forEach(this.oClass.properties, function (property) {
//          command.updateEdges(property.cell);
//     });
// };

var OClassChangeNameCommand = function (oClass, name) {
    this.oClass = oClass;
    this.name = name;
};

OClassChangeNameCommand.prototype = Object.create(OClassChangeNameCommand.prototype);
OClassChangeNameCommand.prototype.constructor = OClassChangeNameCommand;

OClassChangeNameCommand.prototype.execute = function () {
    app.editor.saveActions = false;
    var tmp = this.oClass.name;
    this.oClass.name = this.name;
    this.name = tmp;
    this.oClass.updateValueInCell(true, true);
    app.editor.saveActions = true;
};


/**
 * Connection commands
 **/

var OConnectionManageCommand = function (sourceCell, targetCell, remove) {
    this.sourceCell = sourceCell;
    this.targetCell = targetCell;
    this.remove = remove == null ? false : remove;
    this.commands = null;
};

OConnectionManageCommand.prototype = Object.create(OArchitectCommand.prototype);
OConnectionManageCommand.prototype.constructor = OConnectionManageCommand;

OConnectionManageCommand.prototype.execute = function () {
    app.editor.saveActions = false;
    if (this.commands === null) {
        this.commands = this.getRemoveCommands();
    } else OArchitectUtil.inverseArray(this.commands);

    this.executeCommands(this.commands);

    if (this.remove) {
        console.warn('connection manager disconnect');
        OArchitectConnector.disconnect(this.sourceCell, this.targetCell);
    } else {
        console.warn('connection manager connect');
        OArchitectConnector.connect(this.sourceCell, this.targetCell);
        // var graph = app.editor.graph;
        // graph.addCell(edge, graph.getDefaultParent());
    }
    this.remove = !this.remove;
    app.editor.saveActions = true;
};

OConnectionManageCommand.prototype.getRemoveCommands = function () {
    var commands = [];
    if (isInverse(this.sourceCell.value, this.targetCell.value)) {
        var property = this.sourceCell.value;
        commands.push(new OPropertyInverseChangeCommand(property, property.inversePropertyEnable, null));
    }
    // else if (this.sourceCell.value instanceof OArchitectOClass && this.targetCell.value instanceof OArchitectOClass) {
    //
    // }

    function isInverse(value, targetValue) {
        return value instanceof OArchitectOProperty && targetValue instanceof OArchitectOClass && value !== null;
    }

    return commands;
};

/**
 * Macro commands
 **/
var OPropertyRemoveCommand = function (property) {
    this.property = property;
    this.removed = false;
    this.commands = null;
};

OPropertyRemoveCommand.prototype = Object.create(OArchitectCommand.prototype);
OPropertyRemoveCommand.prototype.constructor = OPropertyRemoveCommand;

OPropertyRemoveCommand.prototype.execute = function () {
    var model = app.editor.graph.getModel();
    var property = this.property;
    OArchitectConnector.disable();
    model.beginUpdate();
    if (this.commands === null) {
        this.commands = this.getRemovePropertyCommands(property);
    } else OArchitectUtil.inverseArray(this.commands);
    this.executeCommands(this.commands);
    this.removed = !this.removed;
    model.endUpdate();
    OArchitectConnector.enable();
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
    this.oClass = oClass;
    this.commands = null;
};

OClassRemoveCommand.prototype = Object.create(OArchitectCommand.prototype);
OClassRemoveCommand.prototype.constructor = OClassRemoveCommand;

OClassRemoveCommand.prototype.execute = function () {
    var model = app.editor.graph.getModel();
    model.beginUpdate();
    console.warn('execute oclass remove command');
    if (this.commands === null) {
        this.commands = this.getRemoveOClassCommands();
    } else OArchitectUtil.inverseArray(this.commands);
    this.executeCommands(this.commands);
    model.endUpdate();
};

OClassRemoveCommand.prototype.getRemoveOClassCommands = function () {
    var commands = [];
    var geometry = this.oClass.cell.geometry;

    addRemovePropertiesCommand(this.oClass.properties);
    commands.push(new OClassCreateCommand(this.oClass, geometry.x, geometry.y, true));

    function addRemovePropertiesCommand(properties) {
        OArchitectUtil.forEach(properties, function (property) {
            if (!property.isSubClassProperty() || !property.isSuperClassExistsInEditor()) {
                commands.push(new OPropertyRemoveCommand(property));
            }
        });
    }
    return commands;
};
