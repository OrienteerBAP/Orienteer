package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ICommandsSupportComponent;
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
	public SavePrototypeCommand(ICommandsSupportComponent<T> component,
			IModel<DisplayMode> displayModeModel, IModel<T> model) {
		super(component, displayModeModel, model);
	}
	public SavePrototypeCommand(ICommandsSupportComponent<T> component,
			IModel<DisplayMode> displayModeModel) {
		super(component, displayModeModel);
	}

	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		IModel<T> model = getModel();
		T object = model!=null?model.getObject():null;
		if(object instanceof IPrototype)
		{
			boolean isActiveTransaction = getDatabase().getTransaction().isActive();
			if(isActiveTransaction) getDatabase().commit();
			try {
				((IPrototype<?>)object).realizePrototype();
				model.detach();
			} finally {
				if(isActiveTransaction) getDatabase().begin();
			}
		}
		super.onClick(targetOptional);
	}
}
