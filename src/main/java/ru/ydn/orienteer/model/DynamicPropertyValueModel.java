package ru.ydn.orienteer.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DynamicPropertyValueModel<T> extends LoadableDetachableModel<T>
{
	private final IModel<ODocument> docModel;
	private final IModel<OProperty> propertyModel;
	
	public DynamicPropertyValueModel(IModel<ODocument> docModel, IModel<OProperty> propertyModel)
	{
		this.docModel = docModel;
		this.propertyModel = propertyModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T load() {
		ODocument doc = docModel.getObject();
		OProperty prop = propertyModel.getObject();
		if(doc==null || prop==null) return null;
		return (T) doc.field(prop.getName());
	}

	@Override
	protected void onDetach() {
		docModel.detach();
		propertyModel.detach();
	}
	
	
	
	
}
