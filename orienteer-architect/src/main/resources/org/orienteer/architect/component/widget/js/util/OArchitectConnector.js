/**
 * Static class which for connect {@link OArchitectOClass} / {@link OArchitectOProperty} cell with {@link OArchitectOClass}
 */
var OArchitectConnector = {

    /**
     * Create connection between sourceCell and targetCell
     * Function don't remove edge between sourceCell and targetCell!
     * @param sourceCell - {@link mxCell} cell with source class
     * @param targetCell - {@link mxCell} cell with target class
     */
    connect: function(sourceCell, targetCell) {
        if (sourceCell != null && targetCell != null) {
            if (targetCell.value instanceof OArchitectOClass) {
                var graph = app.editor.graph;
                if (sourceCell.value instanceof OArchitectOClass) {
                    this.connectSubClassCellWithSuperClassCell(graph, sourceCell, targetCell);
                } else if (sourceCell.value instanceof OArchitectOProperty && sourceCell.value.canConnect()) {
                    this.connectOPropertyCellWithOClassCell(graph, sourceCell, targetCell);
                }
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
        graph.getModel().beginUpdate();
        try {
            subClass.saveState();
            superClass.saveState();
            subClass.addSuperClass(superClass);
            subClass.updateValueInCell();
            superClass.updateValueInCell();
        } finally {
            graph.getModel().endUpdate();
        }
    },

    connectOPropertyCellWithOClassCell: function (graph, propertyCell, classCell) {
        graph.getModel().beginUpdate();
        var property = propertyCell.value;
        if (property.ownerClass instanceof OArchitectOClass) {
            try {
                if (classCell != null) property.setAndSaveLinkedClass(classCell.value);
                else property.setAndSaveLinkedClass(null);
            } finally {
                graph.getModel().endUpdate();
            }
        }
    },

    disconnectSubClassCellFromSuperClassCell: function (graph, subClassCell, superClassCell) {
        var subClass = subClassCell.value;
        var superClass = superClassCell.value;
        graph.getModel().beginUpdate();
        try {
            subClass.saveState();
            superClass.saveState();
            subClass.removeSuperClass(superClass);
            superClass.removeSubClass(subClass);
            superClass.updateValueInCell();
            subClass.updateValueInCell();
        } finally {
            graph.getModel().endUpdate();
        }
    },

    disconnectOPropertyFromOClassCell: function (graph, propertyCell) {
        this.connectOPropertyCellWithOClassCell(graph, propertyCell, null);
    }
};
