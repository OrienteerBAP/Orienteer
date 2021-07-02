package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;
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
			OrienteerDataTable<T, ?> table)
	{
		super(labelModel, table);
	}
	
	public AbstractCheckBoxEnabledModalWindowCommand(IModel<?> labelModel, ICommandsSupportComponent<T> component,
			IModel<T> model) {
		super(labelModel, component, model);
	}



	public AbstractCheckBoxEnabledModalWindowCommand(IModel<?> labelModel, ICommandsSupportComponent<T> component) {
		super(labelModel, component);
	}

	public AbstractCheckBoxEnabledModalWindowCommand(String commandId, IModel<?> labelModel, IModel<T> model) {
		super(commandId, labelModel, model);
	}

	public AbstractCheckBoxEnabledModalWindowCommand(String commandId, IModel<?> labelModel) {
		super(commandId, labelModel);
	}

	public AbstractCheckBoxEnabledModalWindowCommand(String commandId, String labelKey, IModel<T> model) {
		super(commandId, labelKey, model);
	}

	public AbstractCheckBoxEnabledModalWindowCommand(String commandId, String labelKey) {
		super(commandId, labelKey);
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
	public void onSubmit(AjaxRequestTarget target) {
		if(validateSelected(target, getSelected())) modal.show(target);
	}

}
