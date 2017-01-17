package org.orienteer.core.widget.support.gridster;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.support.IDashboardSupport;

import com.orientechnologies.orient.core.record.impl.ODocument;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * {@link IDashboardSupport} for gridster.js
 */
public class GridsterDashboardSupport implements IDashboardSupport {
	
	final static WebjarsJavaScriptResourceReference GRIDSTER_JS = new WebjarsJavaScriptResourceReference("/gridster.js/current/jquery.gridster.min.js");
	final static WebjarsCssResourceReference GRIDSTER_CSS = new WebjarsCssResourceReference("/gridster.js/current/jquery.gridster.min.css");
	final static CssResourceReference WIDGET_CSS = new CssResourceReference(GridsterDashboardSupport.class, "widget.css");

	public void initDashboardPanel(DashboardPanel<?> dashboard) {
		dashboard.add(new GridsterAjaxBehavior());
	}
	
	@Override
	public void initWidget(AbstractWidget<?> widget) {
		widget.add(new GridsterWidgetBehavior());
	}
	
	@Override
	public void ajaxAddWidget(AbstractWidget<?> widget, AjaxRequestTarget target) {
		DashboardPanel<?> dashboard = widget.getDashboardPanel();
		target.prependJavaScript("$('#"+dashboard.getMarkupId()+" > ul').append('<li id=\\'"+widget.getMarkupId()+"\\'></li>')");
		target.add(widget);
		target.appendJavaScript("var gridster = $('#"+dashboard.getMarkupId()+" > ul').data('gridster');\n"
				+ "gridster.add_widget($('#"+widget.getMarkupId()+"'));\n"
				+ "gridster.gridsterChanged();");
	}
	
	@Override
	public void ajaxDeleteWidget(AbstractWidget<?> widget, AjaxRequestTarget target) {
		DashboardPanel<?> dashboard = widget.getDashboardPanel();
		target.prependJavaScript("var gridster = $('#"+dashboard.getMarkupId()+" > ul').data('gridster');\n"
						+ "gridster.remove_widget($('#"+widget.getMarkupId()+"'));\n"
						+ "gridster.gridsterChanged();");
	}
	
	@Override
	public void saveSettings(AbstractWidget<?> widget, ODocument doc) {
		GridsterWidgetBehavior.getBehaviour(widget).saveSettings(doc);
	}
	
	@Override
	public void loadSettings(AbstractWidget<?> widget, ODocument doc) {
		GridsterWidgetBehavior.getBehaviour(widget).loadSettings(doc);
	}

}
