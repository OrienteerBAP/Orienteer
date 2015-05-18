package org.orienteer.core.widget;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.orienteer.core.widget.command.AddWidgetCommand;
import org.orienteer.core.widget.command.UnhideWidgetCommand;

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
	private IDashboardManager dashboardManager;
	
	private String domain;
	
	private String tab;
	
	private RepeatingView commands;
	
	private RepeatingView widgets;
	
	private AbstractDefaultAjaxBehavior ajaxBehavior;
	
	public DashboardPanel(String id, String domain, String tab, IModel<T> model) {
		super(id, model);
		this.domain = domain;
		this.tab = tab;
		commands = new RepeatingView("commands");
		commands.add(new AddWidgetCommand<T>(commands.newChildId()));
		commands.add(new UnhideWidgetCommand<T>(commands.newChildId()));
		add(commands);
		widgets = new RepeatingView("widgets");
		add(widgets);
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
		
		dashboardManager.initializeDashboard(this);
	}
	
	private void updateDashboardByJson(String dashboard) {
		final Map<String, AbstractWidget<?>> widgetsByMarkupId = new HashMap<String, AbstractWidget<?>>();
		visitChildren(AbstractWidget.class, new IVisitor<AbstractWidget<?>, Void>() {

			@Override
			public void component(AbstractWidget<?> widget, IVisit<Void> visit) {
				widgetsByMarkupId.put(widget.getMarkupId(), widget);
				visit.dontGoDeeper();
			}
			
		});
		try {
			JSONArray jsonArray = new JSONArray(dashboard);
			for(int i=0; i<jsonArray.length();i++) {
				JSONObject jsonWidget = jsonArray.getJSONObject(i);
				String markupId = jsonWidget.getString("id");
				AbstractWidget<?> widget = widgetsByMarkupId.get(markupId);
				widget.setCol(jsonWidget.getInt("col"));
				widget.setRow(jsonWidget.getInt("row"));
				widget.setSizeX(jsonWidget.getInt("size_x"));
				widget.setSizeY(jsonWidget.getInt("size_y"));
				storeDashboard();
			}
		} catch (JSONException e) {
			throw new WicketRuntimeException("Can't handle dashboard update", e);
		}
	}
	
	public void storeDashboard() {
		dashboardManager.storeDashboard(this); 
	}
	
	public String newWidgetId()
	{
		return widgets.newChildId();
	}
	
	public List<AbstractWidget<T>> getWidgets()
	{
		final List<AbstractWidget<T>> ret = new ArrayList<AbstractWidget<T>>();
		visitChildren(AbstractWidget.class, new IVisitor<AbstractWidget<T>, Void>() {

			@Override
			public void component(AbstractWidget<T> object, IVisit<Void> visit) {
				ret.add(object);
				visit.dontGoDeeper();
			}
		});
		return ret;
	}
	
	public AbstractWidget<T> addWidget(AbstractWidget<T> widget)
	{
		widgets.add(widget);
		return widget;
	}
	
	public AbstractWidget<T> addWidget(IWidgetType<T> description)
	{
		return addWidget(description.instanciate(newWidgetId(), getModel()));
	}
	
	public DashboardPanel<T> deleteWidget(AbstractWidget<T> widget)
	{
		widgets.remove(widget);
		return this;
	}
	
	public String getDomain() {
		return domain;
	}

	public String getTab() {
		return tab;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		int row = 1;
		for(Component child : widgets)
		{
			AbstractWidget<?> widget = (AbstractWidget<?>) child;
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
	
	public RepeatingView getWidgetsContainer() {
		return widgets;
	}

}
