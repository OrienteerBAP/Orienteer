package ru.ydn.orienteer.components.table;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.orienteer.components.commands.Command;

public class OrienteerDataTable<T, S> extends DataTable<T, S>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataTableCommandsToolbar<T> commandsToolbar;
	
	private IModel<String> captionModel;
	
	public OrienteerDataTable(String id, List<? extends IColumn<T, S>> columns,
			ISortableDataProvider<T, S> dataProvider, int rowsPerPage)
	{
		super(id, columns, dataProvider, rowsPerPage);
		addTopToolbar(commandsToolbar=new DataTableCommandsToolbar<T>(this));
		addTopToolbar(new HeadersToolbar<S>(this, dataProvider));
		addBottomToolbar(new NavigationToolbar(this));
		addBottomToolbar(new NoRecordsToolbar(this));
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

	@Override
	public IModel<String> getCaptionModel() {
		if(captionModel==null)
		{
			captionModel = Model.of("");
		}
		return captionModel;
	}
	
	public OrienteerDataTable<T, S> setCaptionModel(IModel<String> captionModel) {
		get("caption").setDefaultModel(captionModel);
		this.captionModel = captionModel;
		return this;
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(captionModel!=null) captionModel.detach();
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "table", " ");
	}
	
}
