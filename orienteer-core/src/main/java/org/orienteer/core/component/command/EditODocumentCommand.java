package org.orienteer.core.component.command;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;
import org.orienteer.core.component.table.DataTableCommandsToolbar;

import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class EditODocumentCommand extends EditCommand<ODocument> implements ISecuredComponent{

	private IModel<ODocument> documentmodel;
	public EditODocumentCommand(
			OrienteerStructureTable<ODocument, ?> structureTable,
			IModel<DisplayMode> displayModeModel) {
		super(structureTable, displayModeModel);
		documentmodel = structureTable.getModel();
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		return OSecurityHelper.requireOClass(documentmodel.getObject().getSchemaClass(), OrientPermission.UPDATE);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisibilityAllowed(OSecurityHelper.isAllowed(documentmodel.getObject(), OrientPermission.UPDATE));
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(documentmodel!=null) documentmodel.detach();
	}
	
	

	
}
