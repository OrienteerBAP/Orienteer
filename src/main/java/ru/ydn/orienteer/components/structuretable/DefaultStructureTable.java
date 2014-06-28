package ru.ydn.orienteer.components.structuretable;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.commands.Command;

public abstract class DefaultStructureTable<T> extends StructureTable<T>
{
	private StructureTableCommandsToolbar commandsToolbar;

	public DefaultStructureTable(String id, IModel<List<? extends T>> model)
	{
		super(id, model);
		initialize();
	}

	public DefaultStructureTable(String id, List<? extends T> list)
	{
		super(id, list);
		initialize();
	}

	public DefaultStructureTable(String id)
	{
		super(id);
		initialize();
	}
	
	protected void initialize()
	{
		commandsToolbar = new StructureTableCommandsToolbar(this);
		addTopToolbar(commandsToolbar);
	}

	public StructureTableCommandsToolbar getCommandsToolbar() {
		return commandsToolbar;
	}
	
	public DefaultStructureTable<T> addCommand(Command command)
	{
		getCommandsToolbar().add(command);
		return this;
	}
	
	
	

}
