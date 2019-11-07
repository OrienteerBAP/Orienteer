package org.orienteer.core.widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ReorderableRepeatingView;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.support.IDashboardSupport;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.orienteer.core.module.OWidgetsModule.*;

/**
 * Dashboard is {@link Panel} to allow manipulation with a set of {@link AbstractWidget}s
 *
 * @param <T> the type of main data object
 */
public class DashboardPanel<T> extends GenericPanel<T> implements IDashboard<T> {
	private static final long serialVersionUID = 1L;

	@Inject
	protected IDashboardManager dashboardManager;
	
	@Inject
	protected IWidgetTypesRegistry widgetTypesRegistry;
	
	@Inject
	private IDashboardSupport dashboardSupport;
	
	private String domain;
	
	private String tab;
	
	private ReorderableRepeatingView widgets;
	
	//private AbstractDefaultAjaxBehavior ajaxBehavior;
	
	private IModel<DisplayMode> dashboardModeModel = DisplayMode.VIEW.asModel();
	
	private IModel<ODocument> dashboardDocumentModel = new ODocumentModel();
	
	private IWidgetFilter<T> widgetsFilter;
	
	public DashboardPanel(String id, String domain, String tab, IModel<T> model, IWidgetFilter<T> widgetsFilter) {
		super(id, model);
		this.domain = domain;
		this.tab = tab;
		this.widgetsFilter = widgetsFilter;
		widgets = new ReorderableRepeatingView("widgets");
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
		for (IWidgetType<T> type : widgets) {
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
	
	private AbstractWidget<T> createWidgetFromDocument(ODocument widgetDoc) {
		return createWidgetFromDocument(newWidgetId(), widgetDoc);
	}
		
	@SuppressWarnings("unchecked")
	private AbstractWidget<T> createWidgetFromDocument(String widgetId, ODocument widgetDoc) {
		IWidgetType<T> type = null;
		if(widgetDoc!=null) type = (IWidgetType<T>)widgetTypesRegistry.lookupByTypeId((String)widgetDoc.field(OPROPERTY_TYPE_ID));
		return  type!=null ? type.instanciate(widgetId, getModel(), widgetDoc)
						   : new NotFoundWidget<T>(widgetId, getModel(), new ODocumentModel(widgetDoc));
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
	
	@SuppressWarnings("unchecked")
	public List<AbstractWidget<T>> getWidgets()
	{
		final List<AbstractWidget<T>> ret = new ArrayList<AbstractWidget<T>>();
		Iterator<? extends Component> it = widgets.renderIterator();
		while(it.hasNext()) {
			Component comp = it.next();
			if(comp instanceof AbstractWidget) ret.add((AbstractWidget<T>) comp);
		}
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
	
	@SuppressWarnings("unchecked")
	public AbstractWidget<T> addWidget(String widgetId)
	{
		return addWidget((IWidgetType<T>)widgetTypesRegistry.lookupByTypeId(widgetId));
	}
	
	@SuppressWarnings("unchecked")
	public AbstractWidget<T> addWidget(String widgetId, ODocument widgetDoc)
	{
		return addWidget((IWidgetType<T>)widgetTypesRegistry.lookupByTypeId(widgetId), widgetDoc);
	}
	
	public DashboardPanel<T> deleteWidget(AbstractWidget<T> widget)
	{
		widgets.remove(widget);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public AbstractWidget<T> replaceWidget(AbstractWidget<T> oldWidget) {
		IWidgetType<T> type = (IWidgetType<T>)widgetTypesRegistry
								.lookupByWidgetClass((Class<? extends AbstractWidget<?>>)oldWidget.getClass());
		AbstractWidget<T> widget = type.instanciate(oldWidget.getId(), 
										(IModel<T>)oldWidget.getModel(), oldWidget.getWidgetDocument());
		widgets.replace(widget);
		return widget;
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "dashboard", " ");
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		findParent(IDashboardContainer.class).setCurrentDashboard(this);
	}
	
	public String getDomain() {
		return domain;
	}

	public String getTab() {
		return tab;
	}

	public ReorderableRepeatingView getWidgetsContainer() {
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

	@Override
	public DashboardPanel<T> getSelfComponent() {
		return this;
	}
}
