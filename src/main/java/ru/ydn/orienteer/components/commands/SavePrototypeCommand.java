package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class SavePrototypeCommand<T> extends AbstractSaveCommand<T>
{
	private IModel<T> model;
	
	public SavePrototypeCommand(OrienteerDataTable<T, ?> table,
			IModel<DisplayMode> displayModeModel)
	{
		super(table, displayModeModel);
	}

	public SavePrototypeCommand(OrienteerStructureTable<T,?> table,
			IModel<DisplayMode> displayModeModel, IModel<T> model)
	{
		super(table, displayModeModel);
		this.model = model;
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		T object = model!=null?model.getObject():null;
		if(object instanceof IPrototype)
		{
			getDatabase().commit();
			((IPrototype<?>)object).realizePrototype();
			model.detach();
			getDatabase().begin();
		}
		super.onClick(target);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(model!=null) model.detach();
	}
	
}
