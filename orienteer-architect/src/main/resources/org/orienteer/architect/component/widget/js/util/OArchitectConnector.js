/**
 * Static class which for connect {@link OArchitectOClass} / {@link OArchitectOProperty} cell with {@link OArchitectOClass}
 */
var OArchitectConnector = {

    /**
     * Create connection between sourceCell and targetCell
     * Function don't remove edge between sourceCell and targetCell!
     * @param sourceCell {@link mxCell}
     * @param targetCell {@link mxCell}
     */
    connect: function(sourceCell, targetCell) {
        if (targetCell.value instanceof OArchitectOClass) {
            var graph = app.editor.graph;
            if (sourceCell.value instanceof OArchitectOClass) {
                this.connectSubClassCellWithSuperClassCell(graph, sourceCell, targetCell);
            } else if (sourceCell.value instanceof OArchitectOProperty && sourceCell.value.canConnect()) {
                this.connectOPropertyCellWithOClassCell(graph, sourceCell, targetCell);
            }
        }
    },

    /**
     * Remove connection between sourceCell and targetCell
     * Function don't remove edge between sourceCell and targetCell!
     * @param sourceCell {@link mxCell}
     * @param targetCell {@link mxCell}
     */
    disconnect: function (sourceCell, targetCell) {
        if (targetCell.value instanceof OArchitectOClass) {
            var graph = app.editor.graph;
            if (sourceCell.value instanceof OArchitectOClass) {
                this.disconnectSubClassCellFromSuperClassCell(graph, sourceCell, targetCell);
            } else if (sourceCell.value instanceof OArchitectOProperty) {
                this.disconnectOPropertyFromOClassCell(graph, sourceCell, targetCell);
            }
        }
    },

    connectSubClassCellWithSuperClassCell: function (graph, subClassCell, superClassCell) {
        var subClass = subClassCell.value;
        var superClass = superClassCell.value;
        subClass.addSuperClass(superClass);
    },

    connectOPropertyCellWithOClassCell: function (graph, propertyCell, classCell) {
        propertyCell.value.setLinkedClass(classCell.value);
    },

    disconnectSubClassCellFromSuperClassCell: function (graph, subClassCell, superClassCell) {
        var subClass = subClassCell.value;
        var superClass = superClassCell.value;
        subClass.removeSuperClass(superClass);
    },

    disconnectOPropertyFromOClassCell: function (graph, propertyCell, classCell) {
        propertyCell.value.setLinkedClass(null);
    }
};
