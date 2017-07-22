package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.editor.HtmlCssJsEditorPanel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * Visualizer for HTML/CSS/JS code editor
 */
public class CodeVisualizer extends AbstractSimpleVisualizer {
    public CodeVisualizer() {
        super("code", false, OType.STRING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        return new HtmlCssJsEditorPanel(id, (IModel<String>) valueModel, Model.of(mode));
    }
}
