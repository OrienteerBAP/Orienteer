package org.orienteer.core.widget;

import java.util.List;

import org.apache.wicket.model.IModel;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Manager to help load required dashboard
 */
@ImplementedBy(DefaultDashboardManager.class)
public interface IDashboardManager {
	public ODocument createWidgetDocument(IWidgetType<?> widgetType);
	public List<String> listTabs(String domain, IModel<?> dataModel);
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel);
}
