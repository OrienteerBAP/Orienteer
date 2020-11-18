package org.orienteer.core.widget;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebSession;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.orienteer.core.module.OWidgetsModule.*;

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
	public <T> List<String> listTabs(String domain, Predicate<IWidgetType<T>> filter, T data) {
		List<String> tabs = new ArrayList<String>();
		if(domain!=null)
		{
			for(IWidgetType<?> widgetDescriptor : widgetRegistry.lookupByDomain(domain, filter))
			{
				String tabToAdd = widgetDescriptor.getTab();
				//To preserve order from widget registry
				if(domain.equals(widgetDescriptor.getDomain()) && !Strings.isEmpty(tabToAdd) && !tabs.contains(tabToAdd)) 
						tabs.add(tabToAdd);
			}
		}
		return tabs;
	}
	
	@Override
	public List<String> listExistingTabs(String domain, IModel<?> dataModel) {
		ODatabaseDocument db = getDatabaseSession();
		try(OResultSet result = db.query("select tab from "+OCLASS_DASHBOARD+" where "+OPROPERTY_DOMAIN+" = ?", domain)) {
			return result.stream().map(r -> (String)r.getProperty(OPROPERTY_TAB)).collect(Collectors.toList());
		}
	}
	
	@Override
	public List<String> listExistingTabs(String domain, IModel<?> dataModel, OClass oClass) {
		if(oClass==null) return listExistingTabs(domain, dataModel);
		ODatabaseDocument db = getDatabaseSession();
		List<String> ret = new ArrayList<String>();
		List<String> oClassAndSuper = new ArrayList<>();
		oClassAndSuper.add(oClass.getName());
		oClassAndSuper.addAll(oClass.getSuperClassesNames());
		String sql = "select distinct("+OPROPERTY_TAB+") as "+OPROPERTY_TAB+" from "+OCLASS_DASHBOARD+
				" where "+OPROPERTY_DOMAIN+" = ?"+
				" and "+OPROPERTY_CLASS+" IN ?";
		try(OResultSet result = db.query(sql, domain, oClassAndSuper)) {
			return result.stream().map(r -> (String)r.getProperty(OPROPERTY_TAB)).collect(Collectors.toList());
		}
	}
	
	@Override
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel) {
		ODatabaseDocument db = getDatabaseSession();
		String sql = String.format("select from %s where %s = ? and %s = ?", OCLASS_DASHBOARD, OPROPERTY_DOMAIN, OPROPERTY_TAB);
		try(OResultSet result = db.query(sql, domain, tab)) {
				return result.elementStream()
								.findFirst()
								.map(e -> (ODocument) e)
								.orElse(null);
		}
	}
	
	@Override
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel, Map<String, Object> criteriesMap) {
		return getExistingDashboard(domain, tab, dataModel, null, criteriesMap);
	}
	
	@Override
	public ODocument getExistingDashboard(String domain, String tab,
			IModel<?> dataModel, OClass oClass) {
		return getExistingDashboard(domain, tab, dataModel, oClass, null);
	}

	@Override
	public ODocument getExistingDashboard(String domain, String tab,
			IModel<?> dataModel, OClass oClass, Map<String, Object> criteriesMap) {
		StringBuilder sql = new StringBuilder();
		sql.append("select from ").append(OCLASS_DASHBOARD).append(" where ")
		   .append(OPROPERTY_DOMAIN).append(" = ? and ")
		   .append(OPROPERTY_TAB).append(" = ?");
		List<Object> args = new ArrayList<Object>();
		args.add(domain);
		args.add(tab);
		if(criteriesMap!=null) {
			for(Map.Entry<String, Object> entry: criteriesMap.entrySet()) {
				sql.append(" and ").append(entry.getKey()).append(" = ?");
				args.add(entry.getValue());
			}
		}
		ODatabaseSession db = getDatabaseSession();
		List<ODocument>  dashboards;
		try(OResultSet result = db.query(sql.toString(), args.toArray())) {
			
			dashboards = result.elementStream()
									.map(e -> (ODocument) e)
									.collect(Collectors.toCollection(LinkedList::new));
		}

		if(dashboards.isEmpty()) {
			return null;
		} else if (oClass != null) {
			ODocument selected=null;
			int level = Integer.MAX_VALUE;
			OSchema schema = db.getMetadata().getSchema();
			for (ODocument candidate : dashboards) {
				String dashboardClass = candidate.field(OPROPERTY_CLASS);
				if(dashboardClass==null && selected==null) selected = candidate;
				else {
					OClass superClass = schema.getClass(dashboardClass);
					if(superClass!=null) {
						Integer thisLevel = isSuperClass(superClass, oClass);
						if(thisLevel!=null && thisLevel < level) {
							level = thisLevel;
							selected = candidate;
						}
					}
				}
			}
			return selected;
		}
		else return dashboards.get(0);
	}
	
	private Integer isSuperClass(OClass superOClass, OClass oClass) {
		if(superOClass.equals(oClass)) return 0;
		Integer ret = null;
		for(OClass subClass : superOClass.getSubclasses()) {
			Integer thisRet = isSuperClass(subClass, oClass);
			if(thisRet!=null) {
				if(ret==null || ret > thisRet) ret = thisRet+1;
			}
		}
		return ret;
	}

	@Override
	public ODocument createWidgetDocument(IWidgetType<?> widgetType) {
		String oClassName = widgetType.getOClassName();
		if(oClassName==null) oClassName = OCLASS_WIDGET;
		OClass oClass = getDatabaseSession().getMetadata().getSchema().getClass(oClassName);
		if(oClass==null || !oClass.isSubClassOf(OCLASS_WIDGET)) throw new WicketRuntimeException("Wrong OClass specified for widget settings: "+oClassName);
		ODocument widgetDoc = new ODocument(oClass);
		widgetDoc.field(OPROPERTY_TYPE_ID, widgetType.getId());
		return widgetDoc;
	}
	
	@Override
	public ODocument createWidgetDocument(
			Class<? extends AbstractWidget<?>> widgetClass) {
		return createWidgetDocument(widgetRegistry.lookupByWidgetClass(widgetClass));
	}
	
	private ODatabaseSession getDatabaseSession()
	{
		return OrienteerWebSession.get().getDatabaseSession();
	}
	
}
