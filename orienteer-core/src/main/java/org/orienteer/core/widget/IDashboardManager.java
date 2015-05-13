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
	public ODashboardDescriptor getExistingDashboard(String domain, String tab);
	public ODashboardDescriptor getDashboard(String domain, String tab);
	public IDashboardManager saveDashboard(ODashboardDescriptor dashboard);
	public IDashboardManager saveDashboard(ODashboardDescriptor dashboard, ODocument identity);
}
