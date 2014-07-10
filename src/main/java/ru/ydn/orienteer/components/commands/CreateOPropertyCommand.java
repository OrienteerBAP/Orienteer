package ru.ydn.orienteer.components.commands;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.schema.ClassPage;
import ru.ydn.orienteer.web.schema.PropertyPage;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.utils.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.utils.proto.OPropertyPrototyper;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class CreateOPropertyCommand extends AbstractCreateCommand<OProperty> {

	private IModel<OClass> classModel;
	
	public CreateOPropertyCommand(OrienteerDataTable<OProperty, ?> table, IModel<OClass> classModel) {
		super(table);
		Args.notNull(classModel, "classModel");
		this.classModel = classModel;
	}

	@Override
	public void onClick() {
		setResponsePage(new PropertyPage(new OPropertyModel(OPropertyPrototyper.newPrototype(classModel.getObject().getName()))).setDisplayMode(DisplayMode.EDIT));
	}

	@Override
	public void detachModels() {
		super.detachModels();
		classModel.detach();
	}
	
	

}
