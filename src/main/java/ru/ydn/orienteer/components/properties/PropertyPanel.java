package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class PropertyPanel<T> extends GenericPanel<ODocument>
{
	private IModel<OProperty> propertyModel;
	private IModel<T> valueModel;
	
	public PropertyPanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(id, documentModel);
		this.propertyModel = propertyModel;
		valueModel = new DynamicPropertyValueModel<T>(documentModel, propertyModel);
	}
	
	public IModel<ODocument> getDocumentModel()
	{
		return getModel();
	}
	
	public IModel<OProperty> getPropertyModel()
	{
		return propertyModel;
	}
	
	public IModel<T> getValueModel()
	{
		return valueModel;
	}
	
	@Override
	public void detachModel() {
		super.detachModel();
		valueModel.detach();
		propertyModel.detach();
	}
	
	
}
