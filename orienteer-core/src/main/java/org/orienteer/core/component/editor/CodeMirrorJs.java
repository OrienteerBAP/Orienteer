package org.orienteer.core.component.editor;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.io.IClusterable;

/**
 * Contains <a href="http://codemirror.net">CodeMirror</a> JavaScript dependencies.
 */
public enum CodeMirrorJs implements IClusterable {
    CORE("codemirror/current/lib/codemirror.js"),
    XML_MODE("codemirror/current/mode/xml/xml.js"),
    CSS_MODE("codemirror/current/mode/css/css.js"),
    JS_MODE("codemirror/current/mode/javascript/javascript.js"),
    HTMLMIXED_MODE("codemirror/current/mode/htmlmixed/htmlmixed.js"),
    SQL_MODE("codemirror/current/mode/sql/sql.js"),
    C_LIKE_MODE("codemirror/current/mode/clike/clike.js"),
    SHELL_MODE("codemirror/current/mode/shell/shell.js"),
    R_MODE("codemirror/current/mode/r/r.js"),
    MARKDOWN_MODE("codemirror/current/mode/markdown/markdown.js"),
    DOCKERFILE_MODE("codemirror/current/mode/dockerfile/dockerfile.js"),
    MATCH_TAG_ADDON("codemirror/current/addon/edit/matchtags.js"),
    FOLD_ADDON("codemirror/current/addon/fold/xml-fold.js"),
    AUTOCLOSE_TAGS_ADDON("codemirror/current/addon/edit/closetag.js"),
    MATCH_BRACKETS_ADDON("codemirror/current/addon/edit/matchbrackets.js"),
    AUTOCLOSE_BRACKETS_ADDON("codemirror/current/addon/edit/closebrackets.js"),
    ACTIVELINE_ADDON("codemirror/current/addon/selection/active-line.js"),
    SCROLLBAR_ADDON("codemirror/current/addon/scroll/simplescrollbars.js"),
    FULLSCREEN_ADDON("codemirror/current/addon/display/fullscreen.js"),
    SHOW_HINT_ADDON("codemirror/current/addon/hint/show-hint.js"),
    JS_HINT_ADDON("codemirror/current/addon/hint/javascript-hint.js"),
    CSS_HINT_ADDON("codemirror/current/addon/hint/css-hint.js"),
    XML_HINT_ADDON("codemirror/current/addon/hint/xml-hint.js"),
    HTML_HINT_ADDON("codemirror/current/addon/hint/html-hint.js"),
    SQL_HINT_ADDON("codemirror/current/addon/hint/sql-hint.js"),
    ANYWORD_HINT_ADDON("codemirror/current/addon/hint/anyword-hint.js"),;


    private final JavaScriptResourceReference resourceReference;

    CodeMirrorJs(String name) {
        resourceReference = new WebjarsJavaScriptResourceReference(name);
    }

    public JavaScriptResourceReference getResourceReference() {
        return resourceReference;
    }
}
