package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTableCommandsToolbar;
import ru.ydn.wicket.wicketorientdb.utils.proto.IPrototype;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassSaveCommand extends SimpleSaveCommand<OClass>
{
	private IModel<OClass> classModel;
	public OClassSaveCommand(OrienteerStructureTable<OClass,?> table,
			IModel<DisplayMode> displayModeModel, IModel<OClass> classModel)
	{
		super(table, displayModeModel);
		this.classModel = classModel;
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		OClass oClass = classModel.getObject();
		if(oClass instanceof IPrototype)
		{
			((IPrototype<?>)oClass).realizePrototype();
		}
		super.onClick(target);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(classModel!=null) classModel.detach();
	}
	
}
