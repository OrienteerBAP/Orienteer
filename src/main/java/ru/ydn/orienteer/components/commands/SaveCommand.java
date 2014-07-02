package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;

public class SaveCommand extends AjaxFormCommand<ODocument>
{
	private IModel<DisplayMode> displayModeModel;
	private IModel<? extends OIdentifiable> documentModel;

	public SaveCommand(StructureTableCommandsToolbar toolbar, IModel<DisplayMode> displayModeModel, IModel<? extends OIdentifiable> documentModel)
	{
		super(new ResourceModel("command.save"), toolbar);
		this.displayModeModel = displayModeModel;
		this.documentModel = documentModel;
		setIcon(FAIconType.save);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		documentModel.getObject().getRecord().save();
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
