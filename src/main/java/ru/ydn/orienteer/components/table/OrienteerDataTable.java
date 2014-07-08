package ru.ydn.orienteer.components.table;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;

import ru.ydn.orienteer.components.commands.Command;

public class OrienteerDataTable<T, S> extends DefaultDataTable<T, S>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataTableCommandsToolbar<T> commandsToolbar;
	
	public OrienteerDataTable(String id, List<? extends IColumn<T, S>> columns,
			ISortableDataProvider<T, S> dataProvider, int rowsPerPage)
	{
		super(id, columns, dataProvider, rowsPerPage);
		addTopToolbar(commandsToolbar=new DataTableCommandsToolbar<T>(this));
		setOutputMarkupPlaceholderTag(true);
	}

	public DataTableCommandsToolbar<T> getCommandsToolbar() {
		return commandsToolbar;
	}
	
	public OrienteerDataTable<T, S> addCommand(Command<T> command)
	{
		commandsToolbar.add(command);
		return this;
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		if(event.getPayload() instanceof AjaxRequestTarget && Broadcast.BUBBLE.equals(event.getType()))
		{
			((AjaxRequestTarget)event.getPayload()).add(this);
		}
	}
}
