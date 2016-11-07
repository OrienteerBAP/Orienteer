package org.orienteer.core.component.command;

import java.util.Collection;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.modal.SelectSubOClassDialogPage;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.ODocumentPage;

import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Command} to create {@link ODocument}
 */
public class CreateODocumentCommand extends AbstractModalWindowCommand<ODocument> implements ISecuredComponent{
	
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
	private IModel<ODocument> documentModel;
	private IModel<OProperty> propertyModel;
	
	public CreateODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<OClass> classModel) {
		super(new ResourceModel("command.create"), table);
		this.classModel = classModel;
	}
	
	public CreateODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		super(new ResourceModel("command.create"), table);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
		this.classModel = new PropertyModel<OClass>(propertyModel, "linkedClass");
	}
	
	@Override
	protected void onInstantiation() {
		super.onInstantiation();
		setIcon(FAIconType.plus);
		setBootstrapType(BootstrapType.PRIMARY);
		setAutoNotify(false);
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		OClass oClass = classModel.getObject();
		Collection<OClass> subClasses = oClass.getSubclasses();
		if(subClasses==null || subClasses.isEmpty()) {
			//There is no subclasses, so no need to select particular subtype
			redirectToCreateODocumentPage(null, oClass);
		} else {
			modal.show(target);
		}
	}
	
	@Override
	protected void initializeContent(ModalWindow modal) {
		modal.setTitle(new ResourceModel("dialog.select.class"));
		modal.setAutoSize(true);
		modal.setMinimalWidth(300);
		modal.setContent(new SelectSubOClassDialogPage(modal, classModel) {
			
			@Override
			protected void onSelect(AjaxRequestTarget target, OClass selectedOClass) {
				redirectToCreateODocumentPage(target, selectedOClass);
			}
		});
	}
	
	protected void redirectToCreateODocumentPage(AjaxRequestTarget target, OClass oClass) {
		ODocument doc = new ODocument(oClass);
		if(propertyModel!=null && documentModel!=null)
		{
			OProperty property = propertyModel.getObject();
			if(property!=null)
			{
				OProperty inverseProperty = CustomAttribute.PROP_INVERSE.getValue(property);
				if(inverseProperty!=null)
				{
					doc.field(inverseProperty.getName(), documentModel.getObject());
				}
			}
		}
//		target.add(new ODocumentPage(doc).setModeObject(DisplayMode.EDIT));
		setResponsePage(new ODocumentPage(doc).setModeObject(DisplayMode.EDIT));
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(classModel!=null) classModel.detach();
		if(propertyModel!=null) propertyModel.detach();
		if(documentModel!=null) documentModel.detach();
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		return OSecurityHelper.requireOClass(classModel.getObject(), OrientPermission.CREATE);
	}

}
