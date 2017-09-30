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
 * @param widgetId
 * @param containerId
 * @param editorId
 * @param sidebarId
 * @param toolbarId
 * @param outlineId
 * @param canUpdate
 * @constructor
 */
var OArchitectApplication = function (basePath, config, localizer, widgetId, containerId, editorId, sidebarId, toolbarId, outlineId, canUpdate) {
    this.basePath = basePath;
    this.config = mxUtils.parseXml(config);
    this.localizer = localizer;
    this.widgetId = widgetId;
    this.containerId = containerId;
    this.editorId = editorId;
    this.sidebarId = sidebarId;
    this.toolbarId = toolbarId;
    this.outlineId = outlineId;
    this.canUpdate = canUpdate;
};

/**
 * Contains link to {@link OArchitectEditor}
 */
OArchitectApplication.prototype.editor = null;

/**
 * string path to mxGraph resources
 */
OArchitectApplication.prototype.basePath = null;

/**
 * xml config for mxGraph editor
 */
OArchitectApplication.prototype.config = null;

/**
 * object which contains localized strings
 */
OArchitectApplication.prototype.localizer = null;

/**
 * string container id
 */
OArchitectApplication.prototype.containerId = null;

/**
 * string editor id
 */
OArchitectApplication.prototype.editorId = null;

/**
 * string sidebar id
 */
OArchitectApplication.prototype.sidebarId = null;

/**
 * string toolbar id
 */
OArchitectApplication.prototype.toolbarId = null;

/**
 * string outline id
 */
OArchitectApplication.prototype.outlineId = null;

/**
 * boolean if true current user can update editor classes
 * if false user can only read editor
 */
OArchitectApplication.prototype.canUpdate = false;

/**
 * string url to Wicket behavior which save editor xml config in database
 */
OArchitectApplication.prototype.saveEditorConfigCallbackUrl = null;

/**
 * string url to Wicket behavior which apply editor changes in database (creates classes and other)
 */
OArchitectApplication.prototype.applyEditorChangesCallbackUrl = null;

/**
 * string url to Wicket behavior which shows Wicket modal window with all classes in database
 */
OArchitectApplication.prototype.getOClassesRequestCallbackUrl = null;

/**
 * string url to Wicket behavior which checks if given class exists in database
 */
OArchitectApplication.prototype.existsOClassRequestCallbackUrl = null;

/**
 * string url to Wicket behavior which checks if given classes have changes in database
 */
OArchitectApplication.prototype.checkChangesRequestCallbackUrl = null;

/**
 * string url to Wicket behavior which switch fullscreen mode
 */
OArchitectApplication.prototype.switchFullScreenModeCallbackUrl = null;

/**
 * Contains callback function which executes every time after Wicket behavior response
 */
OArchitectApplication.prototype.callback = null;

/**
 * Init application. Create editor and config it.
 */
