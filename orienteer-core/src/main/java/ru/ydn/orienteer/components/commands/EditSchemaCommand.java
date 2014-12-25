package ru.ydn.orienteer.components.commands;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

@RequiredOrientResource(value=ORule.ResourceGeneric.SCHEMA, permissions=OrientPermission.UPDATE)
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
