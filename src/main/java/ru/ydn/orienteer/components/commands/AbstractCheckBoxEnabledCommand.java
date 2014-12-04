package ru.ydn.orienteer.components.commands;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.CheckBoxColumn;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class AbstractCheckBoxEnabledCommand<T> extends AjaxFormCommand<T>
{
	private static final long serialVersionUID = 1L;
	private DataTable<T, ?> table;
	private CheckBoxColumn<T, ?, ?> checkboxColumn;
	
	public AbstractCheckBoxEnabledCommand(IModel<?> labelModel, OrienteerDataTable<T, ?> table)
	{
		super(labelModel, table);
		this.table=table;
		
	}

	public AbstractCheckBoxEnabledCommand(IModel<?> labelModel, DataTableCommandsToolbar<T> toolbar)
	{
		super(labelModel, toolbar);
		table = toolbar.getTable();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(checkboxColumn==null)
		{
			for (IColumn<T, ?> column : table.getColumns())
			{
				if(column instanceof CheckBoxColumn)
				{
					checkboxColumn=(CheckBoxColumn<T, ?, ?>) column;
					break;
				}
			}
		}
		setVisible(checkboxColumn!=null);
	}

	@Override
	public void onSubmit(AjaxRequestTarget target, Form<?> form) {
		performMultiAction(target, getSelected());
		resetSelection();
		this.send(this, Broadcast.BUBBLE, target);
	}
	
	public List<T> getSelected()
	{
		return checkboxColumn.getSelected();
	}
	
	public void resetSelection()
	{
		checkboxColumn.resetSelection();
	}
	
	protected void performMultiAction(AjaxRequestTarget target, List<T> objects)
	{
		for (T object : objects)
		{
			perfromSingleAction(target, object);
		}
	}
	
	protected void perfromSingleAction(AjaxRequestTarget target, T object)
	{
		//NOP
	}
}
