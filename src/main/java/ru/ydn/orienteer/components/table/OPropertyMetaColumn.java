package ru.ydn.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OIndexMetaPanel;
import ru.ydn.orienteer.components.properties.OPropertyMetaPanel;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyMetaColumn  extends AbstractMetaColumn<OProperty, String, String>
{
	public OPropertyMetaColumn(CustomAttributes custom)
	{
		this(custom.getName());
	}
	
	public OPropertyMetaColumn(String critery)
	{
		this(critery, critery);
	}
	
	public OPropertyMetaColumn(String sortParam, String critery)
	{
		super(sortParam, Model.of(critery));
	}

	@Override
	protected <V> AbstractMetaPanel<OProperty, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OProperty> rowModel) {
		return new OPropertyMetaPanel<V>(componentId, DisplayMode.VIEW.asModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new OPropertyMetaPanel.OPropertyFieldNameModel(getCriteryModel());
	}
}
