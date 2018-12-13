package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.editor.HtmlCssJsEditorPanel;
import org.orienteer.core.component.editor.IEditorOptions;
import org.orienteer.core.component.property.DisplayMode;

import java.util.Map;

/**
 * Visualizer for JavaScript code editor
 */
public class JavaScriptCodeVisualizer extends AbstractSimpleVisualizer {
	public static final String NAME = "javascript";
	
    public JavaScriptCodeVisualizer() {
        super(NAME, false, OType.STRING);
    }

    @Override
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        return new HtmlCssJsEditorPanel(id, (IModel<String>) valueModel, Model.of(mode)) {
            @Override
            protected void configureEditorParams(Map<String, String> params) {
                super.configureEditorParams(params);
                params.put(IEditorOptions.NAME, "'jsEditor'");
                params.put(IEditorOptions.MODE, "'javascript'");
            }
        };
    }
}
