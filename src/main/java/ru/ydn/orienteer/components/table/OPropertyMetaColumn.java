package ru.ydn.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OIndexMetaPanel;
import ru.ydn.orienteer.components.properties.OPropertyMetaPanel;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyMetaColumn  extends AbstractMetaColumn<OProperty, String, String>
{
	public OPropertyMetaColumn(String critery)
	{
		this(Model.of(critery));
	}

	public OPropertyMetaColumn(IModel<String> criteryModel)
	{
		super(criteryModel);
	}

	public OPropertyMetaColumn(String sortProperty, IModel<String> criteryModel)
	{
		super(sortProperty, criteryModel);
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
