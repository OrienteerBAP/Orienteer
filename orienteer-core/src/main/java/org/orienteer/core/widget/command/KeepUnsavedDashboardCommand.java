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

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command to cancel modification of a dashboard. State kept unsaved
 */
public class KeepUnsavedDashboardCommand extends AjaxCommand<ODocument> {

	public KeepUnsavedDashboardCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.keep.widget", dashboardDocumentModel);
		setIcon(FAIconType.times);
		setBootstrapType(BootstrapType.DEFAULT);
		setBootstrapSize(BootstrapSize.EXTRA_SMALL);
		setChangingDisplayMode(true);
	}
	
	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		DashboardPanel<?> dashboard = findParent(DashboardPanel.class);
		dashboard.getModeModel().setObject(DisplayMode.VIEW);
		targetOptional.ifPresent(target -> target.add(dashboard));
	}

}
