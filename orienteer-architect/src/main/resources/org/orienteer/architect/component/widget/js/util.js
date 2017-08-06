
var createOClassVertex = function (oClass, x, y) {
    if (x === undefined) x = 0;
    if (y === undefined) y = 0;
    var vertex = new mxCell(oClass, new mxGeometry(x, y, OCLASS_WIDTH, OCLASS_HEIGHT), OCLASS_EDITOR_STYLE);
    vertex.setVertex(true);
    return vertex;
};

var createOPropertyVertex = function (property) {
    var vertex = new mxCell(property,
        new mxGeometry(0, 0, 0, OPROPERTY_HEIGHT), OPROPERTY_EDITOR_STYLE);
    vertex.setVertex(true);
    return vertex;
};

var getOClassesAsJSON = function (graph) {
    const OCLASS_TAG  = 'OClass';
    const PARENT_ATTR = 'parent';
    var node = getEditorXmlNode(graph);
    var classesXml = node.getElementsByTagName(OCLASS_TAG);
    var withParents = [];
    var withoutParents = [];
    var codec = new mxCodec();

    forEach(classesXml, function (classNode) {
        var child = false;
        if (classNode.getAttribute(PARENT_ATTR)) {
            child = true;
        }
        var oClass = codec.decode(classNode);
        if (child) withParents.push(JSON.stringify(oClass));
        else withoutParents.push(JSON.stringify(oClass));
    });

    return '[' + withoutParents.concat(withParents).join(',') + ']';
};

var getEditorXmlNode = function (graph) {
    var encoder = new mxCodec();
    return encoder.encode(graph.getModel());
};

var getOClassSuperClassesCells = function (graph, oClass) {
    var superClasses = oClass.superClasses;
    var superClassesCells = [];
    if (superClasses !== null) {
        var cells = graph.getChildVertices(graph.getDefaultParent());
        forEach(superClasses, function (superClass) {
            var cell = getCellByClassName(cells, superClass);
            if (cell !== null) superClassesCells.push(cell);
        });
    }
    return superClassesCells;
};

var getCellByClassName = function (cells, className) {
    var result = null;
    forEach(cells, function (cell, trigger) {
        if (cell.value.name === className) {
            result = cell;
            trigger.stop = true;
        }
    });
    return result;
};

var getOClassSubClassesCells = function (graph, superClass) {
    var subClassesCells = [];
    var cells = graph.getChildVertices(graph.getDefaultParent());
    if (cells !== null && cells.length > 0) {
        forEach(cells, function (classCell) {
            var oClass = classCell.value;
            var superClasses = oClass.superClasses;
            if (superClasses.indexOf(superClass.name) > -1) {
                subClassesCells.push(classCell);
            }
        });
    }
    return subClassesCells;
};

var fromCellsToOClasses = function (cells) {
    var classes = [];
    if (cells !== null && cells.length > 0) {
        forEach(cells, function (cell) {
            if (cell.value instanceof OClass)
                classes.push(cell.value);
        });
    }
    return classes;
};

var fromJsonToOClasses = function (json) {
    var classes = [];
    var jsonClasses = JSON.parse(json);
    if (jsonClasses !== null && jsonClasses.length > 0) {
        for (var i = 0; i < jsonClasses.length; i++) {
            var oClass = new OClass();
            oClass.config(jsonClasses[i]);
            classes.push(oClass);
        }
    }
    return classes;
};


var searchOClassCell = function (graph, cell) {
    if (cell.value instanceof OClass)
        return cell;
    if (cell === graph.getDefaultParent())
        return null;
    return searchOClassCell(graph, graph.getModel().getParent(cell));
};


var forEach = function (arr, func) {
    if (arr !== null && arr.length > 0 && func !== null) {
        var trigger = {
            stop: false
        };
        for (var i = 0; i < arr.length; i++) {
            func(arr[i], trigger);
            if (trigger.stop) break;
        }
    }
};
