package ru.ydn.orienteer.components.commands;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;

public abstract class AjaxCommand extends Command
{
	
	
	public AjaxCommand(IModel<?> labelModel, DataTableCommandsToolbar toolbar)
	{
		super(labelModel, toolbar);
	}

	public AjaxCommand(IModel<?> labelModel,
			StructureTableCommandsToolbar toolbar)
	{
		super(labelModel, toolbar);
	}

	public AjaxCommand(String commandId, IModel<?> labelModel)
	{
		super(commandId, labelModel);
	}

	public AjaxCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
	}

	public AjaxCommand(String labelKey)
	{
		super(labelKey);
	}
	
	@Override
	protected void initialize(String commandId, IModel<?> labelModel) {
		super.initialize(commandId, labelModel);
		setOutputMarkupPlaceholderTag(true);
	}

	@Override
	protected AbstractLink newLink(String id) {
		return new AjaxFallbackLink<Object>(id)
        {
			@Override
			public void onClick(AjaxRequestTarget target) {
				AjaxCommand.this.onClick(target);
			}
        };
	}
	
	public abstract void onClick(AjaxRequestTarget target);

	@Override
	public final void onClick() {
		throw new WicketRuntimeException("onClick doesn't supported by "+AjaxCommand.class.getSimpleName());
	}

}
