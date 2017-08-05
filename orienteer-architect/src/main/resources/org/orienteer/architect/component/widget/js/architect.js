

var app;
var localizer;

var OArchitectApplication = function (basePath, config, localizer, containerId, editorId, sidebarId, toolbarId) {
	this.basePath = basePath;
	this.config = mxUtils.parseXml(config);
	this.localizer = localizer;
    this.containerId = containerId;
    this.editorId = editorId;
    this.sidebarId = sidebarId;
    this.toolbarId = toolbarId;
    this.saveEditorConfig = null;
    this.applyEditorChanges = null;
    this.getOClassesRequest = null;
    this.editor = null;
    this.callback = null;
};

OArchitectApplication.prototype.init = function () {
    if (mxClient.isBrowserSupported()) {
        localizer = this.localizer;
        this.editor = new SchemeEditor(this.getEditorContainer());
        this.editor.configure(this.config.documentElement);
        this.configureEditorSidebar(this.editor);
        this.configureEditorToolbar(this.editor);
        this.configurePopupMenu(this.editor);
    } else mxUtils.error('Browser is not supported!', 200, false);
};

OArchitectApplication.prototype.configureEditorSidebar = function (editor) {
    var sidebar = new Sidebar(editor, this.getSidebarContainer());
    sidebar.addAction(localizer.classMsg, ADD_OCLASS_ACTION, addOClassAction);
    sidebar.addAction(localizer.property, ADD_OPROPERTY_ACTION, addOPropertyAction);
    sidebar.addAction(localizer.existsClasses, ADD_EXISTS_OCLASSES_ACTION, addExistsOClassesAction);
};

OArchitectApplication.prototype.configureEditorToolbar = function (editor) {
    var toolbar = new Toolbar(editor, this.getToolbarContainer());
    toolbar.addAction(localizer.saveDataModel, SAVE_EDITOR_CONFIG_ACTION, saveEditorConfigAction);
    toolbar.addAction(localizer.applyChanges, APPLY_EDITOR_CHANGES_ACTION, applyEditorChangesAction);
    toolbar.addAction(localizer.toJson, TO_JSON_ACTION, toJsonAction);
};

OArchitectApplication.prototype.configurePopupMenu = function (editor) {
    editor.addAction(EDIT_OPROPERTY_ACTION, editOPropertyAction);
};

OArchitectApplication.prototype.getEditorContainer = function () {
    return $('#' + this.editorId).get(0);
};

OArchitectApplication.prototype.getSidebarContainer = function () {
    return $('#' + this.sidebarId).get(0);
};

OArchitectApplication.prototype.getToolbarContainer = function () {
    return $('#' + this.toolbarId).get(0);
};

OArchitectApplication.prototype.setSaveEditorConfig = function (func, xml) {
    this.saveEditorConfig = func;
    if (xml) this.applyXmlConfig(xml);
};

OArchitectApplication.prototype.setApplyEditorChanges = function (func) {
    this.applyEditorChanges = func;
};

OArchitectApplication.prototype.setGetOClassesRequest = function (func) {
    this.getOClassesRequest = func;
};

OArchitectApplication.prototype.requestOClasses = function (existsClasses, callback) {
    this.callback = callback;
    this.getOClassesRequest(existsClasses);
};

OArchitectApplication.prototype.executeCallback = function (json) {
    this.callback(json);
    this.callback = null;
};

OArchitectApplication.prototype.applyXmlConfig = function (xml) {
    var parser = new DOMParser();
    var node = parser.parseFromString(xml, 'text/xml');
    var codec = new mxCodec();
    codec.decode(node.documentElement, this.editor.graph.getModel());
};

var init = function (basePath, config, localizer, containerId, editorId, sidebarId, toolbarId) {
    app = new OArchitectApplication(basePath, config, localizer, containerId, editorId, sidebarId, toolbarId);
    app.init();
};

var initMxGraph = function(locale) {
    mxLanguage = locale;
};