package ru.ydn.orienteer.components.properties;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class MetaViewPanel<T> extends PropertyPanel<T> {
	private static final String PANEL_ID = "panel";
	private String propertyFullName;
	public MetaViewPanel(String id, IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel) {
		super(id, documentModel, propertyModel);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		OProperty property = getPropertyModel().getObject();
		String newPropertyFullName = property.getFullName();
		if(newPropertyFullName.equals(propertyFullName) || get(PANEL_ID)==null)
		{
			propertyFullName = newPropertyFullName;
			addOrReplace(resolvePanel(property).setRenderBodyOnly(true));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected PropertyPanel<T> resolvePanel(OProperty property)
	{
		switch(property.getType())
		{
			default:
				return (PropertyPanel<T>)new DefaultViewPanel(PANEL_ID, getDocumentModel(), getPropertyModel());
		}
	}
	
	
	
}
