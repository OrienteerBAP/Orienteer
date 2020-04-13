package org.orienteer.core.widget;

import org.orienteer.core.component.meta.IDisplayModeAware;

/**
 * 
 * Dashboard interface
 *
 * @param <T> param for {@link DashboardPanel}
 */
public interface IDashboard<T> extends IDisplayModeAware {
	public DashboardPanel<T> getSelfComponent();
}
