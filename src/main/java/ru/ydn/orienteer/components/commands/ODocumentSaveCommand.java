package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;

public class ODocumentSaveCommand extends SimpleSaveCommand<ODocument>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<? extends OIdentifiable> documentModel;

	public ODocumentSaveCommand(StructureTableCommandsToolbar<ODocument> toolbar, IModel<DisplayMode> displayModeModel, IModel<? extends OIdentifiable> documentModel)
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
