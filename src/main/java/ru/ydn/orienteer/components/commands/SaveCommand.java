package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;

public class SaveCommand extends SimpleSaveCommand<ODocument>
{
	private IModel<? extends OIdentifiable> documentModel;

	public SaveCommand(StructureTableCommandsToolbar toolbar, IModel<DisplayMode> displayModeModel, IModel<? extends OIdentifiable> documentModel)
	{
		super(toolbar, displayModeModel);
		this.documentModel = documentModel;
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		documentModel.getObject().getRecord().save();
		super.onClick(target);
	}
	
}
