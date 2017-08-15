/**
 * Global access for {@link OArchitectApplication} instance
 */
var app;

/**
 * Global access for {@link OArchitectApplication#localizer} instance. Contains localized strings.
 */
var localizer;

/**
 * Application class for 'orienteer-architect'
 * @param basePath
 * @param config
 * @param localizer
 * @param containerId
 * @param editorId
 * @param sidebarId
 * @param toolbarId
 * @constructor
 */
var OArchitectApplication = function (basePath, config, localizer, containerId, editorId, sidebarId, toolbarId) {
	this.basePath = basePath;
	this.config = mxUtils.parseXml(config);
	this.localizer = localizer;
    this.containerId = containerId;
    this.editorId = editorId;
    this.sidebarId = sidebarId;
    this.toolbarId = toolbarId;
    this.saveEditorConfigCallbackUrl = null;
    this.applyEditorChangesCallbackUrl = null;
    this.getOClassesRequestCallbackUrl = null;
    this.existsOClassRequestCallbackUrl = null;
    this.showMoreInformationCallbackUrl = null;
    this.checkChangesRequestCallbackUrl = null;
    this.editor = null;
    this.callback = null;
};

/**
 * Init application. Create editor and config it.
 */
OArchitectApplication.prototype.init = function () {
    if (mxClient.isBrowserSupported()) {
        localizer = this.localizer;
        this.editor = new OArchitectEditor(this.getEditorContainer());
        this.editor.configure(this.config.documentElement);
        this.configureEditorSidebar(this.editor);
        this.configureEditorToolbar(this.editor);
    } else mxUtils.error('Browser is not supported!', 200, false);
};

/**
 * Config editor sidebar.
 * Add OArchitectActionNames to editor sidebar.
 * @param editor {@link OArchitectEditor} for config
 */
OArchitectApplication.prototype.configureEditorSidebar = function (editor) {
    var sidebar = new OArchitectSidebar(editor, this.getSidebarContainer());
    sidebar.addAction(localizer.classMsg, OArchitectActionNames.ADD_OCLASS_ACTION, OArchitectAction.addOClassAction);
    sidebar.addAction(localizer.property, OArchitectActionNames.ADD_OPROPERTY_ACTION, OArchitectAction.addOPropertyAction);
    sidebar.addAction(localizer.existsClasses, OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION, OArchitectAction.addExistsOClassesAction);
};

/**
 * Config editor toolbar
 * @param editor {@link OArchitectEditor} for config
 */
OArchitectApplication.prototype.configureEditorToolbar = function (editor) {
    var toolbar = new OArchitectToolbar(editor, this.getToolbarContainer());
    toolbar.addAction(localizer.saveDataModel, OArchitectActionNames.SAVE_EDITOR_CONFIG_ACTION, OArchitectAction.saveEditorConfigAction);
    toolbar.addAction(localizer.applyChanges, OArchitectActionNames.APPLY_EDITOR_CHANGES_ACTION, OArchitectAction.applyEditorChangesAction);
    toolbar.addAction(localizer.toJson, OArchitectActionNames.TO_JSON_ACTION, OArchitectAction.toJsonAction);
};

/**
 * @returns editor element
 */
OArchitectApplication.prototype.getEditorContainer = function () {
    return $('#' + this.editorId).get(0);
};

/**
 * @returns sidebar element
 */
OArchitectApplication.prototype.getSidebarContainer = function () {
    return $('#' + this.sidebarId).get(0);
};

/**
 * @returns toolbar element
 */
OArchitectApplication.prototype.getToolbarContainer = function () {
    return $('#' + this.toolbarId).get(0);
};

/**
 * Calls from Wicket!
 * @param callbackUrl
 * @param xml
 */
OArchitectApplication.prototype.setSaveEditorConfig = function (callbackUrl, xml) {
    this.saveEditorConfigCallbackUrl = callbackUrl;
    mxObjectCodec.prototype.writeComplexAttribute = OArchitectUtil.createWriteComplexAttributeFunction();
    mxCodec.prototype.decode = OArchitectUtil.createDecodeFunction();
    if (xml) this.applyXmlConfig(xml);
};

/**
 * Calls from Wicket!
 * @param callbackUrl
 */
OArchitectApplication.prototype.setApplyEditorChanges = function (callbackUrl) {
    this.applyEditorChangesCallbackUrl = callbackUrl;
};

/**
 * Calls from Wicket!
 * @param callbackUrl
 */
OArchitectApplication.prototype.setGetOClassesRequest = function (callbackUrl) {
    this.getOClassesRequestCallbackUrl = callbackUrl;
};

/**
 * Calls from Wicket!
 * @param callbackUrl
 */
OArchitectApplication.prototype.setExistsOClassRequest = function (callbackUrl) {
    this.existsOClassRequestCallbackUrl = callbackUrl;
};

/**
 * Calls from Wicket!
 * Set url for request URL for OClass or OProperty
 * @param callbackUrl
 */
OArchitectApplication.prototype.setShowMoreInfoRequest = function (callbackUrl) {
    this.showMoreInformationCallbackUrl = callbackUrl;
};

