package ru.ydn.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.index.OIndex;

import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OIndexMetaPanel;

public class OIndexMetaColumn extends AbstractMetaColumn<OIndex<?>, String, String>
{
	public OIndexMetaColumn(String critery)
	{
		this(Model.of(critery));
	}

	public OIndexMetaColumn(IModel<String> criteryModel)
	{
		super(criteryModel);
	}

	public OIndexMetaColumn(String sortProperty, IModel<String> criteryModel)
	{
		super(sortProperty, criteryModel);
	}

	@Override
	protected <V> AbstractMetaPanel<OIndex<?>, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OIndex<?>> rowModel) {
		return new OIndexMetaPanel<V>(componentId, DisplayMode.VIEW.asModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new OIndexMetaPanel.OIndexFieldNameModel(getCriteryModel());
	}

}
