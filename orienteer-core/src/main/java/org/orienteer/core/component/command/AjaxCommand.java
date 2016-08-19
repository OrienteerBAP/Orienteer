package org.orienteer.core.component.command;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

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
			public void onClick(AjaxRequestTarget target) {
				AjaxCommand.this.onClick(target);
				trySendActionPerformed();
			}
        };
	}
	
	public abstract void onClick(AjaxRequestTarget target);

	@Override
	public final void onClick() {
		throw new WicketRuntimeException("onClick doesn't supported by "+AjaxCommand.class.getSimpleName());
	}

}
