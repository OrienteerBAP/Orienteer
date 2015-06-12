package org.orienteer.core.widget.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.widget.DashboardPanel;

/**
 * Command to save silently current dashboard
 *
 * @param <T> the type of main object for a {@link DashboardPanel}
 */
public class SilentSaveDashboardCommand<T> extends AjaxCommand<T> {

	public SilentSaveDashboardCommand(String id) {
		super(id, "command.save");
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
		setBootstrapSize(BootstrapSize.EXTRA_SMALL);
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		DashboardPanel<?> dashboard = findParent(DashboardPanel.class);
		dashboard.storeDashboard();
		target.add(dashboard);
	}
}
