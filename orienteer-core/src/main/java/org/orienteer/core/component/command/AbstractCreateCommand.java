package org.orienteer.core.component.command;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * Abstract {@link Command} for any commands which create something
 *
 * @param <T> the type of an entity to which this command can be applied
 */
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
