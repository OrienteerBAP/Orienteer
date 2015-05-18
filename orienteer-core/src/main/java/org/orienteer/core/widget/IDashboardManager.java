package org.orienteer.core.widget;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Manager to help load required dashboard
 */
@ImplementedBy(DefaultDashboardManager.class)
public interface IDashboardManager {
	public List<String> listTabs(String domain);
	public <T> AbstractWidget<T> createWidgetFromDocument(DashboardPanel<T> dashboard, ODocument doc);
	public ODocument getExistingDashboard(DashboardPanel<?> dashboard);
	public <T> void initializeDashboard(DashboardPanel<T> dashboard);
	public <T> void storeDashboard(DashboardPanel<T> dashboard);
}
