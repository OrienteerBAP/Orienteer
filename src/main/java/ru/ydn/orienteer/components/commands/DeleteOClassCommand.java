package ru.ydn.orienteer.components.commands;

import java.util.List;

import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;

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
