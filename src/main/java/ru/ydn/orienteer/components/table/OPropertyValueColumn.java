package ru.ydn.orienteer.components.table;

import org.apache.wicket.model.IModel;
import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.ODocumentMetaPanel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OPropertyValueColumn extends AbstractMetaColumn<ODocument, OProperty, String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OPropertyValueColumn(OProperty property)
	{
		this(new OPropertyModel(property));
	}
	
	public OPropertyValueColumn(IModel<OProperty> propertyModel)
	{
		super(new OPropertyNamingModel(propertyModel), propertyModel);
	}

	@Override
	protected <V> AbstractMetaPanel<ODocument, OProperty, V> newMetaPanel(
			String componentId, IModel<OProperty> criteryModel,
			IModel<ODocument> rowModel) {
		return new ODocumentMetaPanel<V>(componentId, DisplayMode.VIEW.asModel(), rowModel, criteryModel);
	}

}
