package org.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.CustomAttributes;
import org.orienteer.components.properties.AbstractMetaPanel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.properties.OIndexMetaPanel;
import org.orienteer.components.properties.OPropertyMetaPanel;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyMetaColumn  extends AbstractModeMetaColumn<OProperty, DisplayMode, String, String>
{
	public OPropertyMetaColumn(CustomAttributes custom, IModel<DisplayMode> modeModel)
	{
		this(custom.getName(), modeModel);
	}
	
	public OPropertyMetaColumn(String critery, IModel<DisplayMode> modeModel)
	{
		this(critery, critery, modeModel);
	}
	
	public OPropertyMetaColumn(String sortParam, String critery, IModel<DisplayMode> modeModel)
	{
		super(sortParam, Model.of(critery), modeModel);
	}

	@Override
	protected <V> AbstractMetaPanel<OProperty, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OProperty> rowModel) {
		return new OPropertyMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("property", getCriteryModel());
	}
}
