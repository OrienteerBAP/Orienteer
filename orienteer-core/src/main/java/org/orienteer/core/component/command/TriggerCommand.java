package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link Command} to trigger something
 *
 * @param <T> the type of an entity to which this command can be applied
 */
public class TriggerCommand<T> extends AjaxCommand<T> {

	private static final long serialVersionUID = 1L;
	private IModel<Boolean> triggerModel;
	
	public TriggerCommand(OrienteerDataTable<T, ?> table, IModel<Boolean> triggerModel) {
		this(null, table, triggerModel);
	}

	public TriggerCommand(String resourceKey, OrienteerDataTable<T, ?> table, IModel<Boolean> triggerModel)
	{
		super(new StringResourceModel(
				resourceKey!=null
				? resourceKey.endsWith(".${}")?resourceKey:resourceKey+".${}"
				: "command.showhide.${}", triggerModel), table);
		this.triggerModel = triggerModel!=null?triggerModel:Model.of(true);
		setIcon(FAIconType.reorder);
		setBootstrapType(BootstrapType.INFO);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		Boolean current = triggerModel.getObject();
		current = current!=null?!current:true;
		triggerModel.setObject(current);
	}
	
	public IModel<Boolean> getTriggerModel() {
		return triggerModel;
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		triggerModel.detach();
	}

}
