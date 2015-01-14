package ru.ydn.orienteer.components.commands;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;

import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.DELETE)
public class DeleteOPropertyCommand extends AbstractDeleteCommand<OProperty> {

	public DeleteOPropertyCommand(DataTableCommandsToolbar<OProperty> toolbar) {
		super(toolbar);
	}
	
	public DeleteOPropertyCommand(OrienteerDataTable<OProperty, ?> table) {
		super(table);
	}
	
	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<OProperty> objects) {
		getDatabase().commit();
		super.performMultiAction(target, objects);
		getDatabase().begin();
	}

	@Override
	protected void perfromSingleAction(AjaxRequestTarget target, OProperty object) {
		object.getOwnerClass().dropProperty(object.getName());
	}

}
