/**
 * Static class for configure {@link OArchitectOClass} from JSON or XML config
 */
var OArchitectOClassConfigurator = {

    /**
     * Config {@link OArchitectOClass} from json which is response from database
     * @param oClass - {@link OArchitectOClass} which will be config
     * @param json - string which contains json data
     */
    configOClassFromDatabase: function (oClass, json) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        oClass.name = json.name;
        oClass.pageUrl = json.pageUrl;
        if (oClass.cell != null) oClass.setCell(oClass.cell);
        OArchitectOClassConfigurator.configProperties(oClass, json.properties, true);
        OArchitectOClassConfigurator.configClasses(oClass, json.superClasses, true, true);
        OArchitectOClassConfigurator.configClasses(oClass, json.subClasses, false, true);
        OArchitectOClassConfigurator.configExistClassesLinks(oClass);
        oClass.setExistsInDb(json.existsInDb);
        graph.getModel().endUpdate();
    },

    /**
     * Config {@link OArchitectOClass} from {@link mxCell} which is saved in xml editor config
     * @param oClass - {@link OArchitectOClass} which will be config
     * @param classCell - {@link mxCell} which is saved in xml editor config
     */
    configOClassFromCell: function (oClass, classCell) {
        if (!oClass.configuredFromCell) {
            oClass.configuredFromCell = true;
            configure();

            function configure() {
                var graph = app.editor.graph;
                graph.getModel().beginUpdate();
                oClass.cell = classCell;
                var superClassesNames = oClass.superClasses;
                var subClassesNames = oClass.subClasses;
                var propertiesCells = OArchitectUtil.getClassPropertiesCells(oClass);
                oClass.superClasses = [];
                oClass.properties = [];
                oClass.subClasses = [];
                OArchitectOClassConfigurator.configProperties(oClass, propertiesCells, false);
                OArchitectOClassConfigurator.configClasses(oClass, superClassesNames, true, false);
                OArchitectOClassConfigurator.configClasses(oClass, subClassesNames, false, false);
                oClass.setExistsInDb(oClass.existsInDb);
                graph.getModel().endUpdate();
            }
        }
    },

    /**
     * Config properties of {@link OArchitectOClass}
     * @param oClass - {@link OArchitectOClass} which properties need to config
     * @param config - config element can be array with json strings or array with {@link mxCell}
     * @param isJson - true if config properties from array with json strings
     */
    configProperties: function (oClass, config, isJson) {
        OArchitectUtil.forEach(config, function (configElement) {
            var property = null;
            if (isJson) {
                var name = configElement.name;
                property = oClass.getProperty(name);
                if (property === null) property = new OArchitectOProperty();
                property.configFromDatabase(oClass, configElement);
            } else {
                property = configElement.value;
                property.configFromCell(oClass, configElement);
            }
        });
    },

    configExistClassesLinks: function (oClass) {
        OArchitectUtil.forEach(OArchitectUtil.getAllClassesInEditor(), function (existsClass) {
            OArchitectUtil.forEach(existsClass.properties, function (property) {
                if (property.linkedClass === oClass.name ||
                    property.linkedClass instanceof OArchitectOClass && property.linkedClass.name === oClass.name) {
                    if (property.isSubClassProperty() && property.isSuperClassExistsInEditor()) {
                        property.linkedClass = oClass;
                    } else property.setLinkedClass(oClass);
                }
            });
        });

    },

    /**
     * Config subclasses or superclasses of {@link OArchitectOClass}
     * @param oClass - {@link OArchitectOClass} which subclasses or superclasses need to config
     * @param classesNames - string array which contains names of subclasses or superclasses
     * @param isSuperClasses - true if need to config superclasses
     * @param isJson - true if config subclasses or super classes from json
     */
    configClasses: function (oClass, classesNames, isSuperClasses, isJson) {
        var classes = [];
        OArchitectUtil.forEach(classesNames, function (className) {
            var classCell = OArchitectUtil.getCellByClassName(className);
            var configuredClass = null;
            if (classCell != null) {
                configuredClass = classCell.value;
                if (!isJson) {
                    OArchitectOClassConfigurator.configOClassFromCell(configuredClass, classCell);
                    classes.push(configuredClass);
                }
            } else {
                configuredClass = new OArchitectOClass();
                configuredClass.name = className;
                configuredClass.existsInEditor = false;
            }
            if (isSuperClasses) oClass.addSuperClass(configuredClass);
            else oClass.addSubClass(configuredClass);
        });
    }
};