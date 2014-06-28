package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;

public class AjaxFormCommand extends AjaxCommand
{

	public AjaxFormCommand(IModel<?> labelModel,
			DataTableCommandsToolbar toolbar)
	{
		super(labelModel, toolbar);
	}

	public AjaxFormCommand(IModel<?> labelModel,
			StructureTableCommandsToolbar toolbar)
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
