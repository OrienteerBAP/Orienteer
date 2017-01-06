package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
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
	
	private boolean forceCommit = false;

	public SaveODocumentCommand(
			OrienteerStructureTable<ODocument, ?> structureTable,
			IModel<DisplayMode> displayModeModel) {
		this(structureTable, displayModeModel, structureTable.getModel());
	}
	
	public SaveODocumentCommand(
			ICommandsSupportComponent<ODocument> component,
			IModel<DisplayMode> displayModeModel, IModel<ODocument> model) {
		super(component, displayModeModel, model);
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		ODocument doc = getModelObject();
		if(doc.getIdentity().isNew()) realizeMandatory(doc);
		doc.save();
		if(forceCommit) {
			ODatabaseDocument db = getDatabase();
			boolean active = db.getTransaction().isActive();
			db.commit();
			if(active) db.begin();
		}
        super.onClick(target);
	}
	
	public static void realizeMandatory(ODocument doc) {
		OClass oClass = doc.getSchemaClass();
		if(oClass!=null) {
			for(OProperty property : oClass.properties()) {
				if(property.isMandatory() 
						&& Strings.isEmpty(property.getDefaultValue()) 
						&& !doc.containsField(property.getName())) {
					doc.field(property.getName(), (Object) null);
				}
			}
		}
	}
	
	public boolean isForceCommit() {
		return forceCommit;
	}

	public SaveODocumentCommand setForceCommit(boolean forceCommit) {
		this.forceCommit = forceCommit;
		return this;
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		return getRequiredResources(getModelObject());
	}
	
	public static RequiredOrientResource[] getRequiredResources(ODocument doc) {
		ORID orid = doc.getIdentity();
		OrientPermission permission = orid.isNew()?OrientPermission.CREATE:OrientPermission.UPDATE;
		return OSecurityHelper.requireOClass(doc.getSchemaClass(), permission);
	}
	
}
