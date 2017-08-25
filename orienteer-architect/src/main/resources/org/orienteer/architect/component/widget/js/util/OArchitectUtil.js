/**
 * Static class which contains utility methods.
 */
var OArchitectUtil = {
    createOClassVertex: function (oClass, x, y) {
        if (x === undefined) x = 0;
        if (y === undefined) y = 0;
        var vertex = new mxCell(oClass,
            new mxGeometry(x, y, OArchitectConstants.OCLASS_WIDTH, OArchitectConstants.OCLASS_HEIGHT));
        vertex.setVertex(true);
        vertex.setStyle(OArchitectConstants.OCLASS_EDITOR_STYLE);
        return vertex;
    },

    createOPropertyVertex: function (property) {
        var vertex = new mxCell(property,
            new mxGeometry(0, 0, 0, OArchitectConstants.OPROPERTY_HEIGHT), OArchitectConstants.OPROPERTY_EDITOR_STYLE);
        vertex.setVertex(true);
        return vertex;
    },

    deleteCells: function (cells, withoutChecks) {

        deleteCells(app.editor.graph, getCellsForRemove(cells));

        function deleteCells(graph, cells) {
            graph.getModel().beginUpdate();
            try {
                graph.removeCells(cells, true);
            } finally {
                graph.getModel().endUpdate();
            }
        }

        function getCellsForRemove(cells) {
            var result = [];
            OArchitectUtil.forEach(cells, function (cell) {
                if (cell.edge) {
                    if (cell.source.value instanceof OArchitectOProperty) {
                        if (cell.source.value.canDisconnect() || withoutChecks) result.push(cell);
                    } else result.push(cell);
                } else result.push(cell);
            });
            return result;
        }
    },

    getOClassesAsJSON: function (graph) {
        var withParents = [];
        var withoutParents = [];
        var cells = graph.getChildVertices(graph.getDefaultParent());

        OArchitectUtil.forEach(cells, function (cell) {
            var oClass = cell.value;
            if (oClass.isSubClass()) withParents.push(oClass.toJson());
            else withoutParents.push(oClass.toJson());
        });

        return '[' + withoutParents.concat(withParents).join(',') + ']';
    },

    getEditorXmlNode: function (graph) {
        var encoder = new mxCodec();
        return encoder.encode(graph.getModel());
    },

    getAllClassCells: function () {
        var graph = app.editor.graph;
        return graph.getChildVertices(graph.getDefaultParent());
    },

    getAllClasses: function () {
        var cells = OArchitectUtil.getAllClassCells();
        var classes = [];
        OArchitectUtil.forEach(cells, function (cell) {
            classes.push(cell.value);
        });
        return classes;
    },

    toClassNames: function (classes) {
        var names = [];
        OArchitectUtil.forEach(classes, function (oClass) {
            names.push(oClass.name);
        });
        return names;
    },

    getAllClassNames: function () {
        return OArchitectUtil.toClassNames(OArchitectUtil.getAllClasses());
    },

    manageEdgesBetweenCells:   function (sourceCell, targetCell, connect) {
        var graph = app.editor.graph;
        var edgesBetween = graph.getEdgesBetween(sourceCell, targetCell);
        graph.getModel().beginUpdate();
        try {
            if (connect) {
                if (edgesBetween == null || edgesBetween.length == 0) {
                    graph.connectionHandler.connect(sourceCell, targetCell);
                }
            } else {
                if (edgesBetween != null && edgesBetween.length > 0) {
                    graph.removeCells(edgesBetween, true);
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
    },

    removeCell: function (cell, includeEdges) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        try {
            graph.removeCells([cell], includeEdges);
        } finally {
            graph.getModel().endUpdate();
        }
    },

    isCellDeletable: function (cell) {
        if (cell == null) return false;
        if (cell.isEdge() && cell.source != null) {
            if (cell.source.value instanceof OArchitectOClass) {
                return !cell.source.value.existsInDb;
            } else if (cell.source.value instanceof OArchitectOProperty) {
                return !cell.source.value.ownerClass.existsInDb;
            }
        }
        return true;
    },

    getCellsByClassNames: function (classNames) {
        var result = [];
        var cells = OArchitectUtil.getAllClassCells();
        OArchitectUtil.forEach(cells, function (cell) {
            var oClass = cell.value;
            if (oClass != null && classNames.indexOf(oClass.name) > -1) {
                result.push(cell);
            }
        });

        return result;
    },

    isValidPropertyTarget: function (cell) {
        var valid = false;

        if (cell.value instanceof OArchitectOClass) {
            valid = !cell.value.existsInDb;
        } else if (cell.value instanceof OArchitectOProperty) {
            var classCell = OArchitectUtil.getClassByPropertyCell(cell);
            valid = classCell != null && !classCell.value.existsInDb;
        }
        return valid;
    },

    getCellByClassName: function (className) {
        var result = null;
        var cells = OArchitectUtil.getAllClassCells();
        OArchitectUtil.forEach(cells, function (cell, trigger) {
            if (cell.value.name === className) {
                result = cell;
                trigger.stop = true;
            }
        });
        return result;
    },

    getClassPropertiesCells: function (oClass) {
        var graph = app.editor.graph;
        var cells = graph.getChildVertices(oClass.cell);
        var result = [];
        OArchitectUtil.forEach(cells, function (cell) {
            if (cell.value instanceof OArchitectOProperty) {
                if (oClass.getProperty(cell.value.name) != null) {
                    result.push(cell);
                }
            }
        });
        return result;
    },

    getClassByPropertyCell: function (cell) {
        var graph = app.editor.graph;
        if (cell.value instanceof OArchitectOClass)
            return cell;
        if (cell === graph.getDefaultParent())
            return null;
        return this.getClassByPropertyCell(graph.getModel().getParent(cell));
    },

    existsOClassInGraph: function (graph, className) {
        var exists = false;
        var cells = graph.getChildVertices(graph.getDefaultParent());
        OArchitectUtil.forEach(cells, function (cell, trigger) {
            if (cell.value.name.toUpperCase() === className.toUpperCase()) {
                exists = true;
                trigger.stop = true;
            }
        });
        return exists;
    },

    forEach: function (arr, func) {
        if (arr != null && arr.length > 0 && func != null) {
            var trigger = {
                stop: false
            };
            for (var i = 0; i < arr.length; i++) {
                func(arr[i], trigger);
                if (trigger.stop) break;
            }
        }
    },

    /**
     * Creates function for save {@link OArchitectOClass} and {@link OArchitectOProperty} to editor xml config.
     * Overrides {@link mxObjectCodec#writeComplexAttribute}
     * @returns Function
     */
    createWriteComplexAttributeFunction: function () {
        var defaultBehavior = mxObjectCodec.prototype.writeComplexAttribute;
        return function (enc, obj, name, value, node) {
            if (value instanceof OArchitectOClass || value instanceof OArchitectOProperty) {
                value = value.toEditorConfigObject();
            } else if (name === 'cell' || name === 'configuredFromEditorConfig' || name === 'existsInEditor') {
                value = undefined;
            }

            defaultBehavior.apply(this, arguments);
        };
    },

    /**
     * Create function for decode {@link OArchitectOClass} and {@link OArchitectOProperty} from editor xml config.
     * Overrides {@link mxCodec#decode}
     * @returns Function
     */
    createDecodeFunction: function () {
        var defaultBehavior = mxCodec.prototype.decode;
        
        return function (node, into) {
            var result = defaultBehavior.apply(this, arguments);
            if (into instanceof mxGraphModel) {
                var graph = app.editor.graph;
                var classCells = graph.getChildVertices(graph.getDefaultParent());
                OArchitectUtil.forEach(classCells, function (classCell) {
                    var oClass = classCell.value;
                    oClass.configFromCell(classCell);
                });
            }
            return result;
        }
    }
};
