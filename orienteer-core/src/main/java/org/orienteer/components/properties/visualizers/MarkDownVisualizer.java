package org.orienteer.components.properties.visualizers;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.model.MarkDownModel;
import org.pegdown.PegDownProcessor;

public class MarkDownVisualizer extends AbstractSimpleVisualizer
{
    private static final String NAME = "markdown";

    public MarkDownVisualizer()
    {
        super(NAME, false, OType.STRING);
    }

    @Override
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel)
    {
        switch (mode)
        {
            case VIEW:
                return new Label(id,new MarkDownModel((IModel<String>) valueModel)).setEscapeModelStrings(false);
            case EDIT:
                return new TextArea<String>(id, (IModel<String>) valueModel);
            default:
                return null;
        }
    }
}
