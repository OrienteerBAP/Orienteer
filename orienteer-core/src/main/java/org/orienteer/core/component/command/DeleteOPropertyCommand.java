package org.orienteer.core.component.command;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link Command} to delete {@link OProperty}
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.DELETE)
public class DeleteOPropertyCommand extends AbstractDeleteCommand<OProperty> {

	public DeleteOPropertyCommand(OrienteerDataTable<OProperty, ?> table) {
		super(table);
	}
	
	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<OProperty> objects) {
		getDatabaseSession().commit();
		super.performMultiAction(target, objects);
		getDatabaseSession().begin();
	}

	@Override
	protected void perfromSingleAction(AjaxRequestTarget target, OProperty object) {
		object.getOwnerClass().dropProperty(object.getName());
	}

}
