package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.filter.DataFilter;
import org.orienteer.core.component.table.filter.IODataFilter;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
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
	public Component getFilter(String componentId, FilterForm<?> form) {
		IModel<OProperty> propertyModel = getCriteryModel();
		UIVisualizersRegistry registry = OrienteerWebApplication.lookupApplication().getUIVisualizersRegistry();
		IVisualizer visualizer = registry.getComponentFactory(propertyModel.getObject().getType(), DataFilter.PROPERTY.getName());
		IFilterStateLocator<IODataFilter<ODocument, String>> stateLocator =
				(IFilterStateLocator<IODataFilter<ODocument, String>>) form.getStateLocator();
		IODataFilter<ODocument, String> filterState = stateLocator.getFilterState();
		IModel<?> model = filterState.getFilteredValueByProperty(propertyModel.getObject().getName());
		return visualizer.createComponent(componentId, DisplayMode.EDIT, null, propertyModel, model);
	}
}
