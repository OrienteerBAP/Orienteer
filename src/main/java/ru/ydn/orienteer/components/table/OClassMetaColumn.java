package ru.ydn.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OClassMetaPanel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassMetaColumn  extends AbstractModeMetaColumn<OClass, DisplayMode, String, String>
{
	public OClassMetaColumn(CustomAttributes custom, IModel<DisplayMode> modeModel)
	{
		this(custom.getName(), modeModel);
	}
	
	public OClassMetaColumn(String critery, IModel<DisplayMode> modeModel)
	{
		this(critery, critery, modeModel);
	}
	
	public OClassMetaColumn(String sortParam, String critery, IModel<DisplayMode> modeModel)
	{
		super(sortParam, Model.of(critery), modeModel);
	}

	@Override
	protected <V> AbstractMetaPanel<OClass, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OClass> rowModel) {
		return new OClassMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("class", getCriteryModel());
	}

}
