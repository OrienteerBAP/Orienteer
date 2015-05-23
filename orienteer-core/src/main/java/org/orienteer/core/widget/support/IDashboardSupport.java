package org.orienteer.core.widget.support;

import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.support.gridster.GridsterDashboardSupport;

import com.google.inject.ImplementedBy;

/**
 * Interface for different dashboard implementations
 */
@ImplementedBy(GridsterDashboardSupport.class)
public interface IDashboardSupport extends IClusterable{

	public void initDashboardPanel(DashboardPanel<?> dashboard);
	public void initWidget(AbstractWidget<?> widget);
	public void ajaxAddWidget(AbstractWidget<?> widget, AjaxRequestTarget target);
	public void ajaxDeleteWidget(AbstractWidget<?> widget, AjaxRequestTarget target);
//	public void updateDashboardByJson(DashboardPanel<?> dashboard, String data);
}
