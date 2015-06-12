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
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;

class GridsterAjaxBehavior extends AbstractDefaultAjaxBehavior {
	
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
		final Map<String, AbstractWidget<?>> widgetsByMarkupId = new HashMap<String, AbstractWidget<?>>();
		dashboard.visitChildren(AbstractWidget.class, new IVisitor<AbstractWidget<?>, Void>() {

			@Override
			public void component(AbstractWidget<?> widget, IVisit<Void> visit) {
				widgetsByMarkupId.put(widget.getMarkupId(), widget);
				visit.dontGoDeeper();
			}
			
		});
		try {
			JSONArray jsonArray = new JSONArray(data);
			for(int i=0; i<jsonArray.length();i++) {
				JSONObject jsonWidget = jsonArray.getJSONObject(i);
				String markupId = jsonWidget.getString("id");
				AbstractWidget<?> widget = widgetsByMarkupId.get(markupId);
				GridsterWidgetBehavior behaviour = GridsterWidgetBehavior.getBehaviour(widget);
				behaviour.setCol(jsonWidget.getInt("col"));
				behaviour.setRow(jsonWidget.getInt("row"));
				behaviour.setSizeX(jsonWidget.getInt("size_x"));
				behaviour.setSizeY(jsonWidget.getInt("size_y"));
			}
		} catch (JSONException e) {
			throw new WicketRuntimeException("Can't handle dashboard update", e);
		}
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "gridster orienteer", " ");
	}
	
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forReference(GridsterDashboardSupport.GRIDSTER_JS));
		response.render(CssHeaderItem.forReference(GridsterDashboardSupport.GRIDSTER_CSS));
		response.render(CssHeaderItem.forReference(GridsterDashboardSupport.WIDGET_CSS));
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("componentId", component.getMarkupId());
		variables.put("callBackScript", getCallbackScript());
		TextTemplate template = new PackageTextTemplate(GridsterDashboardSupport.class, "widget.tmpl.js");
		String script = template.asString(variables);
		response.render(OnDomReadyHeaderItem.forScript(script));
	}
}