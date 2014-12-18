package ru.ydn.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.index.OIndex;

import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OIndexMetaPanel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

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

}
