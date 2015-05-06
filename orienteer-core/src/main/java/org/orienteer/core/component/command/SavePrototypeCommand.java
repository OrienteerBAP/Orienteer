package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link Command} to save {@link IPrototype} based entities
 *
 * @param <T> the type of an entity to which this command can be applied
 */
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
