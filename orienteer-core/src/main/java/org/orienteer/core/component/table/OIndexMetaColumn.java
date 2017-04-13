package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.index.OIndex;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OIndexMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * {@link AbstractModeMetaColumn} for {@link OIndex}ies
 */
public class OIndexMetaColumn extends AbstractModeMetaColumn<OIndex<?>, DisplayMode, String, String>
{

	public OIndexMetaColumn(String critery, IModel<DisplayMode> modeModel)
	{
		this(critery, critery, modeModel);
	}
	
	public OIndexMetaColumn(String sortParam, String critery, IModel<DisplayMode> modeModel)
	{
		super(sortParam, Model.of(critery), modeModel);
	}

	@Override
	protected <V> AbstractMetaPanel<OIndex<?>, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OIndex<?>> rowModel) {
		return new OIndexMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("index", getCriteryModel());
	}

	@Override
	public Component getFilter(String componentId, FilterForm<?> form) {
		return null;
	}
}
