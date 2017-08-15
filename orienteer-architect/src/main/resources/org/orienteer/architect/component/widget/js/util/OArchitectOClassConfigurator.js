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
        oClass.name = json.name;
        oClass.existsInDb = json.existsInDb;
        OArchitectOClassConfigurator.configProperties(oClass, json.properties, true);
        OArchitectOClassConfigurator.configClasses(oClass, json.superClasses, true, true);
        OArchitectOClassConfigurator.configClasses(oClass, json.subClasses, false, true);
    },

    /**
     * Config {@link OArchitectOClass} from {@link mxCell} which is saved in xml editor config
     * @param oClass - {@link OArchitectOClass} which will be config
     * @param classCell - {@link mxCell} which is saved in xml editor config
     */
    configOClassFromEditorConfig: function (oClass, classCell) {
        if (!oClass.configuredFromEditorConfig) {
            oClass.configuredFromEditorConfig = true;
            configure();

            function configure() {
                var superClassesNames = oClass.superClasses;
                var subClassesNames = oClass.subClasses;
                oClass.superClasses = [];
                oClass.subClasses = [];
                oClass.properties = [];
                oClass.setCell(classCell);
                OArchitectOClassConfigurator.configProperties(oClass, OArchitectUtil.getClassPropertiesCells(oClass), false);
                OArchitectOClassConfigurator.configClasses(oClass, superClassesNames, true, false);
                OArchitectOClassConfigurator.configClasses(oClass, subClassesNames, false, false);
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
                property = new OArchitectOProperty();
                property.configFromDatabase(oClass, configElement);
            } else {
                property = configElement.value;
                property.configFromEditorConfig(oClass, configElement);
            }
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
        OArchitectUtil.forEach(classesNames, function (className) {
            var classCell = OArchitectUtil.getCellByClassName(className);
            var configuredClass = null;
            if (classCell != null) {
                configuredClass = classCell.value;
                if (!isJson) {
                    OArchitectOClassConfigurator.configOClassFromEditorConfig(configuredClass, classCell);
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