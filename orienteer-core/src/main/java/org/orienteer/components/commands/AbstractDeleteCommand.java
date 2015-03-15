package org.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class AbstractDeleteCommand<T> extends AbstractCheckBoxEnabledCommand<T>
{
	private static final long serialVersionUID = 1L;
	
	public AbstractDeleteCommand(OrienteerDataTable<T, ?> table)
	{
		super(new ResourceModel("command.delete"), table);
		
	}

	public AbstractDeleteCommand(DataTableCommandsToolbar<T> toolbar)
	{
		super(new ResourceModel("command.delete"), toolbar);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		setIcon(FAIconType.times_circle);
		setBootstrapType(BootstrapType.DANGER);
	}

}