package org.orienteer.core.widget.support.jquery;

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
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;

class JQueryDashboardAjaxBehavior extends AbstractDefaultAjaxBehavior {
	@Override
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		super.updateAjaxAttributes(attributes);
		attributes.getDynamicExtraParameters().add("return {dashboard: JSON.stringify(serialized)};");
		attributes.setMethod(Method.POST);
	}
	@Override
	protected void respond(AjaxRequestTarget target) {
		String dashboard = RequestCycle.get().getRequest().getRequestParameters()
											 .getParameterValue("dashboard").toString();
		updateDashboardByJson((DashboardPanel<?>)getComponent(), dashboard);
	}
	
	public void updateDashboardByJson(DashboardPanel<?> dashboard, String data) {
		try {
			JSONArray jsonArray = new JSONArray(data);
			RepeatingView widgetsContainer = dashboard.getWidgetsContainer();
			for(int i=0; i<jsonArray.length();i++) {
				String markupId = jsonArray.getString(i);
				AbstractWidget<?> widget = (AbstractWidget<?>) widgetsContainer.get(i);
				if(!widget.getMarkupId().equals(markupId)) {
					for(int j=0; j<widgetsContainer.size(); j++) {
						if(widgetsContainer.get(j).getMarkupId().equals(markupId)) {
							widgetsContainer.swap(i, j);
						}
					}
				}
			}
		} catch (JSONException e) {
			throw new WicketRuntimeException("Can't handle dashboard update", e);
		}
	}
	
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		DashboardPanel<?> dashboard = (DashboardPanel<?>)component;
		response.render(CssHeaderItem.forReference(JQueryDashboardSupport.WIDGET_CSS));
		response.render(JavaScriptHeaderItem.forReference(JQueryDashboardSupport.JQUERY_UI_JS));
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("componentId", dashboard.getMarkupId());
		variables.put("callBackScript", getCallbackScript());
		variables.put("disabled", !DisplayMode.EDIT.equals(dashboard.getModeObject()));
		TextTemplate template = new PackageTextTemplate(JQueryDashboardAjaxBehavior.class, "widget.tmpl.js");
		String script = template.asString(variables);
		response.render(OnDomReadyHeaderItem.forScript(script));
	}
}
