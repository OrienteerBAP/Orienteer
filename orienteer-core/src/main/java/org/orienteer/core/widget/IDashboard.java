package org.orienteer.core.widget;

/**
 * 
 * Dashboard interface
 *
 * @param <T> param for {@link DashboardPanel}
 */
public interface IDashboard<T> {
	public DashboardPanel<T> getSelfComponent();
}
