package org.orienteer.core.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.event.ActionPerformedEvent;

/**
 * Behavior to update component if model changing {@link Command} was executed
 */
public class UpdateOnActionPerformedEventBehavior extends Behavior {
	
	public static final UpdateOnActionPerformedEventBehavior INSTANCE = new UpdateOnActionPerformedEventBehavior();
	
	@Override
	public void bind(Component component) {
		super.bind(component);
		component.setOutputMarkupId(true);
	}

	@Override
	public void onEvent(Component component, IEvent<?> wicketEvent) {
		Object payload = wicketEvent.getPayload();
		if(payload instanceof ActionPerformedEvent) {
			ActionPerformedEvent<?> event = (ActionPerformedEvent<?>)payload;
			if(event.isAjax() && match(event, wicketEvent)) {
				update(component, event, wicketEvent);
			}
		}
	}
	
	protected void update(Component component, ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
		event.getTarget().add(component);
	}
	
	protected boolean match(ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
		return event.getCommand().isChangingModel();
	}
}
