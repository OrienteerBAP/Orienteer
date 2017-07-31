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
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.command.AddWidgetCommand;
import org.orienteer.core.widget.command.KeepUnsavedDashboardCommand;
import org.orienteer.core.widget.command.ConfigureDashboardCommand;
import org.orienteer.core.widget.command.SilentSaveDashboardCommand;
import org.orienteer.core.widget.command.UnhideWidgetCommand;
import org.orienteer.core.widget.support.IDashboardSupport;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import static org.orienteer.core.module.OWidgetsModule.*;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * Dashboard is {@link Panel} to allow manipulation with a set of {@link AbstractWidget}s
 *
 * @param <T> the type of main data object
 */
public class DashboardPanel<T> extends GenericPanel<T> implements IDisplayModeAware {
	
	@Inject
	protected IDashboardManager dashboardManager;
	
	@Inject
	protected IWidgetTypesRegistry widgetTypesRegistry;
	
	@Inject
	private IDashboardSupport dashboardSupport;
	
	private String domain;
	
	private String tab;
	
	private ConfigureDashboardCommand configureCommand;
	
	private RepeatingView commands;
	
	private RepeatingView widgets;
	
	private AbstractDefaultAjaxBehavior ajaxBehavior;
	
	private IModel<DisplayMode> dashboardModeModel = DisplayMode.VIEW.asModel();
	
	private IModel<ODocument> dashboardDocumentModel = new ODocumentModel();
	
	private IWidgetFilter<T> widgetsFilter;
	
	public DashboardPanel(String id, String domain, String tab, IModel<T> model, IWidgetFilter<T> widgetsFilter) {
		super(id, model);
		this.domain = domain;
		this.tab = tab;
		this.widgetsFilter = widgetsFilter;
		WebMarkupContainer commandsContainer = new WebMarkupContainer("commandsContainer");
		OSecurityHelper.secureComponent(commandsContainer, OSecurityHelper.requireOClass(OWidgetsModule.OCLASS_DASHBOARD, OrientPermission.UPDATE));
		commandsContainer.add(configureCommand = new ConfigureDashboardCommand("configure", dashboardDocumentModel));
		commands = new RepeatingView("commands");
		commands.add(new AddWidgetCommand<T>(commands.newChildId(), dashboardDocumentModel));
		commands.add(new UnhideWidgetCommand<T>(commands.newChildId(), dashboardDocumentModel));
		commands.add(new SilentSaveDashboardCommand(commands.newChildId(), dashboardDocumentModel));
		commands.add(new KeepUnsavedDashboardCommand(commands.newChildId(), dashboardDocumentModel));
		commandsContainer.add(commands);
		add(commandsContainer);
		widgets = new RepeatingView("widgets");
		add(widgets);
		setOutputMarkupId(true);
		
		loadDashboard();
		dashboardSupport.initDashboardPanel(this);
	}
	
	public ODocument loadDashboard() {
		return loadDashboard(lookupDashboardDocument(domain, tab, getModel()));
	}
	
	/**
	 * Load this dashboard from specified dashboard document. If document is null: default dashboard will be built
	 * @param doc dashboard document
	 * @return document from which this document was built
	 */
	public ODocument loadDashboard(ODocument doc) {
		if(doc!=null)
		{
			dashboardDocumentModel.setObject(doc);
			List<ODocument> widgets = doc.field(OPROPERTY_WIDGETS);
			if(widgets!=null) {
				widgets.remove(null); //To avoid deleted widgets
				for (ODocument widgetDoc : widgets) {
					addWidget(createWidgetFromDocument(widgetDoc));
				}
			}
		}
		else
		{
			buildDashboard();
		}
		return doc;
	}
	
	protected ODocument lookupDashboardDocument(String domain, String tab, IModel<T> model) {
		return dashboardManager.getExistingDashboard(domain, tab, getModel());
	}
	
