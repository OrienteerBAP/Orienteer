package ru.ydn.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;


public abstract class SimpleCreateCommand<T> extends Command<T>
{

	public SimpleCreateCommand(DataTableCommandsToolbar<T> toolbar)
	{
		super(new ResourceModel("command.create"), toolbar);
	}

	public SimpleCreateCommand(OrienteerDataTable<T, ?> table)
	{
		super(new ResourceModel("command.create"), table);
	}

	public SimpleCreateCommand(OrienteerStructureTable<T, ?> table)
	{
		super(new ResourceModel("command.create"), table);
	}

	public SimpleCreateCommand(StructureTableCommandsToolbar<T> toolbar)
	{
		super(new ResourceModel("command.create"), toolbar);
	}


	public SimpleCreateCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
	}

	public SimpleCreateCommand(String labelKey)
	{
		super(labelKey);
	}

	@Override
	protected void initialize(String commandId, IModel<?> labelModel) {
		super.initialize(commandId, labelModel);
		setIcon(FAIconType.plus);
		setBootstrapType(BootstrapType.PRIMARY);
	}
	
	

}
