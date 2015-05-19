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
	public ODocument getExistingDashboard(String domain, String tab);
}
