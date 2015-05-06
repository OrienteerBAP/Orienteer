package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * {@link AbstractCheckBoxEnabledCommand} that require selection of some set of objects.
 * Modal window  will be displayed for additional steps
 *
 * @param <T>  the type of an entity to which this command can be applied
 */
public abstract class AbstractCheckBoxEnabledModalWindowCommand<T> extends
		AbstractCheckBoxEnabledCommand<T>
{
	protected ModalWindow modal;
	
	public AbstractCheckBoxEnabledModalWindowCommand(IModel<?> labelModel,
			DataTableCommandsToolbar<T> toolbar)
	{
		super(labelModel, toolbar);
	}

	public AbstractCheckBoxEnabledModalWindowCommand(IModel<?> labelModel,
			OrienteerDataTable<T, ?> table)
	{
		super(labelModel, table);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		modal = new ModalWindow("modal");
		modal.setAutoSize(true);
		add(modal);
		initializeContent(modal);
	}
	
	protected abstract void initializeContent(ModalWindow modal);
	
	@Override
	public void onSubmit(AjaxRequestTarget target, Form<?> form) {
		modal.show(target);
	}

}
