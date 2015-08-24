package org.orienteer.core.component.command;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.schema.OClassPage;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * {@link Command} to create {@link OClass}
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.CREATE)
public class CreateOClassCommand extends AbstractCreateCommand<OClass>
{
	private OClass superClass;

	public CreateOClassCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(table);
	}

	public CreateOClassCommand(OrienteerDataTable<OClass, ?> table, OClass superClass) {
		super(table);
		this.superClass = superClass;
	}

	@Override
	public void onClick() {
		OClass oClass = OClassPrototyper.newPrototype();
		if (superClass != null) {
			oClass.addSuperClass(superClass);
		}

		setResponsePage(new OClassPage(new OClassModel(oClass)).setModeObject(DisplayMode.EDIT));
	}

}
