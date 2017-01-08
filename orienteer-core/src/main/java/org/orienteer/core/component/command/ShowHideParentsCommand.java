package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link Command} to show or hide parent things
 *
 * @param <T> the type of an entity to which this command can be applied
 */
public class ShowHideParentsCommand<T> extends TriggerCommand<T>
{
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;

	public ShowHideParentsCommand(IModel<OClass> classModel, OrienteerDataTable<T, ?> table, IModel<Boolean> showHideParentModel)
	{
		super("command.showhide.parent", table,  showHideParentModel);
		this.classModel = classModel;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(classModel!=null)
		{
			OClass oClass = classModel.getObject();
			setVisible(oClass!=null && oClass.hasSuperClasses());
		}
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(classModel!=null) classModel.detach();
	}

}
