package org.orienteer.core.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
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
	public List<String> listTabs(String domain, IModel<?> dataModel) {
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
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel) {
		ODatabaseDocument db = getDatabase();
		List<ODocument>  dashboards = db.query(new OSQLSynchQuery<ODocument>("select from "+OCLASS_DASHBOARD+
											" where "+OPROPERTY_DOMAIN+" = ?"+
											" and "+OPROPERTY_TAB+" = ?"), domain, tab);
		if(dashboards==null || dashboards.isEmpty()) return null;
		else return dashboards.get(0);
	}
	
	@Override
	public ODocument getExistingDashboard(String domain, String tab, IModel<?> dataModel, Map<String, Object> criteriesMap) {
		StringBuilder sql = new StringBuilder();
		sql.append("select from ").append(OCLASS_DASHBOARD).append(" where ")
		   .append(OPROPERTY_DOMAIN).append(" = ? and ")
		   .append(OPROPERTY_TAB).append(" = ?");
		List<Object> args = new ArrayList<Object>();
		args.add(domain);
		args.add(tab);
		for(Map.Entry<String, Object> entry: criteriesMap.entrySet()) {
			sql.append(" and ").append(entry.getKey()).append(" = ?");
			args.add(entry.getValue());
		}
		ODatabaseDocument db = getDatabase();
		List<ODocument>  dashboards = db.query(new OSQLSynchQuery<ODocument>(sql.toString()), args.toArray());
		if(dashboards==null || dashboards.isEmpty()) return null;
		else return dashboards.get(0);
	}
	
	@Override
	public ODocument createWidgetDocument(IWidgetType<?> widgetType) {
		String oClassName = widgetType.getOClassName();
		if(oClassName==null) oClassName = OCLASS_WIDGET;
		OClass oClass = getDatabase().getMetadata().getSchema().getClass(oClassName);
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
	
	private ODatabaseDocument getDatabase()
	{
		return OrienteerWebSession.get().getDatabase();
	}
	
}
