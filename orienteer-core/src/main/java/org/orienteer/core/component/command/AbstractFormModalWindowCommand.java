package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;


/**
 * {@link AjaxFormCommand} that will display a modal window for additional steps
 *
 * @param <T>  the type of an entity to which this command can be applied
 */
public abstract class AbstractFormModalWindowCommand<T> extends AjaxFormCommand<T>
{
	protected ModalWindow modal;

	public AbstractFormModalWindowCommand(IModel<?> labelModel,
			ICommandsSupportComponent<T> component)
	{
		super(labelModel, component);
	}

	public AbstractFormModalWindowCommand(String commandId, IModel<?> labelModel)
	{
		super(commandId, labelModel);
	}

	public AbstractFormModalWindowCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
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
		targetOptional.ifPresent(modal::show);
	}
}
