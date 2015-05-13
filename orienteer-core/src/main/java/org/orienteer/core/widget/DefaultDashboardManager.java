package org.orienteer.core.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.util.lang.Objects;
import org.orienteer.core.OrienteerWebSession;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
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
			for(IWidgetType<?, ?> widgetDescriptor : widgetRegistry.listWidgetTypes())
			{
				if(domain.equals(widgetDescriptor.getDefaultDomain())) tabs.add(widgetDescriptor.getDefaultTab());
			}
		}
		return new ArrayList<String>(tabs);
	}

	@Override
	public ODashboardDescriptor getExistingDashboard(String domain, String tab) {
		ODatabaseDocument db = getDatabase();
		List<ODocument>  dashboards = db.query(new OSQLSynchQuery<ODocument>("select from "+ODashboardDescriptor.OCLASS_DASHBOARD+
											" where "+ODashboardDescriptor.OPROPERTY_DOMAIN+" = ?"+
											" and "+ODashboardDescriptor.OPROPERTY_TAB+" = ?"), domain, tab);
		//TODO: Add analysis of dashboards
		if(dashboards==null || dashboards.isEmpty()) return null;
		else return new ODashboardDescriptor(dashboards.get(0));
	}

	@Override
	public ODashboardDescriptor getDashboard(String domain, String tab) {
		ODashboardDescriptor descriptor = getExistingDashboard(domain, tab);
		if(descriptor==null)
		{
			descriptor = new ODashboardDescriptor();
			descriptor.setDomain(domain);
			descriptor.setTab(tab);
			//TODO: add links for widget types
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
	}
	
	private ODatabaseDocument getDatabase()
	{
		return OrienteerWebSession.get().getDatabase();
	}
	
}
