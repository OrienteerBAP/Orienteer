package org.orienteer.core.component.widget.property;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.widget.AbstractSchemaLocalizationWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show and modify {@link com.orientechnologies.orient.core.metadata.schema.OProperty} tooltips.
 */
@Widget(id="property-tooltip-localization", domain="property", tab="localization", autoEnable=true, order = 10)
public class OPropertyTooltipLocalizationWidget extends AbstractSchemaLocalizationWidget<OProperty>  {

	public OPropertyTooltipLocalizationWidget(String id, IModel<OProperty> model, final IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected String getLocalizationKey(OProperty oProperty) {
    	return oProperty.getFullName()+".$tooltip";
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("property.tooltip.localization");
    }
}
