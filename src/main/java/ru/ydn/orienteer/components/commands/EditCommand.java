package ru.ydn.orienteer.components.commands;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;

public class EditCommand<T> extends AjaxCommand<T>
{
	private IModel<DisplayMode> displayModeModel;

	public EditCommand(DataTableCommandsToolbar<T> toolbar, IModel<DisplayMode> displayModeModel)
	{
		super(new ResourceModel("command.edit"), toolbar);
		this.displayModeModel = displayModeModel;
	}
	
	public EditCommand(OrienteerStructureTable<T, ?> structureTable, IModel<DisplayMode> displayModeModel)
	{
		this(structureTable.getCommandsToolbar(), displayModeModel);
	}

	public EditCommand(StructureTableCommandsToolbar<T> toolbar, IModel<DisplayMode> displayModeModel)
	{
		super(new ResourceModel("command.edit"), toolbar);
		this.displayModeModel = displayModeModel;
	}
	
	
	
	@Override
	protected void initialize(String commandId, IModel<?> labelModel) {
		super.initialize(commandId, labelModel);
		setIcon(FAIconType.edit);
		setBootstrapType(BootstrapType.PRIMARY);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(!DisplayMode.EDIT.equals(displayModeModel.getObject()));
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		displayModeModel.setObject(DisplayMode.EDIT);
		target.add(this);
		this.send(this, Broadcast.BUBBLE, target);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if(displayModeModel!=null) displayModeModel.detach();
	}
	
	

}
