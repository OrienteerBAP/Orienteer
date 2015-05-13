package org.orienteer.core.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Descriptor for a dashboard. Implemented as {@link ODocumentWrapper}
 */
public class ODashboardDescriptor extends ODocumentWrapper{

	public static final String OCLASS_DASHBOARD = "ODashboard";
	public static final String OPROPERTY_DOMAIN = "domain";
	public static final String OPROPERTY_TAB = "tab";
	public static final String OPROPERTY_LINKED_IDENTITY = "linked";
	public static final String OPROPERTY_WIDGETS = "widgets";
	
	public ODashboardDescriptor(ODocument iDocument) {
		super(iDocument);
	}

	public ODashboardDescriptor(ORID iRID) {
		super(iRID);
	}

	public ODashboardDescriptor() {
		super(OCLASS_DASHBOARD);
	}
	
	public String getDomain() {
		return document.field(OPROPERTY_DOMAIN);
	}

	public ODashboardDescriptor setDomain(String domain) {
		document.field(OPROPERTY_DOMAIN, domain);
		return this;
	}
	
	public String getTab() {
		return document.field(OPROPERTY_TAB);
	}
	
	public ODashboardDescriptor setTab(String tab) {
		document.field(OPROPERTY_TAB, tab);
		return this;
	}
	
	public ODocument getLinked() {
		return document.field(OPROPERTY_LINKED_IDENTITY);
	}
	
	public ODashboardDescriptor setLinked(ODocument identity) {
		document.field(OPROPERTY_LINKED_IDENTITY, identity);
		return this;
	}
	
	public Set<OWidgetDescriptor> getWidgets() {
		List<ODocument> docs = document.field(OPROPERTY_WIDGETS);
		Set<OWidgetDescriptor> ret = new HashSet<OWidgetDescriptor>();
		if(docs!=null)
		{
			for (ODocument oDocument : docs) {
				ret.add(new OWidgetDescriptor(oDocument));
			}
		}
		return ret;
	}
	
	public ODashboardDescriptor setWidgets(Set<OWidgetDescriptor> widgets) {
		List<ODocument> docs = document.field(OPROPERTY_WIDGETS);
		if(docs==null) docs = new ArrayList<ODocument>(widgets.size());
		List<ODocument> widgetsToSet = new ArrayList<ODocument>();
		for(OWidgetDescriptor widget : widgets)
		{
			widgetsToSet.add(widget.getDocument());
		}
		docs.retainAll(widgetsToSet);
		widgetsToSet.removeAll(docs);
		docs.addAll(widgetsToSet);
		document.field(OPROPERTY_WIDGETS, docs);
		return this;
	}
	
	public ODashboardDescriptor createCopy() {
		ODashboardDescriptor ret = new ODashboardDescriptor();
		ret.setDomain(getDomain());
		ret.setTab(getTab());
		//TODO: Finish creating of a copy
		return ret;
	}

}
