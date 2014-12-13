package ru.ydn.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OClassMetaPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassMetaColumn  extends AbstractMetaColumn<OClass, String, String>
{
	public OClassMetaColumn(CustomAttributes custom)
	{
		this(custom.getName());
	}
	
	public OClassMetaColumn(String critery)
	{
		this(critery, critery);
	}
	
	public OClassMetaColumn(String sortParam, String critery)
	{
		super(sortParam, Model.of(critery));
	}

	@Override
	protected <V> AbstractMetaPanel<OClass, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OClass> rowModel) {
		return new OClassMetaPanel<V>(componentId, DisplayMode.VIEW.asModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new OClassMetaPanel.OClassFieldNameModel(getCriteryModel());
	}

}