OArchitectApplication.prototype.init = function () {
    if (mxClient.isBrowserSupported()) {
        localizer = this.localizer;
        this.editor = new OArchitectEditor(this.getEditorContainer());
        this.editor.configure(this.config.documentElement);
        if (this.canUpdate) this.configureEditorSidebar(this.editor);
        if (this.canUpdate) this.configureEditorToolbar(this.editor);
        this.configureEditorOutline(this.editor);
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
    sidebar.addAction(localizer.existsClasses, OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION, OArchitectAction.addExistsOClassesAction);
    sidebar.addAction(localizer.property, OArchitectActionNames.ADD_OPROPERTY_ACTION, OArchitectAction.addOPropertyAction);
    editor.sidebar = sidebar;
};

/**
 * Config editor toolbar
 * @param editor {@link OArchitectEditor} for config
 */
OArchitectApplication.prototype.configureEditorToolbar = function (editor) {
    var toolbar = new OArchitectToolbar(editor, this.getToolbarContainer());
    toolbar.addAction(localizer.saveDataModel, OArchitectActionNames.SAVE_EDITOR_CONFIG_ACTION, OArchitectAction.saveEditorConfigAction);
    toolbar.addAction(localizer.applyChanges, OArchitectActionNames.APPLY_EDITOR_CHANGES_ACTION, OArchitectAction.applyEditorChangesAction);
    editor.toolbar = toolbar;
};

/**
 * Config outline for editor
 * @param editor {@link OArchitectEditor} for config
 */
OArchitectApplication.prototype.configureEditorOutline = function (editor) {
    editor.outline = new mxOutline(editor.graph, this.getOutlineContainer());
    this.getOutlineContainer().style.display = 'none';
    var msg = new OArchitectMessage(localizer.fullscreenMode);
    msg.show();
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
 * @returns outline element
 */
OArchitectApplication.prototype.getOutlineContainer = function () {
    return $('#' + this.outlineId).get(0);
};

/**
 * @returns application element
 */
OArchitectApplication.prototype.getApplicationContainer = function () {
    return $('#' + this.containerId).get(0);
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
 * @param callbackUrl
 */
OArchitectApplication.prototype.setSwitchFullScreenMode = function (callbackUrl) {
    this.switchFullScreenModeCallbackUrl = callbackUrl;
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
 * @param callback function which calls after saving editor config
 */
OArchitectApplication.prototype.saveEditorConfig = function (xml, callback) {
    this.callback = callback;
    this.sendPostRequest(this.saveEditorConfigCallbackUrl, {
        "config": xml
    });
};

/**
 * Switch fullscreen mode for editor
 * @param clickOnCommand - boolean true if click on widget command
 */
OArchitectApplication.prototype.switchFullScreenMode = function (clickOnCommand) {
    if (this.editor.fullScreenEnable) {
        this.editor.fullscreen = !this.editor.fullscreen;
        $('#' + this.containerId).toggleClass(OArchitectConstants.FULLSCREEN_CLASS);
        $('#' + this.editorId).toggleClass(OArchitectConstants.EDITOR_FULLSCREEN_CLASS);
        if (this.canUpdate) {
            $('#' + this.sidebarId).toggleClass(OArchitectConstants.SIDEBAR_FULLSCREEN_CLASS);
        }
        var outline = this.editor.outline.outline.container;
        if (this.editor.fullscreen) {
            outline.style.display = 'block';
            outline.style.right = '2px';
            if (app.canUpdate) {
                outline.style.top = app.getToolbarContainer().offsetHeight + 2 + 'px';
            } else outline.style.top = '2px';
            this.editor.outline.update();
        } else outline.style.display = 'none';
        if (!clickOnCommand) this.sendPostRequest(this.switchFullScreenModeCallbackUrl, {});
    }
};

/**
 * Calls FROM WICKET
 * Switch page scrolling
 */
OArchitectApplication.prototype.switchPageScrolling = function () {
    if (!this.editor.fullscreen) {
        $('body').toggleClass('noscroll');
    }
};


/**
 * Apply editor changes. Save editor classes in database
 * @param json - json string which contains editor classes
 * @param callback - function which calls after apply changes
 */
OArchitectApplication.prototype.applyEditorChanges = function (json, callback) {
    this.callback = callback;
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
    if (this.callback != null) {
        var callback = this.callback;
        this.callback = null;
        callback(response);
    }
};

/**
 * Apply editor config and checks about new changes
 * @param xml - editor config
 */
OArchitectApplication.prototype.applyXmlConfig = function (xml) {
    var doc = mxUtils.parseXml(xml);
    var codec = new mxCodec(doc);
    app.editor.beginUnsaveActions();
    codec.decode(doc.documentElement, this.editor.graph.getModel());
    this.checksAboutClassesChanges();
    OArchitectUtil.updateAllCells();
    app.editor.endUnsaveActions();
};

/**
 * Checks about classes changes and update classes and editor config if its need.
 */
OArchitectApplication.prototype.checksAboutClassesChanges = function (onCheckEnd) {
    function callback(json) {
        app.editor.beginUnsaveActions();
        if (json != null && json.length > 0) {
            var allClasses = OArchitectUtil.getAllClassesInEditor();
            var jsonClasses = JSON.parse(json);
            OArchitectUtil.updateExistsInDB(configFromDb(jsonClasses, allClasses));
        }

        function configFromDb(jsonClasses, allClasses) {
            var classes = [];
            OArchitectUtil.forEach(jsonClasses, function (jsonClass) {
                OArchitectUtil.forEach(allClasses, function (oClass, trigger) {
                    if (jsonClass.name === oClass.name) {
                        var equals = oClass.equalsWithJsonClass(jsonClass);
                        if (!equals) {
                            oClass.configFromJson(jsonClass);
                            classes.push(oClass);
                        }
                        oClass.setDatabaseJson(jsonClass);
                        trigger.stop = true;
                    }
                });
            });
            return classes;
        }
        if (onCheckEnd != null) onCheckEnd();
        app.editor.endUnsaveActions();
    }
    this.requestAboutChangesInClasses(JSON.stringify(OArchitectUtil.getAllClassNames()), callback);
};

/**
 * Calls from Wicket!
 * Create new instance {@link OArchitectApplication}.
 */
var init = function (basePath, config, localizer, widgetId, containerId, editorId, sidebarId, toolbarId, outlineId, canUpdate) {
    app = new OArchitectApplication(basePath, config, localizer, widgetId, containerId, editorId, sidebarId, toolbarId, outlineId, canUpdate);
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