package org.orienteer.core.component.structuretable;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.IMetaContext;

/**
 * {@link StructureTable} which allow to use meta micro-framework ( {@link IMetaContext} )
 *
 * @param <T> the type of main object for this table
 * @param <C> the type of criterias to be used for this table
 */
public abstract class OrienteerStructureTable<T, C> extends StructureTable<T, C> implements IMetaContext<C>, ICommandsSupportComponent<T>
{
	private static final long serialVersionUID = 1L;
	private StructureTableCommandsToolbar<T> commandsToolbar;

	public OrienteerStructureTable(String id, IModel<T> model, IModel<? extends List<C>> criteriesModel)
	{
		super(id, model, criteriesModel);
		initialize();
	}

	public OrienteerStructureTable(String id, IModel<T> model, List<C> list)
	{
		super(id, model, list);
		initialize();
	}

	protected void initialize()
	{
		add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
		commandsToolbar = new StructureTableCommandsToolbar<T>(this);
		addTopToolbar(commandsToolbar);
	}

	public StructureTableCommandsToolbar<T> getCommandsToolbar() {
		return commandsToolbar;
	}
	
	@Override
	public OrienteerStructureTable<T, C> addCommand(Command<T> command)
	{
		getCommandsToolbar().addCommand(command);
		return this;
	}
	
	@Override
	public OrienteerStructureTable<T, C> removeCommand(Command<T> command) {
		getCommandsToolbar().removeCommand(command);
		return this;
	}

	@Override
	public String newCommandId() {
		return getCommandsToolbar().newCommandId();
	}

	@Override
	protected abstract Component getValueComponent(String id, IModel<C> rowModel);

	@Override
	public OrienteerStructureTable<T, C> setCaptionModel(IModel<String> captionModel)
	{
		super.setCaptionModel(captionModel);
		return this;
	}
	
	@Override
	public OrienteerStructureTable<T, C> getContextComponent() {
		return this;
	}
	
	@Override
	public <K extends AbstractMetaPanel<?, C, ?>> K getMetaComponent(C critery) {
		return AbstractMetaPanel.getMetaComponent(this, critery);
	}
	
	/*@Override
	public void onEvent(IEvent<?> event) {
		
		if(Broadcast.BUBBLE.equals(event.getType())) {
			Object payload = event.getPayload();
			AjaxRequestTarget target=null;
			if(payload instanceof AjaxRequestTarget) target=(AjaxRequestTarget) payload;
			else if(payload instanceof ActionPerformedEvent) target = ((ActionPerformedEvent<?>)payload).getTarget();
			
			if(target!=null) {
				target.add(this);
				onAjaxUpdate(target);
				if(target.equals(payload)) event.stop();
			}
		}
	}*/
	
	public void onAjaxUpdate(AjaxRequestTarget target)
	{
	}

}
