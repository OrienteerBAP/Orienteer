package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DefaultViewPanel extends PropertyPanel<Object> {

	public DefaultViewPanel(String id, IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel) {
		super(id, documentModel, propertyModel);
		add(new Label("value", getValueModel()));
	}

	
}
