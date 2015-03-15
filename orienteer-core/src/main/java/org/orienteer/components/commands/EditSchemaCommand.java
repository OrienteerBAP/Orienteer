package org.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

@RequiredOrientResource(value=OSecurityHelper.SCHEMA, permissions=OrientPermission.UPDATE)
public class EditSchemaCommand<T> extends EditCommand<T>
{

	public EditSchemaCommand(OrienteerDataTable<T, ?> table,
			IModel<DisplayMode> displayModeModel)
	{
		super(table, displayModeModel);
	}

	public EditSchemaCommand(DataTableCommandsToolbar<T> toolbar,
			IModel<DisplayMode> displayModeModel)
	{
		super(toolbar, displayModeModel);
	}

	public EditSchemaCommand(OrienteerStructureTable<T, ?> structureTable,
			IModel<DisplayMode> displayModeModel)
	{
		super(structureTable, displayModeModel);
	}

	public EditSchemaCommand(StructureTableCommandsToolbar<T> toolbar,
			IModel<DisplayMode> displayModeModel)
	{
		super(toolbar, displayModeModel);
	}

}
