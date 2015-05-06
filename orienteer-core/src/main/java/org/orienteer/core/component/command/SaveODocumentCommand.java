package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * {@link Command} for {@link OrienteerStructureTable} to save {@link ODocument}
 */
public class SaveODocumentCommand extends AbstractSaveCommand<ODocument> implements ISecuredComponent
{
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
