package org.orienteer.core.component.command;

import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OrienteerDataTable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
	
	@Getter
	@Setter
	@Accessors(chain = true)
	private boolean requireAtLeastOne = false;
	@Getter
	@Setter
	@Accessors(chain = true)
	private boolean requireExactOne = false;
	
	public AbstractCheckBoxEnabledCommand(IModel<?> labelModel, OrienteerDataTable<T, ?> table)
	{
		super(labelModel, table);
		this.table=table;
	}
	
	public AbstractCheckBoxEnabledCommand(IModel<?> labelModel, ICommandsSupportComponent<T> component,
			IModel<T> model) {
		super(labelModel, component, model);
	}

	public AbstractCheckBoxEnabledCommand(IModel<?> labelModel, ICommandsSupportComponent<T> component) {
		super(labelModel, component);
	}

	public AbstractCheckBoxEnabledCommand(String commandId, IModel<?> labelModel, IModel<T> model) {
		super(commandId, labelModel, model);
	}

	public AbstractCheckBoxEnabledCommand(String commandId, IModel<?> labelModel) {
		super(commandId, labelModel);
	}

	public AbstractCheckBoxEnabledCommand(String commandId, String labelKey, IModel<T> model) {
		super(commandId, labelKey, model);
	}

	public AbstractCheckBoxEnabledCommand(String commandId, String labelKey) {
		super(commandId, labelKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(checkboxColumn==null)
		{
			for (IColumn<T, ?> column : getTable().getColumns())
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
		if(table==null) {
			table = findParent(DataTable.class);
		}
		return table;
	}
	
	public AbstractCheckBoxEnabledCommand<T> setTable(DataTable<T, ?> table) {
		this.table = table;
		return this;
	}

	public List<T> getSelected()
	{
		return checkboxColumn.getSelected();
	}
	
	public void resetSelection()
	{
		checkboxColumn.resetSelection();
	}
	
	protected boolean validateSelected(AjaxRequestTarget target, List<T> objects) {
		String errorMessage=null;
		if(requireExactOne && objects!=null && objects.size() > 1)
		{
			errorMessage = getLocalizer().getString("alert.onlyoneshouldbeselected", this);
		}
		if(requireAtLeastOne && (objects == null || objects.isEmpty()))
		{
			errorMessage = getLocalizer().getString("alert.atleastoneshouldbeselected", this);
		}
		if(errorMessage!=null) {
			target.appendJavaScript("alert(\""+errorMessage.replace("\"", "\\\"")+"\")");
			return false;
		}
		return true;
	}
	
	protected void performMultiAction(AjaxRequestTarget target, List<T> objects)
	{
		validateSelected(target, objects);
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
