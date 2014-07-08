package ru.ydn.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.DocumentPage;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class CreateCommand extends Command<ODocument> implements ISecuredComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public IModel<OClass> classModel;
	public IModel<ODocument> documentModel;
	public IModel<OProperty> propertyModel;
	
	public CreateCommand(OrienteerDataTable<ODocument, ?> table, IModel<OClass> classModel) {
		super(new ResourceModel("command.create"), table);
		this.classModel = classModel;
	}
	
	public CreateCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		super(new ResourceModel("command.create"), table);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
		this.classModel = new PropertyModel<OClass>(propertyModel, "linkedClass");
	}

	public CreateCommand(DataTableCommandsToolbar<ODocument> toolbar, IModel<OClass> classModel) {
		super(new ResourceModel("command.create"), toolbar);
		this.classModel = classModel;
	}
	
	public CreateCommand(DataTableCommandsToolbar<ODocument> toolbar, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		super(new ResourceModel("command.create"), toolbar);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
		this.classModel = new PropertyModel<OClass>(propertyModel, "linkedClass");
	}
	
	@Override
	protected void initialize(String commandId, IModel<?> labelModel) {
		super.initialize(commandId, labelModel);
		setIcon(FAIconType.plus);
		setBootstrapType(BootstrapType.PRIMARY);
	}

	@Override
	public void onClick() {
		ODocument doc = new ODocument(classModel.getObject());
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
