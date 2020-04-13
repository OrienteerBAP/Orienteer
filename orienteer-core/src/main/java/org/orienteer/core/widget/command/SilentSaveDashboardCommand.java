package org.orienteer.core.widget.command;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardContainer;

import java.util.Optional;

/**
 * Command to save silently current dashboard
 */
@OMethod(order=900+80,filters={
		@OFilter(fClass = PlaceFilter.class, fData = "DASHBOARD_SETTINGS"),
})
public class SilentSaveDashboardCommand extends AjaxCommand<ODocument> {
	private static final long serialVersionUID = 1L;

	public SilentSaveDashboardCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.save", dashboardDocumentModel);
		setIcon(FAIconType.save);
		setChangingDisplayMode(true);
	}
	
	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		IDashboardContainer<?> container = findParent(IDashboardContainer.class);
		DashboardPanel<?> dashboard = container.getCurrentDashboard().getSelfComponent();
		dashboard.storeDashboard();
		container.setDashboardModeObject(DisplayMode.VIEW);
		targetOptional.ifPresent(target -> {
			target.add(container.getSelfComponent().get("pageHeader"));
			target.add(dashboard);
		});
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		IDashboardContainer<?> dashboardContainer = findParent(IDashboardContainer.class);
		setVisible(dashboardContainer.hasDashboard() && dashboardContainer.getDashboardModeObject().canModify());
	}
}
