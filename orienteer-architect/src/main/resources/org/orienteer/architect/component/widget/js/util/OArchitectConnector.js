/**
 * Static class which for connect {@link OArchitectOClass} / {@link OArchitectOProperty} cell with {@link OArchitectOClass}
 */
var OArchitectConnector = {

    connect: function(graph, sourceCell, targetCell) {
        if (targetCell.value instanceof OArchitectOClass) {
            console.log('connect');
            if (sourceCell.value instanceof OArchitectOClass) {
                this.connectSubClassCellWithSuperClassCell(graph, sourceCell, targetCell);
            } else if (sourceCell.value instanceof OArchitectOProperty && sourceCell.value.canConnect()) {
                this.connectOPropertyCellWithOClassCell(graph, sourceCell, targetCell);
            }
        }
    },

    disconnect: function (graph, sourceCell, targetCell) {
        if (targetCell.value instanceof OArchitectOClass) {
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
        subClass.addSuperClassName(superClass.name);
    },

    connectOPropertyCellWithOClassCell: function (graph, propertyCell, classCell) {
        propertyCell.value.setLinkedClassName(classCell.value.name);
    },

    disconnectSubClassCellFromSuperClassCell: function (graph, subClassCell, superClassCell) {
        var subClass = subClassCell.value;
        var superClass = superClassCell.value;
        removeOClassInfo();
        removeOPropertyCells();

        function removeOClassInfo() {
            subClass.removeSuperClassName(superClass.name);
            OArchitectUtil.forEach(superClass.properties, function (superClassProperty) {
                subClass.removeProperty(superClassProperty);
            });
        }

        function removeOPropertyCells() {
            var propertiesForRemove = OArchitectUtil.getOPropertiesCellsInOClassCell(graph, superClass.properties, subClassCell);
            graph.getModel().beginUpdate();
            try {
                graph.removeCells(propertiesForRemove, true);
            } finally {
                graph.getModel().endUpdate();
            }
        }
    },

    disconnectOPropertyFromOClassCell: function (graph, propertyCell, classCell) {
        propertyCell.value.linkedClassName = null;
    }
};
