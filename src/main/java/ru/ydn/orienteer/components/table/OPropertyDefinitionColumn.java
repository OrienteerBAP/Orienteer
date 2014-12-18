package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OPropertyViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyDefinitionColumn extends OPropertyMetaColumn
{
	private static final long serialVersionUID = 1L;

	public OPropertyDefinitionColumn(CustomAttributes custom,
			IModel<DisplayMode> modeModel)
	{
		super(custom, modeModel);
	}

	public OPropertyDefinitionColumn(String critery,
			IModel<DisplayMode> modeModel)
	{
		super(critery, modeModel);
	}

	public OPropertyDefinitionColumn(String sortParam, String critery,
			IModel<DisplayMode> modeModel)
	{
		super(sortParam, critery, modeModel);
	}

	@Override
	public void populateItem(Item<ICellPopulator<OProperty>> cellItem,
			String componentId, IModel<OProperty> rowModel) {
		if(DisplayMode.EDIT.equals(getModeObject()))
		{
			super.populateItem(cellItem, componentId, rowModel);
		}
		else
		{
			cellItem.add(new OPropertyViewPanel(componentId, rowModel));
		}
	}
	
}
