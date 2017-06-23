package org.orienteer.core.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.util.lang.Objects;
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
	public static final UpdateOnActionPerformedEventBehavior INSTANCE_ALWAYS = new UpdateAlwaysOnActionPerformedEventBehavior(false, false);
	public static final UpdateOnActionPerformedEventBehavior INSTANCE_ALWAYS_FOR_CHANGING = new UpdateAlwaysOnActionPerformedEventBehavior(false, true);
	
	private static class UpdateChangingOnActionPerformedEventBehavior extends UpdateOnActionPerformedEventBehavior {
		
		public UpdateChangingOnActionPerformedEventBehavior(boolean stopEvent) {
			super(stopEvent);
		}

		@Override
		protected boolean match(Component component, ActionPerformedEvent<?> event,
				IEvent<?> wicketEvent) {
			return super.match(component, event, wicketEvent) && event.getCommand().isChangingModel();
		}
	}

	private static class UpdateAlwaysOnActionPerformedEventBehavior extends UpdateOnActionPerformedEventBehavior {
		private final boolean shouldBeChanging;
		public UpdateAlwaysOnActionPerformedEventBehavior(boolean stopEvent, boolean shouldBeChanging) {
			super(stopEvent);
			this.shouldBeChanging = shouldBeChanging;
		}

		@Override
		protected boolean match(Component component, ActionPerformedEvent<?> event,
				IEvent<?> wicketEvent) {
			return !shouldBeChanging || event.getCommand().isChangingModel();
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
			if(event.isAjax() && match(component, event, wicketEvent)) {
				update(component, event, wicketEvent);
			}
			if(stopEvent) wicketEvent.stop();
		}
	}
	
	protected void update(Component component, ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
		component.configure();
		if(component.isVisibleInHierarchy()) event.getTarget().add(component);
	}
	
	protected boolean match(Component component, ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
		return Objects.equal(component.getDefaultModelObject(), event.getObject());
	}
}
