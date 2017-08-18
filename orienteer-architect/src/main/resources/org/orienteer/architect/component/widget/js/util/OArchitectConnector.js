/**
 * Static class which for connect {@link OArchitectOClass} / {@link OArchitectOProperty} cell with {@link OArchitectOClass}
 */
var OArchitectConnector = {

    /**
     * Create connection between sourceCell and targetCell
     * Function don't remove edge between sourceCell and targetCell!
     * @param sourceCell - {@link mxCell} cell with source class
     * @param targetCell - {@link mxCell} cell with target class
     * @param createLink - boolean if true create link from target class to source class
     */
    connect: function(sourceCell, targetCell, createLink) {
        if (sourceCell != null && targetCell != null) {
            if (targetCell.value instanceof OArchitectOClass) {
                var graph = app.editor.graph;
                if (sourceCell.value instanceof OArchitectOClass) {
                    if (createLink) {
                        this.createLinkForClass(graph, sourceCell, targetCell);
                    } else this.connectSubClassCellWithSuperClassCell(graph, sourceCell, targetCell);
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
        subClass.addSuperClass(superClass);
    },

    createLinkForClass: function (graph, sourceCell, targetCell) {
        var targetClass = targetCell.value;
        var property = new OArchitectOProperty();
        var modal = new OPropertyEditModalWindow(property, app.editorId, onDestroy, true);
        modal.orientDbTypes = OArchitectOType.linkTypes;
        var geometry = targetCell.geometry;
        modal.show(geometry.x - 20, geometry.y - 20);

        function onDestroy(property, event) {
            if (event === this.OK) {
                var property = targetClass.createProperty(property.name, property.type);
                property.setLinkedClass(sourceCell.value);
            } else modal.showErrorFeedback(localizer.cannotCreateLink);
        }
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
