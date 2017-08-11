/**
 * Static class for configure {@link OArchitectOClass} from JSON or XML config
 */
var OArchitectOClassConfigurator = {

    configOClassFromJSON: function (oClass, json) {
        oClass.name = json.name;
        OArchitectOClassConfigurator.configProperties(oClass, json.properties, true);
        OArchitectOClassConfigurator.configClasses(oClass, json.superClasses, true);
        OArchitectOClassConfigurator.configClasses(oClass, json.subClasses, false);
    },

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
                OArchitectOClassConfigurator.configClasses(oClass, superClassesNames, true);
                OArchitectOClassConfigurator.configClasses(oClass, subClassesNames, false);
            }
        }
    },

    configProperties: function (oClass, config, isJson) {
        OArchitectUtil.forEach(config, function (configElement) {
            var property = null;
            if (isJson) {
                property = new OArchitectOProperty();
                property.configFromJSON(oClass, configElement);
            } else {
                property = configElement.value;
                property.configFromEditorConfig(oClass, configElement);
            }
            console.warn('type: ', property.type);
        });
    },

    configClasses: function (oClass, classesNames, isSuperClasses) {
        OArchitectUtil.forEach(classesNames, function (className) {
            var classCell = OArchitectUtil.getCellByClassName(className);
            var configuredClass = null;
            if (classCell != null) {
                configuredClass = classCell.value;
            } else {
                configuredClass = new OArchitectOClass();
                configuredClass.name = className;
                configuredClass.existsInDb = true;
                configuredClass.existsInEditor = false;
            }
            if (isSuperClasses) oClass.addSuperClass(configuredClass);
            else oClass.addSubClass(configuredClass);
        });
    }
};