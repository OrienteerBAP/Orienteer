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

public class SimpleSaveCommand<T> extends AjaxFormCommand<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<DisplayMode> displayModeModel;
	
	public SimpleSaveCommand(StructureTableCommandsToolbar<T> toolbar, IModel<DisplayMode> displayModeModel)
	{
		super(new ResourceModel("command.save"), toolbar);
		this.displayModeModel = displayModeModel;
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
	}
	
	public SimpleSaveCommand(OrienteerStructureTable<T, ?> structureTable, IModel<DisplayMode> displayModeModel)
	{
		this(structureTable.getCommandsToolbar(), displayModeModel);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		displayModeModel.setObject(DisplayMode.VIEW);
		target.add(this);
		this.send(this, Broadcast.BUBBLE, target);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(DisplayMode.EDIT.equals(displayModeModel.getObject()));
	}
}
