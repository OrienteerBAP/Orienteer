package org.orienteer.core.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.core.OrienteerWebSession;

import static org.orienteer.core.module.OWidgetsModule.*;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Default implementation of {@link IDashboardManager}
 */
@Singleton
public class DefaultDashboardManager implements IDashboardManager{
	
	private IWidgetTypesRegistry widgetRegistry;
	@Inject
	public DefaultDashboardManager(IWidgetTypesRegistry widgetRegistry) {
		this.widgetRegistry = widgetRegistry;
	}

	@Override
	public List<String> listTabs(String domain) {
		Set<String> tabs = new HashSet<String>();
		if(domain!=null)
		{
			for(IWidgetType<?> widgetDescriptor : widgetRegistry.listWidgetTypes())
			{
				if(domain.equals(widgetDescriptor.getDefaultDomain())) tabs.add(widgetDescriptor.getDefaultTab());
			}
		}
		//TBD Load from DB(cache)
		return new ArrayList<String>(tabs);
	}
	
	
	@Override
	public <T> void initializeDashboard(DashboardPanel<T> dashboard, String domain, String tab) {
		ODocument doc = getExistingDashboard(domain, tab);
		if(doc!=null)
		{
			List<ODocument> widgets = doc.field(OPROPERTY_WIDGETS);
			for (ODocument widgetDoc : widgets) {
				IWidgetType<T> type = (IWidgetType<T>)widgetRegistry.lookupByTypeId((String) widgetDoc.field(OPROPERTY_TYPE_ID));
				AbstractWidget<T> widget = type.instanciate(dashboard.newWidgetId(), dashboard.getModel());
				widget.loadSettings(widgetDoc);
				dashboard.addWidget(widget);
			}
		}
		else
		{
			List<IWidgetType<T>> widgets = widgetRegistry.lookupByDefaultDomainAndTab(domain, tab);
			for(int i=0;i<widgets.size();i++)
			{
				IWidgetType<T> type = widgets.get(i);
				AbstractWidget<T> widget = type.instanciate(dashboard.newWidgetId(), dashboard.getModel());
				widget.setCol(1);
				widget.setRow(i+1);
				widget.setSizeX(2);
				widget.setSizeY(1);
				dashboard.addWidget(widget);
			}
		}
	}

	private ODocument getExistingDashboard(String domain, String tab) {
		ODatabaseDocument db = getDatabase();
		List<ODocument>  dashboards = db.query(new OSQLSynchQuery<ODocument>("select from "+OCLASS_DASHBOARD+
											" where "+OPROPERTY_DOMAIN+" = ?"+
											" and "+OPROPERTY_TAB+" = ?"), domain, tab);
		//TODO: Add analysis of dashboards
		if(dashboards==null || dashboards.isEmpty()) return null;
		else return dashboards.get(0);
	}
	
