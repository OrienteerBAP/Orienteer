package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OClassViewPanel;
import ru.ydn.orienteer.components.properties.OPropertyViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OClassColumn extends OClassMetaColumn
{
	private static final long serialVersionUID = 1L;

	public OClassColumn(CustomAttributes custom, IModel<DisplayMode> modeModel)
	{
		super(custom, modeModel);
	}

	public OClassColumn(String critery, IModel<DisplayMode> modeModel)
	{
		super(critery, modeModel);
	}

	public OClassColumn(String sortParam, String critery,
			IModel<DisplayMode> modeModel)
	{
		super(sortParam, critery, modeModel);
	}
	
	@Override
	public void populateItem(Item<ICellPopulator<OClass>> cellItem,
			String componentId, IModel<OClass> rowModel) {
		if(DisplayMode.EDIT.equals(getModeObject()))
		{
			super.populateItem(cellItem, componentId, rowModel);
		}
		else
		{
			cellItem.add(new OClassViewPanel(componentId, rowModel));
		}
	}
}
