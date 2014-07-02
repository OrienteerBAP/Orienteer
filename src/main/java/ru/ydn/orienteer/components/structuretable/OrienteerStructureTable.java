package ru.ydn.orienteer.components.structuretable;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.commands.Command;

public abstract class OrienteerStructureTable<T> extends StructureTable<T>
{
	private StructureTableCommandsToolbar<T> commandsToolbar;

	public OrienteerStructureTable(String id, IModel<List<? extends T>> model)
	{
		super(id, model);
		initialize();
	}

	public OrienteerStructureTable(String id, List<? extends T> list)
	{
		super(id, list);
		initialize();
	}

	public OrienteerStructureTable(String id)
	{
		super(id);
		initialize();
	}
	
	protected void initialize()
	{
		commandsToolbar = new StructureTableCommandsToolbar<T>(this);
		addTopToolbar(commandsToolbar);
	}

	public StructureTableCommandsToolbar<T> getCommandsToolbar() {
		return commandsToolbar;
	}
	
	public OrienteerStructureTable<T> addCommand(Command<T> command)
	{
		getCommandsToolbar().add(command);
		return this;
	}

}
