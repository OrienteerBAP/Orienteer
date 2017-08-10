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
        var withParents = [];
        var withoutParents = [];
        var cells = graph.getChildVertices(graph.getDefaultParent());

        OArchitectUtil.forEach(cells, function (cell) {
            var oClass = cell.value;
            if (oClass.isSubClass()) withParents.push(oClass.toJson());
            else withoutParents.push(oClass.toJson());
        });

        return '[' + withoutParents.concat(withParents).join(',\n') + ']';
    },

    getEditorXmlNode: function (graph) {
        var encoder = new mxCodec();
        return encoder.encode(graph.getModel());
    },

    getOClassSuperClassesCells: function (graph, oClass) {
        var superClasses = oClass.superClasses;
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
                var superClasses = oClass.superClasses;
                if (superClasses.indexOf(superClass.name) > -1) {
                    subClassesCells.push(classCell);
                }
            });
        }
        return subClassesCells;
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

    getClassByPropertyCell: function (graph, cell) {
        if (cell.value instanceof OArchitectOClass)
            return cell;
        if (cell === graph.getDefaultParent())
            return null;
        return this.getClassByPropertyCell(graph, graph.getModel().getParent(cell));
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
    }
};
