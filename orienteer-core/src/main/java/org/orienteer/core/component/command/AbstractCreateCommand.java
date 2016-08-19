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

	public AbstractCreateCommand(OrienteerDataTable<T, ?> table)
	{
		super(new ResourceModel("command.create"), table);
	}

	public AbstractCreateCommand(OrienteerStructureTable<T, ?> table)
	{
		super(new ResourceModel("command.create"), table);
	}

	public AbstractCreateCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
	}

	@Override
	protected void onInstantiation() {
		super.onInstantiation();
		setIcon(FAIconType.plus);
		setBootstrapType(BootstrapType.PRIMARY);
		setAutoNotify(false);
	}

}
