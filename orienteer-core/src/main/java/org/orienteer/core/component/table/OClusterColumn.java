package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.storage.OCluster;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClusterViewPanel;

/**
 * {@link OClusterMetaColumn} to refer to {@link OCluster} themselves
 */
public class OClusterColumn extends OClusterMetaColumn
{
	private static final long serialVersionUID = 1L;

	public OClusterColumn(String critery, IModel<DisplayMode> modeModel)
	{
		super(critery, modeModel);
	}

	public OClusterColumn(String sortParam, String critery,
                          IModel<DisplayMode> modeModel)
	{
		super(sortParam, critery, modeModel);
	}
	
	@Override
	public void populateItem(Item<ICellPopulator<OCluster>> cellItem,
			String componentId, IModel<OCluster> rowModel) {
		if(DisplayMode.EDIT.equals(getModeObject()))
		{
			super.populateItem(cellItem, componentId, rowModel);
		}
		else
		{
			cellItem.add(new OClusterViewPanel(componentId, rowModel));
		}
	}
}
