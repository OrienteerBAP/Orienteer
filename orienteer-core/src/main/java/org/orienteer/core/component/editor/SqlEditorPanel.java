package org.orienteer.core.component.editor;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

import java.util.Map;

/**
 *  Implementation of {@link CodeEditorPanel} for create SQL code editor.
 */
public class SqlEditorPanel extends CodeEditorPanel {
    public SqlEditorPanel(String id, IModel<String> model, IModel<DisplayMode> displayModel) {
        super(id, model, displayModel);
    }

    @Override
    protected void configureEditorParams(Map<String, String> params) {
        params.put(IEditorOptions.NAME, "'sqlEditor'");
        params.put(IEditorOptions.MODE, "'text/x-sql'");
    }

    @Override
    protected void setEditorDependencies(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.SQL_MODE.getResourceReference()));
        response.render(JavaScriptHeaderItem.forReference(CodeMirrorJs.SQL_HINT_ADDON.getResourceReference()));
    }
}
