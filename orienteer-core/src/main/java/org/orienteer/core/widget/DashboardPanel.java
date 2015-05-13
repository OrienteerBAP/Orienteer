package org.orienteer.core.widget;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * Dashboard is {@link Panel} to allow manipulation with a set of {@link AbstractWidget}s
 *
 * @param <T> the type of main data object
 */
public class DashboardPanel<T> extends GenericPanel<T> {
	
	private final static WebjarsJavaScriptResourceReference GRIDSTER_JS = new WebjarsJavaScriptResourceReference("/gridster.js/current/jquery.gridster.js");
	private final static WebjarsCssResourceReference GRIDSTER_CSS = new WebjarsCssResourceReference("/gridster.js/current/jquery.gridster.css");
	private final static CssResourceReference WIDGET_CSS = new CssResourceReference(DashboardPanel.class, "widget.css");
	
	@Inject
	private IWidgetTypesRegistry widgetRegistry;
	
	@Inject
	private IDashboardManager dashboardManager;
	
	private RepeatingView repeatingView;
	
	private AbstractDefaultAjaxBehavior ajaxBehavior;
	
	public DashboardPanel(String id, String domain, String tab, IModel<T> model) {
		super(id, model);
		repeatingView = new RepeatingView("widgets");
		add(repeatingView);
		setOutputMarkupId(true);
		add(ajaxBehavior = new AbstractDefaultAjaxBehavior() {
			
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
				updateDashboardByJson(dashboard);
			}
		});
		
		ODashboardDescriptor descriptor = dashboardManager.getDashboard(domain, tab);
		updateDashboardByDescriptor(descriptor);
	}
	
	private void updateDashboardByDescriptor(ODashboardDescriptor descriptor) {
		Set<OWidgetDescriptor> widgets =  descriptor.getWidgets();
		for (OWidgetDescriptor oWidgetDescriptor : widgets) {
			String typeId = oWidgetDescriptor.getTypeId();
			IWidgetType<T, IWidgetSettings> widgetType = (IWidgetType<T, IWidgetSettings>)widgetRegistry.lookupByTypeId(typeId);
			Class<? extends IWidgetSettings> settingsType = widgetType.getSettingsType();
			IWidgetSettings settings;
			if(!settingsType.isInstance(oWidgetDescriptor))
			{
				try {
					settings = settingsType.getConstructor(ODocument.class).newInstance(oWidgetDescriptor.getDocument());
				} catch (Exception e) {
					throw new WicketRuntimeException("Can't instanciate settings for a widget", e);
				} 
			}
			else
			{
				settings = oWidgetDescriptor;
			}
			addWidget(widgetType, settings);
		}
	}
	
	private void updateDashboardByJson(String dashboard) {
		final Map<String, AbstractWidget<?, IWidgetSettings>> widgetsByMarkupId = new HashMap<String, AbstractWidget<?, IWidgetSettings>>();
		visitChildren(AbstractWidget.class, new IVisitor<AbstractWidget<?, IWidgetSettings>, Void>() {

			@Override
			public void component(AbstractWidget<?, IWidgetSettings> widget, IVisit<Void> visit) {
				widgetsByMarkupId.put(widget.getMarkupId(), widget);
				visit.dontGoDeeper();
			}
			
		});
		try {
			JSONArray jsonArray = new JSONArray(dashboard);
			for(int i=0; i<jsonArray.length();i++) {
				JSONObject jsonWidget = jsonArray.getJSONObject(i);
				String markupId = jsonWidget.getString("id");
				AbstractWidget<?, IWidgetSettings> widget = widgetsByMarkupId.get(markupId);
				IWidgetSettings settings = widget.getSettings();
				settings.setCol(jsonWidget.getInt("col"));
				settings.setRow(jsonWidget.getInt("row"));
				settings.setSizeX(jsonWidget.getInt("size_x"));
				settings.setSizeY(jsonWidget.getInt("size_y"));
				settings.persist();
			}
		} catch (JSONException e) {
			throw new WicketRuntimeException("Can't handle dashboard update", e);
		}
	}
	
	public String newWidgetId()
	{
		return repeatingView.newChildId();
	}
	
	public DashboardPanel<T> addWidget(AbstractWidget<T, ?> widget)
	{
		repeatingView.add(widget);
		return this;
	}
	
	public DashboardPanel<T> addWidget(IWidgetType<T, IWidgetSettings> description, IWidgetSettings settings)
	{
		return addWidget(description.instanciate(newWidgetId(), settings, getModel()));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		int row = 1;
		for(Component child : repeatingView)
		{
			AbstractWidget<?, IWidgetSettings> widget = (AbstractWidget<?, IWidgetSettings>) child;
			widget.configure();
			/*IWidgetSettings settings = widget.getSettings();
			if(settings.getCol()==null) settings.setCol(1);
			if(settings.getRow()==null) settings.setRow(row++);*/
		}
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "gridster orienteer", " ");
	}
	
	@SuppressWarnings("resource")
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(GRIDSTER_JS));
		response.render(CssHeaderItem.forReference(GRIDSTER_CSS));
		response.render(CssHeaderItem.forReference(WIDGET_CSS));
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("componentId", getMarkupId());
		variables.put("callBackScript", ajaxBehavior.getCallbackScript());
		TextTemplate template = new PackageTextTemplate(DashboardPanel.class, "widget.tmpl.js");
		String script = template.asString(variables);
		response.render(OnDomReadyHeaderItem.forScript(script));
	}

}
