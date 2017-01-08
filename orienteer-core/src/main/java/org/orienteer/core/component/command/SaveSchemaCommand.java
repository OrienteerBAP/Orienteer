package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * {@link Command} to save a schema specific entities: {@link OClass}, {@link OProperty}, {@link OIndex} 
 *
 * @param <T> the type of an entity to which this command can be applied
 */
public class SaveSchemaCommand<T> extends SavePrototypeCommand<T> implements ISecuredComponent {

	public SaveSchemaCommand(ICommandsSupportComponent<T> component,
			IModel<DisplayMode> displayModeModel, IModel<T> model) {
		super(component, displayModeModel, model);
	}

	public SaveSchemaCommand(ICommandsSupportComponent<T> component,
			IModel<DisplayMode> displayModeModel) {
		super(component, displayModeModel);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		boolean isActiveTransaction = getDatabase().getTransaction().isActive();
		if(isActiveTransaction) getDatabase().commit(); // Schema changes should be done outside of transaction
		try {
			super.onClick(target);
			getDatabase().getMetadata().reload();
		} finally {
			if(isActiveTransaction) getDatabase().begin();
		}
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		T object = getModelObject();
		if(object!=null)
		{
			OrientPermission permission = (object instanceof IPrototype<?>)?OrientPermission.CREATE:OrientPermission.UPDATE;
			return new RequiredOrientResource[] {
					OSecurityHelper.requireResource(ORule.ResourceGeneric.SCHEMA, null, permission)[0],
					OSecurityHelper.requireResource(ORule.ResourceGeneric.CLUSTER, "internal", permission)[0]
				};
		}
		else
		{
			return new RequiredOrientResource[0];
		}
	}
}