	protected void buildDashboard() {
		
		List<IWidgetType<T>> widgets = widgetTypesRegistry.lookupByDomainAndTab(domain, tab, getWidgetsFilter());
		for(int i=0;i<widgets.size();i++)
		{
			IWidgetType<T> type = widgets.get(i);
			if(type.isAutoEnable()) {
				AbstractWidget<T> widget = type.instanciate(newWidgetId(), getModel(), dashboardManager.createWidgetDocument(type));
				addWidget(widget);
			}
		}
	}
	
	public IWidgetFilter<T> getWidgetsFilter() {
		return widgetsFilter;
	}
	
	public DashboardPanel<T> setWidgetsFilter(IWidgetFilter<T> widgetsFilter) {
		this.widgetsFilter = widgetsFilter;
		return this;
	}
	
		
	@SuppressWarnings("unchecked")
	private AbstractWidget<T> createWidgetFromDocument(ODocument widgetDoc) {
		IWidgetType<T> type = null;
		if(widgetDoc!=null) type = (IWidgetType<T>)widgetTypesRegistry.lookupByTypeId((String)widgetDoc.field(OPROPERTY_TYPE_ID));
		return  type!=null ? type.instanciate(newWidgetId(), getModel(), widgetDoc)
						   : new NotFoundWidget<T>(newWidgetId(), getModel(), new ODocumentModel(widgetDoc));
	}
	
	/**
	 * Store dashboard configuration in a document
	 * @return document - can't be null
	 */
	public ODocument storeDashboard() {
		ODocument doc = dashboardDocumentModel.getObject();
		if(doc==null) {
			doc = new ODocument(OCLASS_DASHBOARD);
			doc.field(OPROPERTY_DOMAIN, domain);
			doc.field(OPROPERTY_TAB, tab);
			doc.save();
			dashboardDocumentModel.setObject(doc);
		}
		
		List<AbstractWidget<T>> components = getWidgets();
		List<ODocument> widgets = new ArrayList<ODocument>();
		for (AbstractWidget<T> widget : components) {
			widget.saveSettings();
			ODocument widgetDoc = widget.getWidgetDocument();
			widgetDoc.save();
			widgets.add(widgetDoc);
		}
		doc.field(OPROPERTY_WIDGETS, widgets);
		
		doc.save();
		return doc;
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
		return addWidget(description, dashboardManager.createWidgetDocument(description));
	}
	
	public AbstractWidget<T> addWidget(IWidgetType<T> description, ODocument widgetDoc)
	{
		return addWidget(description.instanciate(newWidgetId(), getModel(), widgetDoc));
	}
	
	public AbstractWidget<T> addWidget(String widgetId)
	{
		return addWidget((IWidgetType<T>)widgetTypesRegistry.lookupByTypeId(widgetId));
	}
	
	public AbstractWidget<T> addWidget(String widgetId, ODocument widgetDoc)
	{
		return addWidget((IWidgetType<T>)widgetTypesRegistry.lookupByTypeId(widgetId), widgetDoc);
	}
	
	public DashboardPanel<T> deleteWidget(AbstractWidget<T> widget)
	{
		widgets.remove(widget);
		return this;
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "dashboard", " ");
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(DisplayMode.EDIT.equals(dashboardModeModel.getObject())) {
			commands.setVisibilityAllowed(true);
			configureCommand.setVisibilityAllowed(false);
		} else {
			commands.setVisibilityAllowed(false);
			configureCommand.setVisibilityAllowed(true);
		}
	}
	
	public String getDomain() {
		return domain;
	}

	public String getTab() {
		return tab;
	}

	public RepeatingView getWidgetsContainer() {
		return widgets;
	}
	
	public IDashboardSupport getDashboardSupport() {
		return dashboardSupport;
	}
	
	@Override
	protected void detachModel() {
		super.detachModel();
		dashboardDocumentModel.detach();
		dashboardModeModel.detach();
	}


	@Override
	public IModel<DisplayMode> getModeModel() {
		return dashboardModeModel;
	}


	@Override
	public DisplayMode getModeObject() {
		return dashboardModeModel.getObject();
	}
	
	public IModel<ODocument> getDashboardDocumentModel() {
		return dashboardDocumentModel;
	}
	
	public ODocument getDashboardDocument() {
		return dashboardDocumentModel.getObject();
	}

}
