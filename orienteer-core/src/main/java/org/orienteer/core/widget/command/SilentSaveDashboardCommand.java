package org.orienteer.core.widget.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.DashboardPanel;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command to save silently current dashboard
 */
public class SilentSaveDashboardCommand extends AjaxCommand<ODocument> {

	public SilentSaveDashboardCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.save", dashboardDocumentModel);
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
		setBootstrapSize(BootstrapSize.EXTRA_SMALL);
		setChangingDisplayMode(true);
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		DashboardPanel<?> dashboard = findParent(DashboardPanel.class);
		dashboard.storeDashboard();
		dashboard.getModeModel().setObject(DisplayMode.VIEW);
		target.add(dashboard);
	}
}
