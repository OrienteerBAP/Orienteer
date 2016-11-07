package org.orienteer.core.component.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OIndexViewPanel;
import org.orienteer.core.component.property.OPropertyViewPanel;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link OIndexMetaColumn} to refer to {@link OIndex} themselves
 */
public class OIndexDefinitionColumn extends OIndexMetaColumn
{
	private static final long serialVersionUID = 1L;

	public OIndexDefinitionColumn(String critery,
			IModel<DisplayMode> modeModel)
	{
		super(critery, modeModel);
	}

	public OIndexDefinitionColumn(String sortParam, String critery,
			IModel<DisplayMode> modeModel)
	{
		super(sortParam, critery, modeModel);
	}

	@Override
	public void populateItem(Item<ICellPopulator<OIndex<?>>> cellItem,
			String componentId, IModel<OIndex<?>> rowModel) {
		if(DisplayMode.EDIT.equals(getModeObject()))
		{
			super.populateItem(cellItem, componentId, rowModel);
		}
		else
		{
			cellItem.add(new OIndexViewPanel(componentId, rowModel));
		}
	}

}
