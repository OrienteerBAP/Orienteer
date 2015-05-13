package org.orienteer.core.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardManager;
import org.orienteer.core.widget.TestWidget;
import org.wicketstuff.annotation.mount.MountPath;

import com.google.inject.Inject;

/**
 * Test page for widgets. Temporal.
 */
@MountPath("/widget")
public class WidgetPage extends OrienteerBasePage<String> {
	
	@Inject
	IDashboardManager dashboardManager;

	public WidgetPage() {
		super();
	}

	public WidgetPage(IModel<String> model) {
		super(model);
	}

	public WidgetPage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		IModel<String> model = Model.of("This is a test. This is a test");
		DashboardPanel<String> dashboard = new DashboardPanel<String>("dashboard", "test", "test", model);
//		dashboard.addWidget(new TestWidget(dashboard.newWidgetId(), model));
//		dashboard.addWidget(new TestWidget(dashboard.newWidgetId(), model));
//		dashboard.addWidget(new TestWidget(dashboard.newWidgetId(), model));
		add(dashboard);
	}
	
}