	@Override
	public <T> void storeDashboard(DashboardPanel<T> dashboard, String domain,
			String tab) {
		ODatabaseDocument db = getDatabase();
		ODocument doc = getExistingDashboard(domain, tab);
		if(doc==null) {
			doc = new ODocument(OCLASS_DASHBOARD);
			doc.field(OPROPERTY_DOMAIN, domain);
			doc.field(OPROPERTY_TAB, tab);
			doc.save();
		}
		List<ODocument> widgets = doc.field(OPROPERTY_WIDGETS);
		if(widgets==null) {
			widgets = new ArrayList<ODocument>();
			doc.field(OPROPERTY_WIDGETS, widgets);
		}
		
		List<ODocument> handledWidgets = new ArrayList<ODocument>();
		
		List<AbstractWidget<T>> components = dashboard.getWidgets();
		for (AbstractWidget<T> widget : components) {
			IWidgetType<T> type = widgetRegistry.lookupByWidgetClass((Class<? extends AbstractWidget<T>>)widget.getClass());
			String typeId = type.getId();
			ODocument widgetDoc=null;
			for (ODocument candidate : widgets) {
				if(handledWidgets.contains(candidate)) continue;
				if(typeId.equals(candidate.field(OPROPERTY_TYPE_ID)))
				{
					handledWidgets.add(candidate);
					widgetDoc = candidate;
					break;
				}
			}
			if(widgetDoc==null) {
				String oClassName = type.getOClassName();
				if(oClassName==null) oClassName = OCLASS_WIDGET;
				OClass oClass = db.getMetadata().getSchema().getClass(oClassName);
				if(oClass==null || !oClass.isSubClassOf(OCLASS_WIDGET)) throw new WicketRuntimeException("Wrong OClass specified for widget settings: "+oClassName);
				widgetDoc = new ODocument(oClass);
				widgetDoc.field(OPROPERTY_TYPE_ID, typeId);
				widgets.add(widgetDoc);
				widget.saveSettings(widgetDoc);
			}
			else
			{
				widget.saveSettings(widgetDoc);
				widgetDoc.save();
			}
		}
		
		List<ODocument> widgetsToRemove = new ArrayList<ODocument>(widgets);
		widgetsToRemove.removeAll(handledWidgets);
		for (ODocument toRemove : widgetsToRemove) {
			toRemove.delete();
		}
		
		doc.save();
		
		
		/*Map<String, List<ODocument>> widgetsByType = new HashMap<String, List<ODocument>>();
		for (ODocument widget : widgets) {
			String typeId = widget.field(OPROPERTY_TYPE_ID);
			List<ODocument> list = widgetsByType.get(typeId);
			if(list==null)
			{
				list = new ArrayList<ODocument>();
				widgetsByType.put(typeId, list);
			}
			list.add(widget);
		}
		
		Map<String, List<AbstractWidget<T>>> widgetsComponentsByType = new HashMap<String, List<AbstractWidget<T>>>();
		for (AbstractWidget<T> widget : components) {
			String typeId = widgetRegistry.lookupByWidgetClass((Class<? extends AbstractWidget<T>>)widget.getClass()).getId();
			List<AbstractWidget<T>> list = widgetsComponentsByType.get(typeId);
			if(list==null)
			{
				list = new ArrayList<AbstractWidget<T>>();
				widgetsComponentsByType.put(typeId, list);
			}
			list.add(widget);
		}
		
		for(String typeId : widgetsComponentsByType.keySet()) {
			List<AbstractWidget<T>> compList = widgetsComponentsByType.get(typeId);
			List<ODocument> widgetList = widgetsByType.get(typeId);
		}*/
	}

	/*@Override
	public ODashboardDescriptor getDashboard(String domain, String tab) {
		ODashboardDescriptor descriptor = getExistingDashboard(domain, tab);
		if(descriptor==null)
		{
			descriptor = new ODashboardDescriptor();
			descriptor.setDomain(domain);
			descriptor.setTab(tab);
			List<IWidgetType<Object, IWidgetSettings>> widgets = widgetRegistry.lookupByDefaultDomainAndTab(domain, tab);
			for(int i=0;i<widgets.size();i++)
			{
				IWidgetType<Object, IWidgetSettings> type = widgets.get(i);
				OWidgetDescriptor widgetDescriptor = new OWidgetDescriptor();
				widgetDescriptor.setTypeId(type.getId());
				widgetDescriptor.setCol(1);
				widgetDescriptor.setRow(i+1);
				widgetDescriptor.setSizeX(2);
				widgetDescriptor.setSizeY(1);
				descriptor.addWidget(widgetDescriptor);
			}
		}
		return descriptor;
	}

	@Override
	public IDashboardManager saveDashboard(ODashboardDescriptor dashboard) {
		dashboard.save();
		return this;
	}

	@Override
	public IDashboardManager saveDashboard(ODashboardDescriptor dashboard,
			ODocument identity) {
		if(!Objects.equal(identity, dashboard.getLinked())) {
			dashboard = dashboard.createCopy();
			dashboard.setLinked(identity);
		}
		saveDashboard(dashboard);
		return this;
	}*/
	

	private ODatabaseDocument getDatabase()
	{
		return OrienteerWebSession.get().getDatabase();
	}
	
}
