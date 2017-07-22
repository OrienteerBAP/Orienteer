package org.orienteer.core.component.editor;

import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.head.CssHeaderItem;
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
    protected IMarkupFragment createMarkup(DisplayMode mode) {
        return Markup.of("<textarea wicket:id='editor' class='form-control'></textarea>");
    }

    @Override
    protected void configureEditorParams(Map<String, String> params) {
        params.put(NAME, "'htmlEditor'");
        params.put(MODE, "'htmlmixed'");
        params.put(MATCH_TAGS, "true");
        params.put(MATCH_BRACKETS, "true");
        params.put(AUTOCLOSE_BRACKETS, "true");
        params.put(AUTOCLOSE_TAGS, "true");
        params.put(THEME, "'eclipse'");
    }

    @Override
    protected void configureEditorKeysBinding(Map<String, String> keysMap) {
        keysMap.put("'Ctrl-Space'", "'autocomplete'");
    }

    @Override
    protected void setEditorDependencies(IHeaderResponse response) {
        addHtmlMixedMode(response);
        addMatchTags(response);
        addAutoCloseTags(response);
        addMatchBrackets(response);
        addAutoCloseBrackets(response);
        addHtmlCssJsAutocomplete(response);
        addEditorTheme(response);
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

    private void addMatchBrackets(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.MATCH_BRACKETS_ADDON.getResourceReference()));
    }

    private void addAutoCloseBrackets(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.AUTOCLOSE_BRACKETS_ADDON.getResourceReference()));
    }

    private void addHtmlCssJsAutocomplete(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CodeMirrorCss.SHOW_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.SHOW_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.XML_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.HTML_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.CSS_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.JS_HINT_ADDON.getResourceReference()));
    }

    private void addEditorTheme(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CodeMirrorCss.ECLIPSE_THEME.getResourceReference()));
    }
}
