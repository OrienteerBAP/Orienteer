package org.orienteer.core.web;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.widget.TestWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardManager;

import com.google.inject.Inject;

/**
 * Test page for widgets. Temporal.
 */
@MountPath("/widget")
public class WidgetPage extends AbstractWidgetPage<String> {
	
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
	public String getDomain() {
		return "test";
	}

	@Override
	public List<String> getTabs() {
		return Arrays.asList("test", "test2", "test3");
	}
	
}
