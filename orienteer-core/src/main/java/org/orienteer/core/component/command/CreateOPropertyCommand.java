package org.orienteer.core.component.command;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.schema.OPropertyPage;

import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResources;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link Command} to create {@link OProperty}
 */
@RequiredOrientResources({
	@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.CREATE),
	@RequiredOrientResource(value=OSecurityHelper.CLUSTER, specific="internal", permissions=OrientPermission.CREATE)
})
public class CreateOPropertyCommand extends AbstractCreateCommand<OProperty> {

	private IModel<OClass> classModel;
	
	public CreateOPropertyCommand(OrienteerDataTable<OProperty, ?> table, IModel<OClass> classModel) {
		super(table);
		Args.notNull(classModel, "classModel");
		this.classModel = classModel;
	}

	@Override
	public void onClick() {
		OClass oClass = classModel.getObject();
		OProperty newProperty = OPropertyPrototyper.newPrototype(oClass.getName());
		CustomAttribute.ORDER.setValue(newProperty, findMaxOrder(oClass)+10);
		setResponsePage(new OPropertyPage(new OPropertyModel(newProperty)).setModeObject(DisplayMode.EDIT));
	}
	
	public int findMaxOrder(OClass oClass)
	{
		int ret = 0;
		for(OProperty property: oClass.properties())
		{
			Integer order = CustomAttribute.ORDER.getValue(property);
			if(order!=null && order > ret) ret = order;
		}
		return ret;
	}

	@Override
	public void detachModels() {
		super.detachModels();
		classModel.detach();
	}
	
	

}
