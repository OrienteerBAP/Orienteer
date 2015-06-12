package org.orienteer.core.widget.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.DashboardPanel;

/**
 * Command to cancel modification of a dashboard
 *
 * @param <T> the type of main object for a {@link DashboardPanel}
 */
public class CancelSaveDashboardCommand<T> extends AjaxCommand<T> {

	public CancelSaveDashboardCommand(String id) {
		super(id, "command.cancel");
		setIcon(FAIconType.times);
		setBootstrapType(BootstrapType.DEFAULT);
		setBootstrapSize(BootstrapSize.EXTRA_SMALL);
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		DashboardPanel<?> dashboard = findParent(DashboardPanel.class);
		dashboard.getModeModel().setObject(DisplayMode.VIEW);
		target.add(dashboard);
	}

}
