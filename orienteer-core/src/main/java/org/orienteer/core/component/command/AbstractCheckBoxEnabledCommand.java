package org.orienteer.core.component.command;

import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * {@link AjaxFormCommand} that require checking of a set of entities for execution
 *
 * @param <T>  the type of an entity to which this command can be applied
 */
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
	public void onSubmit(AjaxRequestTarget target) {
		performMultiAction(target, getSelected());
		resetSelection();
	}
	
	public DataTable<T, ?> getTable() {
		return table;
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
