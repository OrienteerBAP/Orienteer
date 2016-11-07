package org.orienteer.core.component.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OPropertyViewPanel;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link OPropertyMetaColumn} to refer to {@link OProperty} themselves
 */
public class OPropertyDefinitionColumn extends OPropertyMetaColumn
{
	private static final long serialVersionUID = 1L;

	public OPropertyDefinitionColumn(CustomAttribute custom,
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
