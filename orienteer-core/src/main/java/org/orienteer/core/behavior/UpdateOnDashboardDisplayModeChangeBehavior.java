package org.orienteer.core.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;

/**
 * {@link Behavior} to refresh {@link AbstractWidget} when {@link DashboardPanel} switching {@link DisplayMode}
 */
public class UpdateOnDashboardDisplayModeChangeBehavior extends UpdateOnActionPerformedEventBehavior {

	public static final UpdateOnDashboardDisplayModeChangeBehavior INSTANCE = new UpdateOnDashboardDisplayModeChangeBehavior();
	
	public UpdateOnDashboardDisplayModeChangeBehavior() {
		super(false);
	}

	@Override
	protected boolean match(Component component, ActionPerformedEvent<?> event,
			IEvent<?> wicketEvent) {
		if(component instanceof AbstractWidget && event.getCommand().isChangingDisplayMode()) {
			AbstractWidget<?> widget = (AbstractWidget<?>)component;
			return Objects.equal(widget.getDashboardPanel().getDashboardDocument(), event.getObject());
		} else return false;
	}
	
}
