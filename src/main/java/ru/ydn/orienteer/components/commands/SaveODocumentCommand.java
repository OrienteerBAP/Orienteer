package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

public class SaveODocumentCommand extends AbstractSaveCommand<ODocument> implements ISecuredComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<? extends OIdentifiable> documentModel;

	public SaveODocumentCommand(
			OrienteerStructureTable<ODocument, ?> structureTable,
			IModel<DisplayMode> displayModeModel) {
		super(structureTable, displayModeModel);
		this.documentModel = structureTable.getModel();
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
	}



	@Override
	public void onClick(AjaxRequestTarget target) {
		documentModel.getObject().getRecord().save();
		super.onClick(target);
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		ODocument doc = documentModel.getObject().getRecord();
		ORID orid = documentModel.getObject().getIdentity();
		OrientPermission permission = orid.isNew()?OrientPermission.CREATE:OrientPermission.UPDATE;
		return OSecurityHelper.requireOClass(doc.getSchemaClass(), permission);
	}
	
}
