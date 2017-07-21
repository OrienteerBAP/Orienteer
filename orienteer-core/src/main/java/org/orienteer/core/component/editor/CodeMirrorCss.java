package org.orienteer.core.component.editor;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.io.IClusterable;

/**
 * Contains <a href="http://codemirror.net">CodeMirror</a> CSS dependencies.
 */
public enum CodeMirrorCss implements IClusterable {
    CORE("codemirror/current/lib/codemirror.css"),
    SCROLLBAR_ADDON("codemirror/current/addon/scroll/simplescrollbars.css"),
    FULLSCREEN_ADDON("codemirror/current/addon/display/fullscreen.css"),
    SHOW_HINT_ADDON("codemirror/current/addon/hint/show-hint.css");


    private final CssResourceReference resourceReference;

    CodeMirrorCss(String name) {
        resourceReference = new WebjarsCssResourceReference(name);
    }

    public CssResourceReference getResourceReference() {
        return resourceReference;
    }
}
