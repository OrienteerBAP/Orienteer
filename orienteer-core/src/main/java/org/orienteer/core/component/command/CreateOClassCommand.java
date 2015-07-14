package org.orienteer.core.component.command;

import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.schema.OClassPage;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link Command} to create {@link OClass}
 */
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
		setResponsePage(new OClassPage(new OClassModel(OClassPrototyper.newPrototype())).setModeObject(DisplayMode.EDIT));
	}

}
