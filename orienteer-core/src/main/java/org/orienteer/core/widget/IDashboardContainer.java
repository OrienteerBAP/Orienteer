package org.orienteer.core.widget;

import java.io.Serializable;

import org.apache.wicket.Component;

public interface IDashboardContainer {

	public void setCurrentDashboard(IDashboard dashboard);
	public IDashboard getCurrentDashboard();
	
	public Component getSelf();
	
}
