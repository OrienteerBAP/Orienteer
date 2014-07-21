package ru.ydn.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.schema.OClassPage;
import ru.ydn.orienteer.web.schema.OPropertyPage;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;

@RequiredOrientResource(value = ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.CREATE)
public class CreateOPropertyCommand extends AbstractCreateCommand<OProperty> {

	private IModel<OClass> classModel;
	
	public CreateOPropertyCommand(OrienteerDataTable<OProperty, ?> table, IModel<OClass> classModel) {
		super(table);
		Args.notNull(classModel, "classModel");
		this.classModel = classModel;
	}

	@Override
	public void onClick() {
		setResponsePage(new OPropertyPage(new OPropertyModel(OPropertyPrototyper.newPrototype(classModel.getObject().getName()))).setDisplayMode(DisplayMode.EDIT));
	}

	@Override
	public void detachModels() {
		super.detachModels();
		classModel.detach();
	}
	
	

}
