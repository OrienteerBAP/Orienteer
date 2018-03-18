package org.orienteer.core.widget.support.jquery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
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
import org.orienteer.core.component.ReorderableRepeatingView;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONException;

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
			ReorderableRepeatingView widgetsContainer = dashboard.getWidgetsContainer();
			Map<String, String> markupIdsMap = widgetsContainer.stream().collect(Collectors.toMap(Component::getMarkupId, Component::getId));
			List<String> requiredOrder = new ArrayList<>();
			for(int i=0; i<jsonArray.length();i++) { 
				String componentId = markupIdsMap.get(jsonArray.getString(i));
				if(componentId!=null) requiredOrder.add(componentId);
			}
			widgetsContainer.setComponentOrderByIds(requiredOrder);
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
