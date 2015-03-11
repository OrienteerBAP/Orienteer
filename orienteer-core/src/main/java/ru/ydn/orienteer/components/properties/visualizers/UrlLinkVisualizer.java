package ru.ydn.orienteer.components.properties.visualizers;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import ru.ydn.orienteer.components.properties.DisplayMode;

public class UrlLinkVisualizer extends AbstractSimpleVisualizer
{
    private static final String NAME = "urlLink";
    public UrlLinkVisualizer()
    {
        super(NAME,false, OType.STRING);
    }
    @Override
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel)
    {
        switch (mode)
        {
            case VIEW:
                return new ExternalLink(id, (IModel<String>) valueModel,valueModel);
            case EDIT:
                return new TextField<String>(id, (IModel<String>) valueModel);
            default:
                return null;
        }
    }
}