/**
 * Calls from Wicket!
 * Set url for request changes in classes
 * @param callbackUrl
 */
OArchitectApplication.prototype.setChecksAboutClassesChanges = function (callbackUrl) {
    this.checkChangesRequestCallbackUrl = callbackUrl;
};

/**
 * Save editor config in database
 * @param xml config which will be saved
 */
OArchitectApplication.prototype.saveEditorConfig = function (xml) {
    this.sendPostRequest(this.saveEditorConfigCallbackUrl, {
        "config": xml
    });
};

/**
 * Apply editor changes. Save editor classes in database
 * @param json json string which contains editor classes
 */
OArchitectApplication.prototype.applyEditorChanges = function (json) {
    this.sendPostRequest(this.applyEditorChangesCallbackUrl, {
       "json": json
    });
};

/**
 * Create request for getting exists classes from database
 * @param json exists classes in editor
 * @param callback function which will be execute when get response
 */
OArchitectApplication.prototype.requestExistsOClasses = function (json, callback) {
    this.callback = callback;
    this.sendPostRequest(this.getOClassesRequestCallbackUrl, {
        "existsClasses": json
    });
};

/**
 * Create request for checks if given class name exists in database
 * @param name class name for check
 * @param callback function which will be execute when get response
 */
OArchitectApplication.prototype.requestIfOClassExists = function (name, callback) {
    this.callback = callback;
    this.sendPostRequest(this.existsOClassRequestCallbackUrl, {
        existsClassName: name
    });
};

/**
 * Create request for getting URL to OClass with name name
 * @param name class name
 * @param callback function which will be execute when get response
 */
OArchitectApplication.prototype.requestOClassPage = function (name, callback) {
    this.callback = callback;
    this.sendPostRequest(this.showMoreInformationCallbackUrl, {
        "class": name
    });
};

/**
 * Create request for getting URL to OProperty
 * @param className - class name with property
 * @param propertyName - property name
 * @param callback - function which will be execute when get response
 */
OArchitectApplication.prototype.requestOPropertyPage = function (className, propertyName, callback) {
    this.callback = callback;
    this.sendPostRequest(this.showMoreInformationCallbackUrl, {
        "class": className,
        "property": propertyName
    });
};

/**
 * Create request for getting changes in given classes
 * @param classesNames - json string which contains class names for checks
 * @param callback - function which will be execute when get response
 */
OArchitectApplication.prototype.requestAboutChangesInClasses = function (classesNames, callback) {
    this.callback = callback;
    this.sendPostRequest(this.checkChangesRequestCallbackUrl, {
        "classesNames": classesNames
    });
};

/**
 * Create POST request to Wicket
 * @param url callback url
 * @param data data for send contains JavaScript object
 */
OArchitectApplication.prototype.sendPostRequest = function (url, data) {
    Wicket.Ajax.post({
        "u": url,
        "ep": data
    });
};

/**
 * Calls from Wicket!
 * Execute callback from server
 * @param response data from server
 */
OArchitectApplication.prototype.executeCallback = function (response) {
    this.callback(response);
    this.callback = null;
};

/**
 * Apply editor config and checks about new changes
 * @param xml - editor config
 */
OArchitectApplication.prototype.applyXmlConfig = function (xml) {
    var doc = mxUtils.parseXml(xml);
    var codec = new mxCodec(doc);
    codec.decode(doc.documentElement, this.editor.graph.getModel());
    this.checksAboutClassesChanges();
};

/**
 * Checks about classes changes and update classes and editor config if its need.
 */
OArchitectApplication.prototype.checksAboutClassesChanges = function () {
    function callback(json) {
        if (json != null && json.length > 0) {
            var allClasses = OArchitectUtil.getAllClasses();
            var jsonClasses = JSON.parse(json);
            var saveNewConfig = false;
            OArchitectUtil.forEach(jsonClasses, function (jsonClass) {
                OArchitectUtil.forEach(allClasses, function (oClass, trigger) {
                    if (jsonClass.name === oClass.name) {
                        if (!oClass.equalsWithJsonClass(jsonClass)) {
                            saveNewConfig = true;
                            oClass.configFromDatabase(jsonClass);
                        }
                        trigger.stop = true;
                    }
                });
            });
            app.editor.execute(OArchitectActionNames.SAVE_EDITOR_CONFIG_ACTION);
        }
    }
    this.requestAboutChangesInClasses(JSON.stringify(OArchitectUtil.getAllClassNames()), callback);
};

/**
 * Calls from Wicket!
 * Create new instance {@link OArchitectApplication}.
 */
var init = function (basePath, config, localizer, containerId, editorId, sidebarId, toolbarId) {
    app = new OArchitectApplication(basePath, config, localizer, containerId, editorId, sidebarId, toolbarId);
    app.init();
};

/**
 * @deprecated
 * Calls from Wicket!
 * Init {@link mxGraph}
 * @param locale
 */
var initMxGraph = function(locale) {
    mxLanguage = locale;
};