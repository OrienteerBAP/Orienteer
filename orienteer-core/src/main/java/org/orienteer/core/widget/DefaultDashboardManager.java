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
	
	public ODocument getExistingDashboard(String domain, String tab) {
		ODatabaseDocument db = getDatabase();
		List<ODocument>  dashboards = db.query(new OSQLSynchQuery<ODocument>("select from "+OCLASS_DASHBOARD+
											" where "+OPROPERTY_DOMAIN+" = ?"+
											" and "+OPROPERTY_TAB+" = ?"), domain, tab);
		//TODO: Add analysis of dashboards
		if(dashboards==null || dashboards.isEmpty()) return null;
		else return dashboards.get(0);
	}
	

	private ODatabaseDocument getDatabase()
	{
		return OrienteerWebSession.get().getDatabase();
	}
	
}
