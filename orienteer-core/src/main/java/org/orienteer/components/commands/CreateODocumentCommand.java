package org.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.CustomAttributes;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.web.DocumentPage;

import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class CreateODocumentCommand extends AbstractCreateCommand<ODocument> implements ISecuredComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
	private IModel<ODocument> documentModel;
	private IModel<OProperty> propertyModel;
	
	public CreateODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<OClass> classModel) {
		super(table);
		this.classModel = classModel;
	}
	
	public CreateODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		super(table);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
		this.classModel = new PropertyModel<OClass>(propertyModel, "linkedClass");
	}

	public CreateODocumentCommand(DataTableCommandsToolbar<ODocument> toolbar, IModel<OClass> classModel) {
		super(toolbar);
		this.classModel = classModel;
	}
	
	public CreateODocumentCommand(DataTableCommandsToolbar<ODocument> toolbar, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		super(toolbar);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
		this.classModel = new PropertyModel<OClass>(propertyModel, "linkedClass");
	}
	
	@Override
	public void onClick() {
		ODocument doc = new ODocument(classModel.getObject());
		if(propertyModel!=null && documentModel!=null)
		{
			OProperty property = propertyModel.getObject();
			if(property!=null)
			{
				OProperty inverseProperty = CustomAttributes.PROP_INVERSE.getValue(property);
				if(inverseProperty!=null)
				{
					doc.field(inverseProperty.getName(), documentModel.getObject());
				}
			}
		}
		setResponsePage(new DocumentPage(doc).setDisplayMode(DisplayMode.EDIT));
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
