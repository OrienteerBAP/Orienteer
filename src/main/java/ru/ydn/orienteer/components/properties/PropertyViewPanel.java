package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class PropertyViewPanel<T> extends GenericPanel<T> implements IPropertyDisplayPanel<T>
{
	private IModel<ODocument> documentModel;
	private IModel<OProperty> propertyModel;
	
	public PropertyViewPanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(id, new DynamicPropertyValueModel<T>(documentModel, propertyModel));
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
	}
	
	public IModel<ODocument> getDocumentModel()
	{
		return documentModel;
	}
	
	public IModel<OProperty> getPropertyModel()
	{
		return propertyModel;
	}
	
	public IModel<T> getValueModel()
	{
		return getModel();
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(documentModel!=null) documentModel.detach();
		if(propertyModel!=null) propertyModel.detach();
	}

}
