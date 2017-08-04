
const ADD_OCLASS_ACTION      = 'addOClass';
const ADD_OPROPERTY_ACTION   = 'addOProperty';
const EDIT_OPROPERTY_ACTION  = 'editOProperty';

const SAVE_EDITOR_CONFIG_ACTION   = 'saveEditorConfig';
const APPLY_EDITOR_CHANGES_ACTION = 'applyChanges';

const OCLASS_EDITOR_STYLE    = 'oClassName-style';
const OPROPERTY_EDITOR_STYLE = 'oProperty-style';

const FA_FILE_O = 'fa fa-file-o';
const FA_ALIGN_JUSTIFY = 'fa fa-align-justify';
const FA_2X     = 'fa-2x';

const SIDEBAR_ITEM_CLASS = 'sidebar-item';
const TOOLBAR_ITEM_CLASS = 'toolbar-item';

const OCLASS_WIDTH     = 150;
const OCLASS_HEIGHT    = 60;
const OPROPERTY_HEIGHT = 20;

const BASE_PATH              = '../../org/orienteer/architect/component/widget';
const CONNECTOR_IMG_PATH     = BASE_PATH + '/img/arrow.png';
const CONFIG_PATH = BASE_PATH + '/js/config.xml';

const TO_JSON_ACTION = 'toJsonAction';

const NAME   = 'Name';
const TYPE   = 'Type';
const CANCEL = 'Cancel';
const OK     = 'OK';
const OPROPERT_ADD_ERR = 'OProperty must add only in OClass';
const INFO_MSG         = 'Info';

const BUTTON_PRIMARY = 'btn-primary';
const BUTTON_DANGER  = 'btn-danger';

const OCLASS    = 'OClass';
const OPROPERTY = 'OProperty';

const CREATE_OPROPERTY_MSG = 'Create OProperty';
const EDIT_OPROPERTY_MSG   = 'Edit OProperty';

var OArchitectApplication = function (containerId, editorId, sidebarId, toolbarId) {
    this.containerId = containerId;
    this.editorId = editorId;
    this.sidebarId = sidebarId;
    this.toolbarId = toolbarId;
    this.saveEditorConfig = null;
    this.applyEditorChanges = null;
    this.editor = null;
};

OArchitectApplication.prototype.init = function () {
    if (mxClient.isBrowserSupported()) {
       this.editor = new SchemeEditor(this.getEditorContainer());
       this.editor.configure(mxUtils.load(CONFIG_PATH).getDocumentElement());
       this.configureEditorSidebar(this.editor);
       this.configureEditorToolbar(this.editor);
       this.configurePopupMenu(this.editor);
    } else mxUtils.error('Browser is not supported!', 200, false);
};

OArchitectApplication.prototype.configureEditorSidebar = function (editor) {
    var sidebar = new Sidebar(editor, this.getSidebarContainer());
    sidebar.addAction('OClass', ADD_OCLASS_ACTION, addOClassAction);
    sidebar.addAction('OProperty', ADD_OPROPERTY_ACTION, addOPropertyAction);
};

OArchitectApplication.prototype.configureEditorToolbar = function (editor) {
    var toolbar = new Toolbar(editor, this.getToolbarContainer());
    toolbar.addAction('Save scheme', SAVE_EDITOR_CONFIG_ACTION, saveEditorConfigAction);
    toolbar.addAction('Apply changes', APPLY_EDITOR_CHANGES_ACTION, applyEditorChangesAction);
    toolbar.addAction('To JSON', TO_JSON_ACTION, toJsonAction);
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

OArchitectApplication.prototype.applyXmlConfig = function (xml) {
    var parser = new DOMParser();
    var node = parser.parseFromString(xml, 'text/xml');
    var codec = new mxCodec();
    codec.decode(node.documentElement, this.editor.graph.getModel());
};

var app;

var init = function (containerId, editorId, sidebarId, toolbarId) {
    app = new OArchitectApplication(containerId, editorId, sidebarId, toolbarId);
    app.init();
};

var initMxGraph = function(locale) {
    mxLanguage = locale;
};