package ru.ydn.orienteer.components.commands;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.schema.ClassPage;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.utils.proto.OClassPrototyper;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassCreateCommand extends SimpleCreateCommand<OClass>
{
	

	public OClassCreateCommand(DataTableCommandsToolbar<OClass> toolbar)
	{
		super(toolbar);
	}

	public OClassCreateCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(table);
	}

	@Override
	public void onClick() {
		setResponsePage(new ClassPage(new OClassModel(OClassPrototyper.newPrototype())).setDisplayMode(DisplayMode.EDIT));
	}

}
