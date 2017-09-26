/**
 * Static class which for connect {@link OArchitectOClass} / {@link OArchitectOProperty} cell with {@link OArchitectOClass}
 */
var OArchitectConnector = {

    disableCounter: 0,

    disable: function () {
        this.disableCounter++;
    },

    enable: function () {
        this.disableCounter--;
    },

    isEnable: function () {
        return this.disableCounter === 0;
    },

    /**
     * Create connection between sourceCell and targetCell
     * Function don't remove edge between sourceCell and targetCell!
     * @param sourceCell - {@link mxCell} cell with source class
     * @param targetCell - {@link mxCell} cell with target class
     */
    connect: function(sourceCell, targetCell) {
        var edge = null;
        if (this.isEnable()) {
            this.disable();
            if (sourceCell != null && targetCell != null) {
                var graph = app.editor.graph;
                if (sourceCell.value instanceof OArchitectOClass) {
                    edge = this.connectSubClassCellWithSuperClassCell(graph, sourceCell, targetCell);
                } else if (sourceCell.value instanceof OArchitectOProperty && targetCell.value instanceof OArchitectOProperty) {
                    edge = this.connectInverseProperties(graph, sourceCell, targetCell);
                } else if (sourceCell.value instanceof OArchitectOProperty && sourceCell.value.canConnect()) {
                    edge = this.connectOPropertyCellWithOClassCell(graph, sourceCell, targetCell);
                }
            }
            this.enable();
        }
        return edge;
    },

    /**
     * Remove connection between sourceCell and targetCell
     * Function don't remove edge between sourceCell and targetCell!
     * @param sourceCell {@link mxCell}
     * @param targetCell {@link mxCell}
     */
    disconnect: function (sourceCell, targetCell) {
        if (this.isEnable()) {
            this.disable();
            if (sourceCell != null && targetCell != null) {
                var graph = app.editor.graph;
                if (sourceCell.value instanceof OArchitectOClass) {
                    this.disconnectSubClassCellFromSuperClassCell(graph, sourceCell, targetCell);
                } else if (sourceCell.value instanceof OArchitectOProperty && targetCell.value instanceof OArchitectOProperty) {
                    this.disconnectInverseProperties(graph, sourceCell, targetCell);
                } else if (sourceCell.value instanceof OArchitectOProperty) {
                    this.disconnectOPropertyFromOClassCell(graph, sourceCell, targetCell);
                }
            }
            this.enable();
        }
    },

    connectSubClassCellWithSuperClassCell: function (graph, subClassCell, superClassCell) {
        var edge = null;
        var subClass = subClassCell.value;
        var superClass = superClassCell.value;
        graph.getModel().beginUpdate();
        // subClass.saveState();
        // superClass.saveState();
        edge = subClass.addSuperClass(superClass);
        subClass.updateValueInCell();
        superClass.updateValueInCell();
        graph.getModel().endUpdate();
        return edge;
    },

    connectOPropertyCellWithOClassCell: function (graph, propertyCell, classCell) {
        var edge = null;
        var property = propertyCell.value;
        if (property.ownerClass instanceof OArchitectOClass) {
            graph.getModel().beginUpdate();
            edge = property.setLinkedClass(classCell != null ? classCell.value : null);
            graph.getModel().endUpdate();
        }
        return edge;
    },

    connectInverseProperties: function (graph, propertyCell, inversePropertyCell) {
        var edge = null;
        var property = propertyCell.value;
        var inverse = inversePropertyCell.value;
        if (propertyCell.value !== inversePropertyCell.value.inverseProperty && !property.inverseLock && !inverse.inverseLock) {
            console.warn('CONNECT INVERSE PROPERTY: ', app.editor.undoOrRedoRuns);
            property.setInversePropertyEnable(true);
            inverse.setInversePropertyEnable(true);
            property.setLinkedClass(inverse.ownerClass, true);
            inverse.setLinkedClass(property.ownerClass, true);
            // property.setInverseProperty(inverse, !app.editor.undoOrRedoRuns);
            // inverse.setInverseProperty(property, !app.editor.undoOrRedoRuns);
            property.setInverseProperty(inverse);
            inverse.setInverseProperty(property);
            edge = graph.getEdgesBetween(propertyCell, inversePropertyCell)[0];
            property.updateValueInCell();
            inverse.updateValueInCell();
        }
        return edge;
    },

    disconnectInverseProperties: function (graph, propertyCell, inversePropertyCell) {
        var property = propertyCell.value;
        var inverse = inversePropertyCell.value;
        if (property === inverse.inverseProperty && !property.inverseLock && !inverse.inverseLock) {
            console.warn('DISCONNECT INVERSE PROPERTY');
            property.setInverseProperty(null);
            inverse.setInverseProperty(null);
            property.setLinkedClass(null);
            inverse.setLinkedClass(null);
            property.updateValueInCell();
            inverse.updateValueInCell();
        }
    },

    disconnectSubClassCellFromSuperClassCell: function (graph, subClassCell, superClassCell) {
        var subClass = subClassCell.value;
        var superClass = superClassCell.value;
        graph.getModel().beginUpdate();
        subClass.saveState();
        superClass.saveState();
        subClass.removeSuperClass(superClass);
        superClass.removeSubClass(subClass);
        superClass.updateValueInCell();
        subClass.updateValueInCell();
        graph.getModel().endUpdate();
    },

    disconnectOPropertyFromOClassCell: function (graph, propertyCell) {
        // var property = propertyCell.value;
        // if (property.inverseProperty !== null) {
        //     app.editor.graph.getModel().execute(new OPropertyInverseChangeCommand(property, property.inversePropertyEnable, null));
        // }
        this.connectOPropertyCellWithOClassCell(graph, propertyCell, null);
    }
};
