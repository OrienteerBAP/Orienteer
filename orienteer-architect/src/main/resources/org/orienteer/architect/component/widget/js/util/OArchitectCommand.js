
var OArchitectChangeCommand = function (cell) {
    this.cell = cell;
};

OArchitectChangeCommand.prototype.execute = function () {
    console.log('execute architect change command');
};

OArchitectChangeCommand.prototype.undo = function () {
    console.log('undo architect change command');
};

OArchitectChangeCommand.prototype.redo = function () {
    console.log('redo architect change command');
};

/**
 * Commands for properties
 */

var OPropertyCreateCommand = function (property, oClass, cell) {
    OArchitectChangeCommand.apply(this, [cell]);
    this.property = property;
    this.oClass = oClass;
};

OPropertyCreateCommand.prototype = Object.create(OArchitectChangeCommand.prototype);
OPropertyCreateCommand.prototype.constructor = OPropertyCreateCommand;

OPropertyCreateCommand.prototype.execute = function () {
    if (this.oClass.getPropertyIndex(this.property) === -1) {
        this.oClass.properties.push(this.property);
        this.oClass.createCellForProperty(this.property);
    } else this.undo();
};

OPropertyCreateCommand.prototype.undo = function () {
    console.warn('undo');
    if (this.oClass.getPropertyIndex(this.property) > -1) {
        app.editor.saveActions = false;
        this.oClass.removeProperty(this.property);
        app.editor.saveActions = true;
    }
};
