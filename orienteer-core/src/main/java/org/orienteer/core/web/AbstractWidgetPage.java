package org.orienteer.core.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardManager;

import com.google.inject.Inject;

public abstract class AbstractWidgetPage<T> extends OrienteerBasePage<T> {
	
	@Inject
	private IDashboardManager dashboardManager;

	public AbstractWidgetPage() {
		super();
	}

	public AbstractWidgetPage(IModel<T> model) {
		super(model);
	}

	public AbstractWidgetPage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		add(new DashboardPanel<T>("dashboard", getDomain(), getTab(), getModel()));
	}
	
	public abstract String getDomain();
	
	public abstract String getTab();
	
}
