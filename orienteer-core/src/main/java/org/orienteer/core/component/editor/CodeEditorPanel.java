package org.orienteer.core.component.editor;

import com.google.common.base.Strings;
import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.core.component.property.DisplayMode;

import java.util.HashMap;
import java.util.Map;

import static org.orienteer.core.component.editor.IEditorOptions.*;

/**
 * Editor panel which uses <a href="http://codemirror.net">CodeMirror framework</a>  for creating code editor.
 * For configure editor params see {@link CodeEditorPanel#configureEditorParams(Map)}
 * For configure editor keys binding see {@link CodeEditorPanel#configureEditorKeysBinding(Map)}
 */
public abstract class CodeEditorPanel extends FormComponentPanel<String> {

    private final IModel<DisplayMode> displayModel;

    private String convertedInput;

    private TextArea<String> codeArea;
    private WebMarkupContainer handle;

    public CodeEditorPanel(String id, IModel<String> model, IModel<DisplayMode> displayModel) {
        super(id, model);
        this.displayModel = displayModel;
    }

    private void setNewConvertedInput(String input) {
        this.convertedInput = input;
    }

    @Override
    public void convertInput() {
        setConvertedInput(convertedInput);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupPlaceholderTag(true);
        handle = new WebMarkupContainer("handle");
        handle.setOutputMarkupId(true);
        WebMarkupContainer container = new WebMarkupContainer("container") {
            @Override
            public IMarkupFragment getMarkup(Component child) {
                if (child != null && child.getId().equals(handle.getId()))
                    return super.getMarkup(child);
                return createMarkup(displayModel.getObject());
            }
        };
        container.add(handle);
        container.add(codeArea = createTextArea("editor"));
        add(container);
    }

    private TextArea<String> createTextArea(String id) {
        return new TextArea<String>(id, CodeEditorPanel.this.getModel()) {
            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);

            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                setOutputMarkupPlaceholderTag(true);
            }

