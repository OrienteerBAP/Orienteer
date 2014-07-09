package ru.ydn.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

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
	protected void initialize(String commandId, IModel<?> labelModel) {
		super.initialize(commandId, labelModel);
		setIcon(FAIconType.times_circle);
		setBootstrapType(BootstrapType.DANGER);
	}

}