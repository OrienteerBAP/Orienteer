package org.orienteer.core.widget;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.meta.IModeAware;
import org.orienteer.core.component.property.DisplayMode;

/**
 * 
 * Object contains {@link IDashboard}
 *
 */
public interface IDashboardContainer<T> extends IDisplayModeAware {

	public void setCurrentDashboard(IDashboard<T> dashboard);
	public IDashboard<T> getCurrentDashboard();
	
	public Component getSelfComponent();
	
	default public DashboardPanel<T> getCurrentDashboardComponent() {
		return getCurrentDashboard().getSelfComponent();
	}
	
	default public IModel<DisplayMode> getModeModel() {
		return getCurrentDashboard().getModeModel();
	}
	
	default public DisplayMode getModeObject() {
		return getCurrentDashboard().getModeObject();
	}
	
	default public IDashboardContainer<T> setModeObject(DisplayMode mode) {
		getCurrentDashboard().setModeObject(mode);
		return this;
	}
	
}
