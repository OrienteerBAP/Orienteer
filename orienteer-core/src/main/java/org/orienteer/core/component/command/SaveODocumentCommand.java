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

import org.orienteer.core.web.ODocumentPage;
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

	public SaveODocumentCommand(
			OrienteerStructureTable<ODocument, ?> structureTable,
			IModel<DisplayMode> displayModeModel) {
		super(structureTable, displayModeModel, structureTable.getModel());
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		getModelObject().getRecord().save();
		super.onClick(target);

        if(getModelObject().getClassName().equals("OFunction")) {
            setResponsePage(new ODocumentPage(getModelObject()).setModeObject(DisplayMode.VIEW));
        }
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		ODocument doc = getModelObject();
		ORID orid = doc.getIdentity();
		OrientPermission permission = orid.isNew()?OrientPermission.CREATE:OrientPermission.UPDATE;
		return OSecurityHelper.requireOClass(doc.getSchemaClass(), permission);
	}
	
}
