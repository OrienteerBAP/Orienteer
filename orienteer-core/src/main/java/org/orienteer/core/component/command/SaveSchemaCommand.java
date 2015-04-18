package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.OrienteerDataTable;

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
	
	
	
	public SaveSchemaCommand(OrienteerDataTable<T, ?> table,
			IModel<DisplayMode> displayModeModel)
	{
		super(table, displayModeModel);
	}

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
		T object = objectModel!=null?objectModel.getObject():null;
		if(object!=null)
		{
			OrientPermission permission = (object instanceof IPrototype<?>)?OrientPermission.CREATE:OrientPermission.UPDATE;
			return OSecurityHelper.requireResource(ORule.ResourceGeneric.SCHEMA, null, permission);
		}
		else
		{
			return new RequiredOrientResource[0];
		}
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(objectModel!=null) objectModel.detach();
	}
	
	

}
