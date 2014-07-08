package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.CheckBoxColumn;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

public class DeleteCommand extends AjaxFormCommand<ODocument>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataTable<ODocument, ?> table;
	private CheckBoxColumn<ODocument, ?, ?> checkboxColumn;
	
	public DeleteCommand(OrienteerDataTable<ODocument, ?> table)
	{
		super(new ResourceModel("command.delete"), table);
		this.table=table;
		
	}

	public DeleteCommand(DataTableCommandsToolbar<ODocument> toolbar)
	{
		super(new ResourceModel("command.delete"), toolbar);
		table = toolbar.getTable();
	}
	
	@Override
	protected void initialize(String commandId, IModel<?> labelModel) {
		super.initialize(commandId, labelModel);
		setIcon(FAIconType.times_circle);
		setBootstrapType(BootstrapType.DANGER);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(checkboxColumn==null)
		{
			for (IColumn<ODocument, ?> column : table.getColumns())
			{
				if(column instanceof CheckBoxColumn)
				{
					checkboxColumn=(CheckBoxColumn<ODocument, ?, ?>) column;
					break;
				}
			}
		}
		setVisible(checkboxColumn!=null);
	}

	@Override
	public void onSubmit(AjaxRequestTarget target, Form<?> form) {
		for (ODocument docToDelete : checkboxColumn.getSelected())
		{
			docToDelete.delete();
		}
		getDatabase().commit();
		checkboxColumn.resetSelection();
		this.send(this, Broadcast.BUBBLE, target);
	}



}
