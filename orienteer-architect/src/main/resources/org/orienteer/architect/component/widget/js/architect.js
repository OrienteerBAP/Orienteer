
const ADD_OCLASS_ACTION           = 'addOClass';
const ADD_OPROPERTY_ACTION        = 'addOProperty';
const ADD_OCLASSES_ACTION         = 'addOClasses';
const EDIT_OPROPERTY_ACTION       = 'editOProperty';
const TO_JSON_ACTION              = 'toJsonAction';
const SAVE_EDITOR_CONFIG_ACTION   = 'saveEditorConfig';
const APPLY_EDITOR_CHANGES_ACTION = 'applyChanges';

const OCLASS_EDITOR_STYLE    = 'oClassName-style';
const OPROPERTY_EDITOR_STYLE = 'oProperty-style';

const SIDEBAR_ITEM_CLASS     = 'sidebar-item';
const TOOLBAR_ITEM_CLASS     = 'toolbar-item';
const BUTTON_PRIMARY_CLASS   = 'btn-primary';
const BUTTON_DANGER_CLASS    = 'btn-danger';
const FA_FILE_O_CLASS        = 'fa fa-file-o';
const FA_ALIGN_JUSTIFY_CLASS = 'fa fa-align-justify';
const FA_DATABASE_CLASS      = 'fa fa-database';
const FA_2X_CLASS            = 'fa-2x';

const OCLASS_WIDTH     = 150;
const OCLASS_HEIGHT    = 60;
const OPROPERTY_HEIGHT = 20;


const NAME_MSG             = 'Name';
const TYPE_MSG             = 'Type';
const CANCEL_MSG           = 'Cancel';
const OK_MSG               = 'OK';
const OPROPERT_ADD_ERR_MSG = 'OProperty must add only in OClass';
const INFO_MSG             = 'Info';
const OCLASS_MSG           = 'OClass';
const OPROPERTY_MSG        = 'OProperty';
const OCLASSES_MSG         = 'Exists OClasses';
const CREATE_OPROPERTY_MSG = 'Create OProperty';
const EDIT_OPROPERTY_MSG   = 'Edit OProperty';
const SAVE_DATA_MODEL_MSG  = 'Save Data Model';
const APPLY_CHANGES_MSG    = 'Apply Changes';
const TO_JSON_MSG          = 'To JSON';
const CHOOSE_CLASSES_MSG   = 'Choose super classes';

const CONNECTOR_IMG_PATH     = 'img/arrow.png';

const DEFAULT_OCLASS_NAME    = 'OClass';
const DEFAULT_OPROPERTY_NAME = 'OProperty';

const MAX_LABEL_LENGTH = 20;

var OArchitectApplication = function (basePath, config, containerId, editorId, sidebarId, toolbarId) {
	this.basePath = basePath;
	this.config = mxUtils.parseXml(config);
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
       this.editor = new SchemeEditor(this.getEditorContainer());
       this.editor.configure(this.config.documentElement);
       this.configureEditorSidebar(this.editor);
       this.configureEditorToolbar(this.editor);
       this.configurePopupMenu(this.editor);
    } else mxUtils.error('Browser is not supported!', 200, false);
};

OArchitectApplication.prototype.configureEditorSidebar = function (editor) {
    var sidebar = new Sidebar(editor, this.getSidebarContainer());
    sidebar.addAction(OCLASS_MSG, ADD_OCLASS_ACTION, addOClassAction);
    sidebar.addAction(OPROPERTY_MSG, ADD_OPROPERTY_ACTION, addOPropertyAction);
    sidebar.addAction(OCLASSES_MSG, ADD_OCLASSES_ACTION, addOClassesAction);
};

OArchitectApplication.prototype.configureEditorToolbar = function (editor) {
    var toolbar = new Toolbar(editor, this.getToolbarContainer());
    toolbar.addAction(SAVE_DATA_MODEL_MSG, SAVE_EDITOR_CONFIG_ACTION, saveEditorConfigAction);
    toolbar.addAction(APPLY_CHANGES_MSG, APPLY_EDITOR_CHANGES_ACTION, applyEditorChangesAction);
    toolbar.addAction(TO_JSON_MSG, TO_JSON_ACTION, toJsonAction);
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

var app;

var init = function (basePath, config, containerId, editorId, sidebarId, toolbarId) {
    app = new OArchitectApplication(basePath, config, containerId, editorId, sidebarId, toolbarId);
    app.init();
};

var initMxGraph = function(locale) {
    mxLanguage = locale;
};