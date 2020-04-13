package org.orienteer.core.widget;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.meta.IModeAware;
import org.orienteer.core.component.property.DisplayMode;

/**
 * 
 * Object contains {@link IDashboard}
 * @param <T> type of the main object for the container
 */
public interface IDashboardContainer<T> {

	public void setCurrentDashboard(IDashboard<T> dashboard);
	public IDashboard<T> getCurrentDashboard();
	
	public Component getSelfComponent();
	
	default public boolean hasDashboard() {
		return getCurrentDashboard()!=null;
	}
	
	default public DashboardPanel<T> getCurrentDashboardComponent() {
		IDashboard<T> dashboard = getCurrentDashboard();
		return dashboard!=null?dashboard.getSelfComponent():null;
	}
	
	default public IModel<DisplayMode> getDashboardModeModel() {
		IDashboard<T> dashboard = getCurrentDashboard();
		return dashboard!=null?dashboard.getModeModel():null;
	}
	
	default public DisplayMode getDashboardModeObject() {
		IDashboard<T> dashboard = getCurrentDashboard();
		return dashboard!=null?dashboard.getModeObject():DisplayMode.VIEW;
	}
	
	default public IDashboardContainer<T> setDashboardModeObject(DisplayMode mode) {
		getCurrentDashboard().setModeObject(mode);
		return this;
	}
	
}
