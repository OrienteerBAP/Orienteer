package ru.ydn.orienteer.components.commands;

import java.util.List;

import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;

@RequiredOrientResource(value = ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.DELETE)
public class DeleteOClassCommand extends AbstractDeleteCommand<OClass> {

	public DeleteOClassCommand(DataTableCommandsToolbar<OClass> toolbar) {
		super(toolbar);
	}

	public DeleteOClassCommand(OrienteerDataTable<OClass, ?> table) {
		super(table);
	}
	
	@Override
	protected void performMultiAction(List<OClass> objects) {
		getDatabase().commit();
		super.performMultiAction(objects);
		getDatabase().begin();
	}

	@Override
	protected void perfromSingleAction(OClass object) {
		getSchema().dropClass(object.getName());
	}

}
