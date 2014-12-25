package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

public abstract class AbstractModalWindowCommand<T> extends AjaxCommand<T>
{
	protected ModalWindow modal;

	public AbstractModalWindowCommand(IModel<?> labelModel,
			DataTableCommandsToolbar<T> toolbar)
	{
		super(labelModel, toolbar);
	}

	public AbstractModalWindowCommand(IModel<?> labelModel,
			OrienteerDataTable<T, ?> table)
	{
		super(labelModel, table);
	}

	public AbstractModalWindowCommand(IModel<?> labelModel,
			OrienteerStructureTable<T, ?> table)
	{
		super(labelModel, table);
	}

	public AbstractModalWindowCommand(IModel<?> labelModel,
			StructureTableCommandsToolbar<T> toolbar)
	{
		super(labelModel, toolbar);
	}

	public AbstractModalWindowCommand(String commandId, IModel<?> labelModel)
	{
		super(commandId, labelModel);
	}

	public AbstractModalWindowCommand(String commandId, String labelKey)
	{
		super(commandId, labelKey);
	}

	public AbstractModalWindowCommand(String labelKey)
	{
		super(labelKey);
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
	public void onClick(AjaxRequestTarget target) {
		modal.show(target);
	}
	
}
