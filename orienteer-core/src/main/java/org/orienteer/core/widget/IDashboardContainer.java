package org.orienteer.core.widget;

import org.apache.wicket.Component;

/**
 * 
 * Object contains {@link IDashboard}
 *
 */
public interface IDashboardContainer {

	public void setCurrentDashboard(IDashboard dashboard);
	public IDashboard getCurrentDashboard();
	
	public Component getSelf();
	
}
