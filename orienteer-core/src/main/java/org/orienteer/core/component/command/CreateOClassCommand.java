package org.orienteer.core.component.command;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.schema.OClassPage;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResources;

/**
 * {@link Command} to create {@link OClass}
 */
@RequiredOrientResources({
	@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.CREATE),
	@RequiredOrientResource(value=OSecurityHelper.CLUSTER, specific="internal", permissions=OrientPermission.CREATE)
})
public class CreateOClassCommand extends AbstractCreateCommand<OClass>
{
	private IModel<OClass> superClassModel;

	public CreateOClassCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(table);
	}

	public CreateOClassCommand(OrienteerDataTable<OClass, ?> table, IModel<OClass> superClassModel) {
		super(table);
		this.superClassModel = superClassModel;
	}

	@Override
	public void onClick() {
		OClass oClass = OClassPrototyper.newPrototype();
		if (superClassModel != null && superClassModel.getObject() != null) {
			oClass.addSuperClass(superClassModel.getObject());
		}

		setResponsePage(new OClassPage(new OClassModel(oClass)).setModeObject(DisplayMode.EDIT));
	}

}
