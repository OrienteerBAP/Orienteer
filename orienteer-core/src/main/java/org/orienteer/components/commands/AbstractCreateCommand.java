package org.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;


public abstract class AbstractCreateCommand<T> extends Command<T>
{

	public AbstractCreateCommand(DataTableCommandsToolbar<T> toolbar)
	{
		super(new ResourceModel("command.create"), toolbar);
	}

	public AbstractCreateCommand(OrienteerDataTable<T, ?> table)
	{
		super(new ResourceModel("command.create"), table);
	}

	public AbstractCreateCommand(OrienteerStructureTable<T, ?> table)
	{
		super(new ResourceModel("command.create"), table);
	}

	public AbstractCreateCommand(StructureTableCommandsToolbar<T> toolbar)
	{
		super(new ResourceModel("command.create"), toolbar);
	}


	public AbstractCreateCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
	}

	public AbstractCreateCommand(String labelKey)
	{
		super(labelKey);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		setIcon(FAIconType.plus);
		setBootstrapType(BootstrapType.PRIMARY);
	}

}
