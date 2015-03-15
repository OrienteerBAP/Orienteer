package org.orienteer.components.commands;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

public class EditCommand<T> extends AjaxCommand<T>
{
	private static final long serialVersionUID = 1L;
	private IModel<DisplayMode> displayModeModel;
	
	public EditCommand(OrienteerDataTable<T, ?> table, IModel<DisplayMode> displayModeModel)
	{
		super(new ResourceModel("command.edit"), table);
		this.displayModeModel = displayModeModel;
	}

	public EditCommand(DataTableCommandsToolbar<T> toolbar, IModel<DisplayMode> displayModeModel)
	{
		super(new ResourceModel("command.edit"), toolbar);
		this.displayModeModel = displayModeModel;
	}
	
	public EditCommand(OrienteerStructureTable<T, ?> structureTable, IModel<DisplayMode> displayModeModel)
	{
		super(new ResourceModel("command.edit"), structureTable);
		this.displayModeModel = displayModeModel;
	}

	public EditCommand(StructureTableCommandsToolbar<T> toolbar, IModel<DisplayMode> displayModeModel)
	{
		super(new ResourceModel("command.edit"), toolbar);
		this.displayModeModel = displayModeModel;
	}
	
	
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
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
