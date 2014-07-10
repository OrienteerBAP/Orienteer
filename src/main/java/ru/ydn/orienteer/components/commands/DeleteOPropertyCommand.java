package ru.ydn.orienteer.components.commands;

import java.util.List;

import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class DeleteOPropertyCommand extends AbstractDeleteCommand<OProperty> {

	public DeleteOPropertyCommand(DataTableCommandsToolbar<OProperty> toolbar) {
		super(toolbar);
	}
	
	public DeleteOPropertyCommand(OrienteerDataTable<OProperty, ?> table) {
		super(table);
	}
	
	@Override
	protected void performMultiAction(List<OProperty> objects) {
		getDatabase().commit();
		super.performMultiAction(objects);
		getDatabase().begin();
	}

	@Override
	protected void perfromSingleAction(OProperty object) {
		object.getOwnerClass().dropProperty(object.getName());
	}

}
