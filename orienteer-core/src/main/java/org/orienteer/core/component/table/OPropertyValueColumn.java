package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * {@link AbstractModeMetaColumn} for {@link ODocument}s
 */
public class OPropertyValueColumn extends AbstractModeMetaColumn<ODocument, DisplayMode, OProperty, String>
{
	private static final long serialVersionUID = 1L;

	public OPropertyValueColumn(OProperty oProperty, IModel<DisplayMode> modeModel)
	{
		this(new OPropertyModel(oProperty), modeModel);
	}

	public OPropertyValueColumn(IModel<OProperty> criteryModel, IModel<DisplayMode> modeModel)
	{
		super(criteryModel, modeModel);
	}
	
	public OPropertyValueColumn(String sortProperty, OProperty oProperty, IModel<DisplayMode> modeModel)
	{
		this(sortProperty, new OPropertyModel(oProperty), modeModel);
	}

	public OPropertyValueColumn(String sortProperty, IModel<OProperty> criteryModel, IModel<DisplayMode> modeModel)
	{
		super(sortProperty, criteryModel, modeModel);
	}

	@Override
	protected <V> AbstractMetaPanel<ODocument, OProperty, V> newMetaPanel(
			String componentId, IModel<OProperty> criteryModel,
			IModel<ODocument> rowModel) {
		return new ODocumentMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new OPropertyNamingModel(getCriteryModel());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getFilter(final String componentId, FilterForm<?> form) {
		IModel<OProperty> propertyModel = getCriteryModel();
		OProperty property = propertyModel.getObject();
		return property != null ? getComponentForFiltering(componentId, propertyModel, (FilterForm<OQueryModel<?>>) form) : null;
	}

}
