package org.orienteer.core.widget.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardContainer;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command to enable modification of dashboard
 */
public class ConfigureDashboardCommand extends AjaxCommand<ODocument> {
	private static final long serialVersionUID = 1L;

	public ConfigureDashboardCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.configure.widget", dashboardDocumentModel);
		setIcon(FAIconType.gear);
		setChangingDisplayMode(true);
	}

	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		IDashboardContainer container = findParent(IDashboardContainer.class);
		DashboardPanel<?> dashboard = container.getCurrentDashboard().getSelfComponent();
		dashboard.getModeModel().setObject(DisplayMode.EDIT);
		targetOptional.ifPresent(target -> target.add(dashboard));
	}

}
