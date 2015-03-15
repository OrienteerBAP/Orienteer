package org.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

public class AjaxFormCommand<T> extends AjaxCommand<T>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AjaxFormCommand(IModel<?> labelModel, OrienteerDataTable<T, ?> table)
	{
		super(labelModel, table);
	}

	public AjaxFormCommand(IModel<?> labelModel,
			OrienteerStructureTable<T, ?> table)
	{
		super(labelModel, table);
	}

	public AjaxFormCommand(IModel<?> labelModel,
			DataTableCommandsToolbar<T> toolbar)
	{
		super(labelModel, toolbar);
	}

	public AjaxFormCommand(IModel<?> labelModel,
			StructureTableCommandsToolbar<T> toolbar)
	{
		super(labelModel, toolbar);
	}

	public AjaxFormCommand(String commandId, IModel<?> labelModel)
	{
		super(commandId, labelModel);
	}

	public AjaxFormCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
	}

	public AjaxFormCommand(String labelKey)
	{
		super(labelKey);
	}

	@Override
	protected AbstractLink newLink(String id) {
		return new AjaxSubmitLink(id)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				AjaxFormCommand.this.onSubmit(target, form);
			}
			
		};
		/*return new AjaxFallbackLink<Object>(id)
		        {
					@Override
					public void onClick(AjaxRequestTarget target) {
						AjaxCommand.this.onClick(target);
					}
		        };*/
	}
	
	public void onSubmit(AjaxRequestTarget target, Form<?> form) {
		onClick(target);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {

	}

}
