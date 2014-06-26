package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public interface IPropertyDisplayPanel<T>
{
	public IModel<ODocument> getDocumentModel();
	
	public IModel<OProperty> getPropertyModel();
	
	public IModel<T> getValueModel();
}
