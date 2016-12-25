package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * Ajax-enabled {@link Command} which additionally submits a form
 *
 * @param <T>  the type of an entity to which this command can be applied
 */
public class AjaxFormCommand<T> extends AjaxCommand<T>
{
	private static final long serialVersionUID = 1L;
	private Boolean defaultFormProcessing;

	public AjaxFormCommand(IModel<?> labelModel,
			ICommandsSupportComponent<T> component, IModel<T> model) {
		super(labelModel, component, model);
	}

	public AjaxFormCommand(IModel<?> labelModel,
			ICommandsSupportComponent<T> component) {
		super(labelModel, component);
	}

	public AjaxFormCommand(String commandId, IModel<?> labelModel,
			IModel<T> model) {
		super(commandId, labelModel, model);
	}

	public AjaxFormCommand(String commandId, IModel<?> labelModel) {
		super(commandId, labelModel);
	}

	public AjaxFormCommand(String commandId, String labelKey, IModel<T> model) {
		super(commandId, labelKey, model);
	}

	public AjaxFormCommand(String commandId, String labelKey) {
		super(commandId, labelKey);
	}

	@Override
	protected AbstractLink newLink(String id) {
		AjaxSubmitLink link =  new AjaxSubmitLink(id)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				AjaxFormCommand.this.onSubmit(target, form);
				trySendActionPerformed();
			}
			
		};
		if(defaultFormProcessing!=null) link.setDefaultFormProcessing(defaultFormProcessing);
		return link;
		/*return new AjaxFallbackLink<Object>(id)
		        {
					@Override
					public void onClick(AjaxRequestTarget target) {
						AjaxCommand.this.onClick(target);
					}
		        };*/
	}
	
	public AjaxFormCommand<T> setDefaultFormProcessing(boolean defaultFormProcessing) {
		if(getLink()!=null) {
			((AjaxSubmitLink)getLink()).setDefaultFormProcessing(defaultFormProcessing);
		} 
		this.defaultFormProcessing = defaultFormProcessing;
		return this;
	}
	
	public void onSubmit(AjaxRequestTarget target, Form<?> form) {
		onClick(target);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {

	}

}
