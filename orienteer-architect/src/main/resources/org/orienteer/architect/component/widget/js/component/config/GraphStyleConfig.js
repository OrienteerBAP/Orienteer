/**
 * Class for config graph styles
 * @param editor which contains graph for config
 * @constructor
 */
var GraphStyleConfig = function (editor) {
    this.editor = editor;
    this.graph = editor.graph;
};

GraphStyleConfig.prototype.config = function () {
    // mxConstants.CURSOR_MOVABLE_VERTEX = 'default';
    var stylesheet = this.graph.getStylesheet();
    stylesheet.putDefaultVertexStyle(this.createVertexStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_EDITOR_STYLE, this.createOClassStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_EXISTS_EDITOR_STYLE, this.createOClassExistsStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_EDITOR_STYLE, this.createOPropertyStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_EXISTS_EDITOR_STYLE, this.createOPropertyExistsStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_CONNECTION_STYLE, this.createOClassConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_CONNECTION_STYLE, this.createOPropertyConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_EXISTS_CONNECTION_STYLE, this.createOClassExistsConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_EXISTS_CONNECTION_STYLE, this.createOPropertyExistsConnectionStyle());
};

GraphStyleConfig.prototype.createOClassStyle = function () {
    var style = {};
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_SWIMLANE;
    style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
    style[mxConstants.STYLE_IMAGE_ALIGN] = mxConstants.ALIGN_RIGHT;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
    style[mxConstants.STYLE_GRADIENTCOLOR] = '#41B9F5';
    style[mxConstants.STYLE_FILLCOLOR] = '#8CCDF5';
    style[mxConstants.STYLE_SWIMLANE_FILLCOLOR] = '#ffffff';
    style[mxConstants.STYLE_STROKECOLOR] = '#1B78C8';
    style[mxConstants.STYLE_FONTCOLOR] = '#000000';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    style[mxConstants.STYLE_STARTSIZE] = '28';
    style[mxConstants.STYLE_VERTICAL_ALIGN] = 'middle';
    style[mxConstants.STYLE_FONTSIZE] = '12';
    style[mxConstants.STYLE_FONTSTYLE] = 1;
    return style;
};

/**
 * Create style for change color in classes which exists in database
 */
GraphStyleConfig.prototype.createOClassExistsStyle = function () {
    var style = this.createOClassStyle();
    style[mxConstants.STYLE_GRADIENTCOLOR] = '#6D5A72';
    style[mxConstants.STYLE_FILLCOLOR]     = '#8884FF';
    style[mxConstants.STYLE_STROKECOLOR]   = '#6D5A72';
    return style;
};

GraphStyleConfig.prototype.createOPropertyStyle = function () {
    var style = {};
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
    style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_FONTCOLOR] = '#000000';
    style[mxConstants.STYLE_FONTSIZE] = '11';
    style[mxConstants.STYLE_FONTSTYLE] = 0;
    style[mxConstants.STYLE_SPACING_LEFT] = '4';
    style[mxConstants.STYLE_STROKECOLOR] = '#1B78C8';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    return style;
};

/**
 * Create style for properties which exists in database
 */
GraphStyleConfig.prototype.createOPropertyExistsStyle = function () {
    var style = this.createOPropertyStyle();
    style[mxConstants.STYLE_STROKECOLOR] = '#6D5A72';
    return style;
};

GraphStyleConfig.prototype.createOClassConnectionStyle = function () {
    var style = {};
    style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
    style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
    style[mxConstants.STYLE_STARTFILL] = 0;
    style[mxConstants.STYLE_ENDFILL] = 0;
    style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_ORTHOGONAL;
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    return style;
};

GraphStyleConfig.prototype.createOPropertyConnectionStyle = function () {
    var style = {};
    style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_OPEN;
    style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_ORTHOGONAL;
    style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    return style;
};

GraphStyleConfig.prototype.createOClassExistsConnectionStyle = function () {
    var style = this.createOClassConnectionStyle();
    style[mxConstants.STYLE_STROKECOLOR] = '#6D5A72';
    return style;
};

GraphStyleConfig.prototype.createOPropertyExistsConnectionStyle = function () {
    var style = this.createOPropertyConnectionStyle();
    style[mxConstants.STYLE_STROKECOLOR] = '#6D5A72';
    return style;
};

GraphStyleConfig.prototype.createVertexStyle = function () {
    var style = {};
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
    style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_FONTCOLOR] = '#000000';
    return style;
};