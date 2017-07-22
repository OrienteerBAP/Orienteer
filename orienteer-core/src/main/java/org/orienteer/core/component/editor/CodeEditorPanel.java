package org.orienteer.core.component.editor;

import com.google.common.base.Strings;
import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.core.component.property.DisplayMode;

import java.util.HashMap;
import java.util.Map;

import static org.orienteer.core.component.editor.IEditorOptions.*;

/**
 * Editor panel which uses <a href="http://codemirror.net">CodeMirror framework</a>  for creating code editor.
 * For configure editor params see {@link CodeEditorPanel#configureEditorParams(Map<String, String>)}
 * For configure editor keys binding see {@link CodeEditorPanel#configureEditorKeysBinding(Map<String, String>)}
 */
public abstract class CodeEditorPanel extends FormComponentPanel<String> {

    private final IModel<DisplayMode> displayModel;

    private String convertedInput;

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
        WebMarkupContainer container = new WebMarkupContainer("container") {
            @Override
            public IMarkupFragment getMarkup(Component child) {
                return createMarkup(displayModel.getObject());
            }
        };
        container.add(createTextArea("editor"));
        add(container);
    }

    private Component createTextArea(String id) {
        TextArea<String> textArea = new TextArea<String>(id, getModel()) {
            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                Map<String, String> params = newParamsMap();
                Map<String, String> keysMap = newParamsMap();
                configureCustomEditorParams(params);
                configureCustomEditorKeysBinding(keysMap);
                configureEditorParams(params);
                configureEditorKeysBinding(keysMap);
                if (displayModel.getObject() == DisplayMode.EDIT) params.put(EXTRAKEYS, keysMap.toString());
                response.render(OnLoadHeaderItem.forScript(String.format(";editorInit('%s', %s);", getMarkupId(),
                        params.toString())));
            }

            @Override
            public void convertInput() {
                super.convertInput();
                CodeEditorPanel.this.setNewConvertedInput(getConvertedInput());
            }
        };
        return textArea;
    }

    /**
     * Create custom params for editor
     * @param params {@link Map<String, String>} params which contains fields of JavaScript object
     */
    protected void configureCustomEditorParams(Map<String, String> params) {
        boolean edit = displayModel.getObject() == DisplayMode.EDIT;
        params.put(READ_ONLY, Boolean.toString(!edit));
        params.put(CURSOR_BLINK_RATE, edit ? Integer.toString(530) : Integer.toString(-1));
        params.put(LINE_NUMBERS, "true");
        params.put(STYLE_ACTIVE_LINE, Boolean.toString(edit));
        params.put(SCROLLBAR_STYLE, "'overlay'");
    }

    /**
     * Create custom keys binding for editor
     * @param keysMap {@link Map<String, String>} keysMap which contains keys and function (fields of JavaScript object)
     */
    protected void configureCustomEditorKeysBinding(Map<String, String> keysMap) {
        keysMap.put("'F11'", "function(cm) {switchFullScreen(cm);}");
        keysMap.put("'Esc'", "function(cm) {disableFullscreen(cm);}");
    }

    /**
     * Create markup for {@link CodeEditorPanel} in different display modes.
     * @param mode {@link DisplayMode} if {@link DisplayMode#EDIT} editor configs for edit code
     *                                if {@link DisplayMode#VIEW} editor configs for view code
     * @return new {@link IMarkupFragment} for display correctly state of {@link CodeEditorPanel}
     */
    protected abstract IMarkupFragment createMarkup(DisplayMode mode);

    /**
     * Set editor dependencies.
     * Editor depends of CodeMirror. See <a href="http://codemirror.net">CodeMirror</a>
     * Orienteer's CodeMirror dependencies defines in classes {@link CodeMirrorJs} and {@link CodeMirrorCss}
     * @param response {@link IHeaderResponse} for add dependencies
     */
    protected abstract void setEditorDependencies(IHeaderResponse response);

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
     * @param params {@link Map<String, String>} which contains fields and values of JavaScript object
     */
    protected abstract void configureEditorParams(Map<String, String> params);

    /**
     * Configure editor keys binding. See <a href="http://codemirror.net">CodeMirror</a> for additional information.
     * Example:
     * keysMap.put("'Ctrl-Space'", "'autocomplete'");
     * Use '' for put JavaScript object field in this case.
     * @param keysMap {@link Map<String, String>} which contains fields and values of JavaScript object
     */
    protected abstract void configureEditorKeysBinding(Map<String, String> keysMap);


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        setCustomEditorDependencies(response);
        setEditorDependencies(response);
    }

    protected void setCustomEditorDependencies(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(CodeEditorPanel.class, "editor.js")));
        response.render(CssHeaderItem.forReference(CodeMirrorCss.CORE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.CORE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.ACTIVELINE_ADDON.getResourceReference()));
        addActiveLine(response);
        addScrollbar(response);
        addFullscreenMode(response);
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

    @Override
    public boolean isEnabled() {
        return displayModel.getObject() == DisplayMode.EDIT;
    }

    /**
     * @return {@link Map<String, String>} which has override method toString().
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
