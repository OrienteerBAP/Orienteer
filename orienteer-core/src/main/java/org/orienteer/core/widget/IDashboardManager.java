package org.orienteer.core.widget;

import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IModel;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Manager to help load required dashboard
 */
@ImplementedBy(DefaultDashboardManager.class)
public interface IDashboardManager {
	public ODocument createWidgetDocument(IWidgetType<?> widgetType);
	public ODocument createWidgetDocument(Class<? extends AbstractWidget<?>> widgetClass);
	public <T> List<String> listTabs(String domain, Predicate<IWidgetType<T>> filter);
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel);
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel, Map<String, Object> criteriesMap);
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel, OClass oClass);
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel, OClass oClass, Map<String, Object> criteriesMap);
}
