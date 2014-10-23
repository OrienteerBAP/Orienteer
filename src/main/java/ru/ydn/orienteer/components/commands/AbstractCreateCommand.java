package ru.ydn.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;


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