            @Override
            public void convertInput() {
                super.convertInput();
                CodeEditorPanel.this.setNewConvertedInput(getConvertedInput());
            }
        };
    }

    /**
     * Create custom params for editor
     * @param params {@link Map<String, String>} params which contains fields of JavaScript object
     */
    private void configureCustomEditorParams(Map<String, String> params) {
        boolean edit = displayModel.getObject() == DisplayMode.EDIT;
        params.put(READ_ONLY, Boolean.toString(!edit));
        params.put(CURSOR_BLINK_RATE, edit ? Integer.toString(530) : Integer.toString(-1));
        params.put(LINE_NUMBERS, "true");
        params.put(STYLE_ACTIVE_LINE, Boolean.toString(edit));
        params.put(SCROLLBAR_STYLE, "'overlay'");
        params.put(MATCH_BRACKETS, "true");
        params.put(AUTOCLOSE_BRACKETS, "true");
        params.put(THEME, "'eclipse'");
    }

    /**
     * Create custom keys binding for editor
     * @param keysMap {@link Map<String, String>} keysMap which contains keys and function (fields of JavaScript object)
     */
    private void configureCustomEditorKeysBinding(Map<String, String> keysMap) {
        keysMap.put("'F11'", "function(cm) {switchFullScreen(cm);}");
        keysMap.put("'Esc'", "function(cm) {disableFullscreen(cm);}");
        keysMap.put("'Ctrl-Space'", "'autocomplete'");
    }

    /**
     * Create markup for {@link CodeEditorPanel} in different display modes.
     * @param mode {@link DisplayMode} if {@link DisplayMode#EDIT} editor configs for edit code
     *                                if {@link DisplayMode#VIEW} editor configs for view code
     * @return new {@link IMarkupFragment} for display correctly state of {@link CodeEditorPanel}
     */
    protected IMarkupFragment createMarkup(DisplayMode mode) {
        return Markup.of("<textarea wicket:id='editor' class='form-control'></textarea>");
    }

    /**
     * Set editor dependencies.
     * Editor depends of CodeMirror. See <a href="http://codemirror.net">CodeMirror</a>
     * Orienteer's CodeMirror dependencies defines in classes {@link CodeMirrorJs} and {@link CodeMirrorCss}
     * @param response {@link IHeaderResponse} for add dependencies
     */
    protected void setEditorDependencies(IHeaderResponse response) {

    }

    /**
     * Configure editor params. See <a href="http://codemirror.net">CodeMirror</a> for additional information.
     * Example:
     * set name of editor: params.put("name", "'myName'");
     * set editor mode:    params.put("mode", "'javascript'");
     * enable match tags:  params.put("matchTags", "true");
     * Please use '' when you put value in params for JavaScript string value.
     * Example:
     * params.put("mode", "'javascript'") - in JavaScript object it's will be like this: {mode: 'javascript'}
     * params.put("matchTags", "true") - in JavaScript object it's will be like this: {matchTags: true}
     * @param params {@link Map} which contains fields and values of JavaScript object
     */
    protected void configureEditorParams(Map<String, String> params) {

    }

    /**
     * Configure editor keys binding. See <a href="http://codemirror.net">CodeMirror</a> for additional information.
     * Example:
     * keysMap.put("'Ctrl-Space'", "'autocomplete'");
     * Use '' for put JavaScript object field in this case.
     * @param keysMap {@link Map} which contains fields and values of JavaScript object
     */
    protected void configureEditorKeysBinding(Map<String, String> keysMap) {

    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        setCustomEditorDependencies(response);
        setEditorDependencies(response);
        initCodeMirror(response);
    }

    private void initCodeMirror(IHeaderResponse response) {
        Map<String, String> params = newParamsMap();
        Map<String, String> keysMap = newParamsMap();
        configureCustomEditorParams(params);
        configureCustomEditorKeysBinding(keysMap);
        configureEditorParams(params);
        configureEditorKeysBinding(keysMap);
        if (displayModel.getObject() == DisplayMode.EDIT) params.put(EXTRAKEYS, keysMap.toString());
        response.render(OnLoadHeaderItem.forScript(getInitJsCode(params)));
    }

    private void setCustomEditorDependencies(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(CodeEditorPanel.class, "editor.js")));
        response.render(CssHeaderItem.forReference(
                new CssResourceReference(CodeEditorPanel.class, "editor.css")));
        response.render(CssHeaderItem.forReference(CodeMirrorCss.CORE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.CORE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.ACTIVELINE_ADDON.getResourceReference()));
        addShowHintAddon(response);
        addActiveLine(response);
        addScrollbar(response);
        addFullscreenMode(response);
        addMatchBrackets(response);
        addAutoCloseBrackets(response);
        addEditorTheme(response);
    }

    private void addShowHintAddon(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CodeMirrorCss.SHOW_HINT_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.SHOW_HINT_ADDON.getResourceReference()));
    }

    private void addActiveLine(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.ACTIVELINE_ADDON.getResourceReference()));
    }

    private void addScrollbar(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CodeMirrorCss.SCROLLBAR_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.SCROLLBAR_ADDON.getResourceReference()));
    }

    private void addFullscreenMode(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CodeMirrorCss.FULLSCREEN_ADDON.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.FULLSCREEN_ADDON.getResourceReference()));
    }

    private void addMatchBrackets(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.MATCH_BRACKETS_ADDON.getResourceReference()));
    }

    private void addAutoCloseBrackets(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.AUTOCLOSE_BRACKETS_ADDON.getResourceReference()));
    }


    protected void addEditorTheme(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CodeMirrorCss.ECLIPSE_THEME.getResourceReference()));
    }

    @Override
    public boolean isEnabled() {
        return displayModel.getObject() == DisplayMode.EDIT;
    }

    private String getInitJsCode(Map<String, String> params) {
        return String.format("setTimeout(function() {editorInit('%s', '%s', %s);}, 0);",
                codeArea.getMarkupId(), handle.getMarkupId(), params.toString());
    }

    /**
     * @return {@link Map} which has override method toString().
     * toString() returns JavaScript object.
     */
    protected final Map<String, String> newParamsMap() {
        return new HashMap<String, String>() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                int counter = 0;
                for (String key: keySet()) {
                    String value = get(key);
                    if (!Strings.isNullOrEmpty(value)) {
                        if (counter > 0) sb.append(",");
                        sb.append(key)
                                .append(":")
                                .append(value);
                        counter++;
                    }
                }
                sb.append("}");
                return sb.toString();
            }
        };
    }
}
