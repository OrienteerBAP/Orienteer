package org.orienteer.core.component.widget.property;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.widget.AbstractSchemaLocalizationWidget;
import org.orienteer.core.widget.Widget;

/**
 * Widget to show and modify {@link com.orientechnologies.orient.core.metadata.schema.OProperty} localization.
 */
@Widget(id="property-localization", domain="property", tab="localization", autoEnable=true)
public class OPropertyLocalizationWidget extends AbstractSchemaLocalizationWidget<OProperty> {

    public OPropertyLocalizationWidget(String id, IModel<OProperty> model, final IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);


    }

    @Override
    protected String getLocalizationKey(OProperty oProperty) {
    	return oProperty.getFullName();
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("property.localization");
    }

}
