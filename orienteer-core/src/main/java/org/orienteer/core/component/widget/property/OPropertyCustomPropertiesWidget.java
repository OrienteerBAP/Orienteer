package org.orienteer.core.component.widget.property;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.widget.AbstractSchemaCustomPropertiesWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.model.OPropertyCustomModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show custom properties of an {@link OProperty}
 */
@Widget(id="property-custom", domain="property", tab="configuration", order=30, autoEnable=true)
public class OPropertyCustomPropertiesWidget extends
		AbstractSchemaCustomPropertiesWidget<OProperty> {
	
	public OPropertyCustomPropertiesWidget(String id, IModel<OProperty> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}

	@Override
	protected Collection<String> getOriginalCustomKeys() {
		return getModelObject().getCustomKeys();
	}

	@Override
	protected void addCustom(String key, String value) {
		getModelObject().setCustom(key, value);
	}

	@Override
	protected IModel<String> createCustomModel(
			IModel<OProperty> schemaObjectModel,
			IModel<String> customPropertyModel) {
		return new OPropertyCustomModel(schemaObjectModel, customPropertyModel);
	}

}
