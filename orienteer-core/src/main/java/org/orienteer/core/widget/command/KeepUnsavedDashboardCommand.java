package org.orienteer.core.widget.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardContainer;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command to cancel modification of a dashboard. State kept unsaved
 */
public class KeepUnsavedDashboardCommand extends AjaxCommand<ODocument> {
	private static final long serialVersionUID = 1L;

	public KeepUnsavedDashboardCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.keep.widget", dashboardDocumentModel);
		setIcon(FAIconType.times);
		setChangingDisplayMode(true);
	}
	
	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		IDashboardContainer container = findParent(IDashboardContainer.class);
		DashboardPanel<?> dashboard = container.getCurrentDashboard().getSelfComponent();
		dashboard.getModeModel().setObject(DisplayMode.VIEW);
		targetOptional.ifPresent(target -> target.add(dashboard));
	}

}
