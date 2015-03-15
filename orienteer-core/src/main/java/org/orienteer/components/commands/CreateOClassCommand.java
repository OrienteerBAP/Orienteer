package org.orienteer.components.commands;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.web.schema.OClassPage;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.CREATE)
public class CreateOClassCommand extends AbstractCreateCommand<OClass>
{

	public CreateOClassCommand(DataTableCommandsToolbar<OClass> toolbar)
	{
		super(toolbar);
	}

	public CreateOClassCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(table);
	}

	@Override
	public void onClick() {
		setResponsePage(new OClassPage(new OClassModel(OClassPrototyper.newPrototype())).setDisplayMode(DisplayMode.EDIT));
	}

}
