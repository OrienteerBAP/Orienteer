package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

public class SaveSchemaCommand<T> extends SavePrototypeCommand<T> implements ISecuredComponent {

	private IModel<T> objectModel;
	public SaveSchemaCommand(OrienteerStructureTable<T, ?> table,
			IModel<DisplayMode> displayModeModel, IModel<T> model) {
		super(table, displayModeModel, model);
		objectModel = table.getModel();
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		super.onClick(target);
		getDatabase().getMetadata().reload();
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		T object = objectModel.getObject();
		OrientPermission permission = (object instanceof IPrototype<?>)?OrientPermission.CREATE:OrientPermission.UPDATE;
		return OSecurityHelper.requireResource(ORule.ResourceGeneric.SCHEMA, null, permission);
	}

	@Override
	public void detachModels() {
		super.detachModels();
		objectModel.detach();
	}
	
	

}
