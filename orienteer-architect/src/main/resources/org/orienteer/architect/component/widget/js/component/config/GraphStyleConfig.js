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
    var stylesheet = this.graph.getStylesheet();
    this.configEdgeConnectorStyles();
    stylesheet.putDefaultVertexStyle(this.createVertexStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_STYLE, this.createOClassStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_EXISTS_STYLE, this.createOClassExistsStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_STYLE, this.createOPropertyStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_EXISTS_STYLE, this.createOPropertyExistsStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_CONNECTION_STYLE, this.createOClassConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_CONNECTION_STYLE, this.createOPropertyConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_INVERSE_CONNECTION_STYLE, this.createOPropertyInverseConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OCLASS_EXISTS_CONNECTION_STYLE, this.createOClassExistsConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_EXISTS_CONNECTION_STYLE, this.createOPropertyExistsConnectionStyle());
    stylesheet.putCellStyle(OArchitectConstants.OPROPERTY_EXISTS_INVERSE_CONNECTION_STYLE, this.createOPropertyInverseExistsConnectionStyle());
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
    style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_SEGMENT;
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    style[mxConstants.STYLE_FONTSIZE] = '15';
    return style;
};

GraphStyleConfig.prototype.createOPropertyConnectionStyle = function () {
    var style = {};
    style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_OPEN;
    style[mxConstants.STYLE_EDGE] = mxEdgeStyle.SideToSide;
    style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
    style[mxConstants.STYLE_STROKEWIDTH] = '2';
    style[mxConstants.STYLE_FONTSIZE] = '15';
    return style;
};

GraphStyleConfig.prototype.createOPropertyInverseConnectionStyle = function () {
    var style = this.createOPropertyConnectionStyle();
    style[mxConstants.STYLE_STARTARROW] = mxConstants.ARROW_OPEN;
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
    style[mxConstants.STYLE_FONTCOLOR] = '#6D5A72';
    return style;
};

GraphStyleConfig.prototype.createOPropertyInverseExistsConnectionStyle = function () {
    var style = this.createOPropertyExistsConnectionStyle();
    style[mxConstants.STYLE_STARTARROW] = mxConstants.ARROW_OPEN;
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

GraphStyleConfig.prototype.configEdgeConnectorStyles = function () {
    mxEdgeStyle.SideToSide = function(state, source, target, points, result) {
        var view = state.view;
        var pt = (points != null && points.length > 0) ? points[0] : null;
        var pts = state.absolutePoints;
        var p0 = pts[0];
        var pe = pts[pts.length-1];
        if (pt != null)
        {
            pt = view.transformControlPoint(state, pt);
        }

        if (p0 != null)
        {
            source = new mxCellState();
            source.x = p0.x;
            source.y = p0.y;
        }

        if (pe != null)
        {
            target = new mxCellState();
            target.x = pe.x;
            target.y = pe.y;
        }

        if (source != null && target != null) {
            var l = Math.max(source.x, target.x);
            var r = Math.min(source.x + source.width,
                target.x + target.width);

            var x = (pt != null) ? pt.x : Math.round(r + (l - r) / 2);
            var y1 = view.getRoutingCenterY(source);
            var y2 = view.getRoutingCenterY(target);
            x = getPointX(x, source, target);
            if (pt != null)
            {
                if (pt.y >= source.y && pt.y <= source.y + source.height)
                {
                    y1 = pt.y;
                }

                if (pt.y >= target.y && pt.y <= target.y + target.height)
                {
                    y2 = pt.y;
                }
            }

            if (!mxUtils.contains(target, x, y1) &&
                !mxUtils.contains(source, x, y1))
            {
                result.push(new mxPoint(x,  y1));
            }

            if (!mxUtils.contains(target, x, y2) &&
                !mxUtils.contains(source, x, y2))
            {
                result.push(new mxPoint(x, y2));
            }

            if (result.length == 1)
            {
                if (pt != null)
                {
                    if (!mxUtils.contains(target, x, pt.y) &&
                        !mxUtils.contains(source, x, pt.y))
                    {
                        result.push(new mxPoint(x, pt.y));
                    }
                }
                else
                {
                    var t = Math.max(source.y, target.y);
                    var b = Math.min(source.y + source.height,
                        target.y + target.height);

                    result.push(new mxPoint(x, t + (b - t) / 2));
                }
            }
        }
    };

    function getPointX(x, source, target) {
        if (x >= target.x && x < target.x + target.width / 2) {
            x = target.x - 20;
        } else if (x >= target.x + target.width / 2 && x <= target.x + target.width) {
            x = target.x + target.width + 20;
        } else if (x >= source.x && x < source.x + source.width / 2) {
            x = source.x - 20;
        } else if (x >= source.x + source.width / 2 && x <= source.x + source.width) {
            x = source.x + source.width + 20;
        }
        return x;
    }
};