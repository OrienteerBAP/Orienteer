package ru.ydn.orienteer.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Args;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DynamicPropertyValueModel<T> extends LoadableDetachableModel<T>
{
	private final IModel<ODocument> docModel;
	private final IModel<OProperty> propertyModel;
	
	public DynamicPropertyValueModel(IModel<ODocument> docModel, IModel<OProperty> propertyModel)
	{
		Args.notNull(docModel, "documentModel");
		this.docModel = docModel;
		this.propertyModel = propertyModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T load() {
		ODocument doc = docModel.getObject();
		OProperty prop = propertyModel!=null?propertyModel.getObject():null;
		if(doc==null) return null;
		if(prop==null) return (T) doc;
		return (T) doc.field(prop.getName());
	}

	@Override
	protected void onDetach() {
		docModel.detach();
		if(propertyModel!=null) propertyModel.detach();
	}
	
	
	
	
}
