package org.orienteer.architect.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.editor.CodeEditorPanel;
import org.orienteer.core.component.editor.CodeMirrorJs;
import org.orienteer.core.component.property.DisplayMode;

import java.util.Map;

/**
 * Panel for display Java sources
 */
public class JavaCodeEditorPanel extends CodeEditorPanel {

    public JavaCodeEditorPanel(String id, IModel<String> model, IModel<DisplayMode> displayModel) {
        super(id, model, displayModel);
    }

    @Override
    protected void configureEditorParams(Map<String, String> params) {
        params.put("name", "'Java editor'");
        params.put("mode", "'text/x-java'");
    }

    @Override
    protected void setEditorDependencies(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.C_LIKE_MODE.getResourceReference()));
    }
}
