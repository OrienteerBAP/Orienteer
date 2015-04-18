package org.orienteer.core.component.command;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

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