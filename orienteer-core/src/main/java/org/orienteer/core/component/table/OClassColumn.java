package org.orienteer.core.component.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.component.property.OPropertyViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link OClassMetaColumn} to refer to {@link OClass} themselves
 */
public class OClassColumn extends OClassMetaColumn
{
	private static final long serialVersionUID = 1L;

	public OClassColumn(CustomAttribute custom, IModel<DisplayMode> modeModel)
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
