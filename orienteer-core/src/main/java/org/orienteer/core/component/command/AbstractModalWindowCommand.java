package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;

/**
 * {@link AjaxCommand} that will display a modal window for additional steps
 *
 * @param <T>  the type of an entity to which this command can be applied
 */
public abstract class AbstractModalWindowCommand<T> extends AjaxCommand<T>
{
	protected ModalWindow modal;

	public AbstractModalWindowCommand(IModel<?> labelModel,
			ICommandsSupportComponent<T> component)
	{
		super(labelModel, component);
	}

	public AbstractModalWindowCommand(String commandId, IModel<?> labelModel)
	{
		super(commandId, labelModel);
	}

	public AbstractModalWindowCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
	}
	
	public AbstractModalWindowCommand(IModel<?> labelModel,
			ICommandsSupportComponent<T> component, IModel<T> model) {
		super(labelModel, component, model);
	}

	public AbstractModalWindowCommand(String commandId, IModel<?> labelModel,
			IModel<T> model) {
		super(commandId, labelModel, model);
	}

	public AbstractModalWindowCommand(String commandId, String labelKey,
			IModel<T> model) {
		super(commandId, labelKey, model);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		modal = new ModalWindow("modal");
		modal.setAutoSize(true);
		add(modal);
		initializeContent(modal);
		setAutoNotify(false);
	}
	
	protected abstract void initializeContent(ModalWindow modal);
	
	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		if(targetOptional.isPresent())
			modal.show(targetOptional.get());
	}
	
	public void onAfterModalSubmit(){
		//can be called on submit modal window if necessary
	};
	
}
