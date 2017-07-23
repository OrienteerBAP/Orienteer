package org.orienteer.core.component.editor;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

import java.util.Map;

import static org.orienteer.core.component.editor.IEditorOptions.*;

/**
 * Implementation of {@link CodeEditorPanel} for create HTML, CSS, JavaScript code editor.
 */
public class HtmlCssJsEditorPanel extends CodeEditorPanel {

    public HtmlCssJsEditorPanel(String id, IModel<String> model, IModel<DisplayMode> displayModel) {
        super(id, model, displayModel);
    }

    @Override
    protected void configureEditorParams(Map<String, String> params) {
        params.put(NAME, "'htmlEditor'");
        params.put(MODE, "'htmlmixed'");
        params.put(MATCH_TAGS, "true");
        params.put(AUTOCLOSE_TAGS, "true");
    }


    @Override
    protected void setEditorDependencies(IHeaderResponse response) {
        addHtmlMixedMode(response);
        addMatchTags(response);
        addAutoCloseTags(response);
        addHtmlCssJsAutocomplete(response);
    }

    private void addHtmlMixedMode(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.XML_MODE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.JS_MODE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.CSS_MODE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.HTMLMIXED_MODE.getResourceReference()));
    }

    private void addMatchTags(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.MATCH_TAG_ADDON.getResourceReference()));
    }

    private void addAutoCloseTags(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.AUTOCLOSE_TAGS_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.FOLD_ADDON.getResourceReference()));
    }


    private void addHtmlCssJsAutocomplete(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.XML_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.HTML_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.CSS_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.JS_HINT_ADDON.getResourceReference()));
    }
}
