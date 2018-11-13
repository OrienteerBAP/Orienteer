package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;

/**
 * Ajax-enabled {@link Command}
 *
 * @param <T> the type of an entity to which this command can be applied
 */
public abstract class AjaxCommand<T> extends Command<T>
{
	private static final long serialVersionUID = 1L;

	

	public AjaxCommand(IModel<?> labelModel,
			ICommandsSupportComponent<T> component, IModel<T> model) {
		super(labelModel, component, model);
	}

	public AjaxCommand(IModel<?> labelModel,
			ICommandsSupportComponent<T> component) {
		super(labelModel, component);
	}

	public AjaxCommand(String commandId, IModel<?> labelModel, IModel<T> model) {
		super(commandId, labelModel, model);
	}

	public AjaxCommand(String commandId, IModel<?> labelModel) {
		super(commandId, labelModel);
	}

	public AjaxCommand(String commandId, String labelKey, IModel<T> model) {
		super(commandId, labelKey, model);
	}

	public AjaxCommand(String commandId, String labelKey) {
		super(commandId, labelKey);
	}

	@Override
	protected void onInstantiation() {
		super.onInstantiation();
		setOutputMarkupPlaceholderTag(true);
	}

	@Override
	protected AbstractLink newLink(String id) {
		return new AjaxFallbackLink<Object>(id)
        {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Optional<AjaxRequestTarget> targetOptional) {
				AjaxCommand.this.onClick(targetOptional);
				trySendActionPerformed();
			}
        };
	}
	
	public abstract void onClick(Optional<AjaxRequestTarget> targetOptional);

	@Override
	public final void onClick() {
		throw new WicketRuntimeException("onClick doesn't supported by "+AjaxCommand.class.getSimpleName());
	}

}
