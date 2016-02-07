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
	
	public static final UpdateOnActionPerformedEventBehavior INSTANCE_ALL_CONTINUE = new UpdateOnActionPerformedEventBehavior(false);
	public static final UpdateOnActionPerformedEventBehavior INSTANCE_ALL_STOP = new UpdateOnActionPerformedEventBehavior(true);
	public static final UpdateOnActionPerformedEventBehavior INSTANCE_CHANGING_CONTINUE = new UpdateChangingOnActionPerformedEventBehavior(false);
	public static final UpdateOnActionPerformedEventBehavior INSTANCE_CHANGING_STOP = new UpdateChangingOnActionPerformedEventBehavior(true);
	
	private static class UpdateChangingOnActionPerformedEventBehavior extends UpdateOnActionPerformedEventBehavior {
		
		public UpdateChangingOnActionPerformedEventBehavior(boolean stopEvent) {
			super(stopEvent);
		}

		@Override
		protected boolean match(ActionPerformedEvent<?> event,
				IEvent<?> wicketEvent) {
			return event.getCommand().isChangingModel();
		}
	}
	
	private final boolean stopEvent;
	
	public UpdateOnActionPerformedEventBehavior(boolean stopEvent) {
		this.stopEvent = stopEvent;
	}
	
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
			if(stopEvent) wicketEvent.stop();
		}
	}
	
	protected void update(Component component, ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
		event.getTarget().add(component);
	}
	
	protected boolean match(ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
		return true;
	}
}
