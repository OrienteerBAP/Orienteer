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

    getOClassesAsJSON: function (graph) {
        const OCLASS_TAG  = 'OArchitectOClass';
        const PARENT_ATTR = 'parent';
        var node = OArchitectUtil.getEditorXmlNode(graph);
        var classesXml = node.getElementsByTagName(OCLASS_TAG);
        var withParents = [];
        var withoutParents = [];
        var codec = new mxCodec();

        OArchitectUtil.forEach(classesXml, function (classNode) {
            var child = false;
            if (classNode.getAttribute(PARENT_ATTR)) {
                child = true;
            }
            var oClass = codec.decode(classNode);
            if (child) withParents.push(JSON.stringify(oClass));
            else withoutParents.push(JSON.stringify(oClass));
        });

        return '[' + withoutParents.concat(withParents).join(',') + ']';
    },

    getEditorXmlNode: function (graph) {
        var encoder = new mxCodec();
        return encoder.encode(graph.getModel());
    },

    getOClassSuperClassesCells: function (graph, oClass) {
        var superClasses = oClass.superClassesNames;
        var superClassesCells = [];
        if (superClasses !== null) {
            var cells = graph.getChildVertices(graph.getDefaultParent());
            OArchitectUtil.forEach(superClasses, function (superClass) {
                var cell = OArchitectUtil.getCellByClassName(cells, superClass);
                if (cell !== null) superClassesCells.push(cell);
            });
        }
        return superClassesCells;
    },

    getCellByClassName: function (cells, className) {
        var result = null;
        OArchitectUtil.forEach(cells, function (cell, trigger) {
            if (cell.value.name === className) {
                result = cell;
                trigger.stop = true;
            }
        });
        return result;
    },

    getOClassSubClassesCells: function (graph, superClass) {
        var subClassesCells = [];
        var cells = graph.getChildVertices(graph.getDefaultParent());
        if (cells !== null && cells.length > 0) {
            OArchitectUtil.forEach(cells, function (classCell) {
                var oClass = classCell.value;
                var superClasses = oClass.superClassesNames;
                if (superClasses.indexOf(superClass.name) > -1) {
                    subClassesCells.push(classCell);
                }
            });
        }
        return subClassesCells;
    },

    getOPropertiesCellsInOClassCell: function (graph, properties, classCell) {
        var cells = [];
        var allPropertiesCells = graph.getChildVertices(classCell);
        if (properties == null) {
            cells = allPropertiesCells;
        } else {
           OArchitectUtil.forEach(allPropertiesCells, function (propertyCell) {
               if (OArchitectUtil.propertyContainsInProperties(propertyCell.value, properties)) {
                   cells.push(propertyCell);
               }
           });
        }
        return cells;
    },

    getPropertyCellFromPropertiesCells: function (propertiesCells, property) {
        var propertyCell = null;
        OArchitectUtil.forEach(propertiesCells, function (cell, trigger) {
            if (cell.value.name === property.name && cell.value.type === property.type) {
                propertyCell = cell;
                trigger.stop = true;
            }
        });
        return propertyCell;
    },

    fromCellsToOClasses: function (cells) {
        var classes = [];
        if (cells !== null && cells.length > 0) {
            OArchitectUtil.forEach(cells, function (cell) {
                if (cell.value instanceof OArchitectOClass)
                    classes.push(cell.value);
            });
        }
        return classes;
    },

    fromJsonToOClasses: function (json) {
        var classes = [];
        var jsonClasses = JSON.parse(json);
        if (jsonClasses !== null && jsonClasses.length > 0) {
            for (var i = 0; i < jsonClasses.length; i++) {
                var oClass = new OArchitectOClass();
                oClass.config(jsonClasses[i]);
                classes.push(oClass);
            }
        }
        return classes;
    },

    searchOClassCell: function (graph, cell) {
        if (cell.value instanceof OArchitectOClass)
            return cell;
        if (cell === graph.getDefaultParent())
            return null;
        return this.searchOClassCell(graph, graph.getModel().getParent(cell));
    },

    existsOClassInGraph: function (graph, className) {
        var exists = false;
        var cells = graph.getChildVertices(graph.getDefaultParent());
        OArchitectUtil.forEach(cells, function (cell, trigger) {
            if (cell.value.name === className) {
                exists = true;
                trigger.stop = true;
            }
        });
        return exists;
    },

    propertyContainsInProperties: function (property, properties) {
        var contains = false;
        OArchitectUtil.forEach(properties, function (p, trigger) {
            if (p.name === property.name && p.type === property.type) {
                contains = true;
                trigger.stop = true;
            }
        });
        return contains;
    },

    forEach: function (arr, func) {
        if (arr !== null && arr.length > 0 && func !== null) {
            var trigger = {
                stop: false
            };
            for (var i = 0; i < arr.length; i++) {
                func(arr[i], trigger);
                if (trigger.stop) break;
            }
        }
    }
};
