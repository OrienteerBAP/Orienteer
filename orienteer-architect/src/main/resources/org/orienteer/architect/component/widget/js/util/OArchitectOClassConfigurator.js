/**
 * Static class for configure {@link OArchitectOClass} from JSON or XML config
 */
var OArchitectOClassConfigurator = {

    configuredClassNames: [],

    isOClassConfigured: function (name) {
        return this.configuredClassNames.indexOf(name) > 0;
    },

    addConfiguredClassName: function (name) {
        this.configuredClassNames.push(name);
    },

    removeConfiguredClassName: function (name) {
        var index = this.configuredClassNames.indexOf(name);
        if (index > -1) {
            this.configuredClassNames.splice(index, 1);
        }
    },

    clear: function () {
        this.configuredClassNames.splice(0, this.configuredClassNames.length);
    },

    /**
     * Config {@link OArchitectOClass} from json which is response from database
     * @param oClass - {@link OArchitectOClass} which will be config
     * @param json - string which contains json data
     */
    configOClassFromJson: function (oClass, json) {
        var graph = app.editor.graph;
        graph.getModel().beginUpdate();
        oClass.name = json.name;
        oClass.pageUrl = json.pageUrl;
        if (oClass.cell != null) oClass.setCell(oClass.cell);
        OArchitectOClassConfigurator.configProperties(oClass, json.properties, true);
        oClass.setExistsInDb(json.existsInDb);
        OArchitectOClassConfigurator.configClasses(oClass, json.superClasses, true, true);
        OArchitectOClassConfigurator.configClasses(oClass, json.subClasses, false, true);
        OArchitectOClassConfigurator.configExistClassesLinks(oClass);
        oClass.changePropertiesOrder();
        oClass.saveState(true, true);
        graph.getModel().endUpdate();
    },

    /**
     * Config {@link OArchitectOClass} from {@link mxCell} which is saved in xml editor config
     * @param oClass - {@link OArchitectOClass} which will be config
     * @param classCell - {@link mxCell} which is saved in xml editor config
     */
    configOClassFromCell: function (oClass, classCell) {
        if (!this.isOClassConfigured(oClass.name)) {
            this.addConfiguredClassName(oClass.name);
            configure();

            function configure() {
                var graph = app.editor.graph;
                graph.getModel().beginUpdate();
                oClass.setCell(classCell);
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
                property.configFromJson(oClass, configElement);
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
                        if (property.inverseProperty != null) {
                            var prop = oClass.getProperty(property.inverseProperty.name);
                            if (prop !== null) property.inverseProperty = prop;
                        }
                    } else {
                        property.setLinkedClass(oClass);
                        if (property.inverseProperty != null) {
                            var inverse = oClass.getProperty(property.inverseProperty.name);
                            if (inverse !== null) property.setInverseProperty(inverse);
                        }
                    }
                }
            });
            existsClass.setExistsInDb(existsClass.existsInDb);
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
                    console.warn('config class: ', configuredClass);
                    OArchitectOClassConfigurator.configOClassFromCell(configuredClass, classCell);
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