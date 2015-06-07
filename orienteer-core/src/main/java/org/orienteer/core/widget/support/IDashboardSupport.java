package org.orienteer.core.widget.support;

import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.support.gridster.GridsterDashboardSupport;
import org.orienteer.core.widget.support.jquery.JQueryDashboardSupport;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Interface for different dashboard implementations
 */
@ImplementedBy(JQueryDashboardSupport.class)
public interface IDashboardSupport extends IClusterable{

	public void initDashboardPanel(DashboardPanel<?> dashboard);
	public void initWidget(AbstractWidget<?> widget);
	public void ajaxAddWidget(AbstractWidget<?> widget, AjaxRequestTarget target);
	public void ajaxDeleteWidget(AbstractWidget<?> widget, AjaxRequestTarget target);
	public void saveSettings(AbstractWidget<?> widget, ODocument doc);
	public void loadSettings(AbstractWidget<?> widget, ODocument doc);
//	public void updateDashboardByJson(DashboardPanel<?> dashboard, String data);
}
