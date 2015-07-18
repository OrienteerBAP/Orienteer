package org.orienteer.core.web;

import static org.orienteer.core.module.OWidgetsModule.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.ODocumentPageHeader;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.widget.document.ExtendedVisualizerWidget;
import org.orienteer.core.component.widget.document.ODocumentPropertiesWidget;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IWidgetFilter;
import org.orienteer.core.widget.IWidgetType;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Widgets based page for {@link ODocument}s display
 */
@MountPath("/doc/#{rid}/#{mode}")
public class ODocumentPage extends AbstractWidgetDisplayModeAwarePage<ODocument> {
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	public ODocumentPage() {
		super();
	}
	
	public ODocumentPage(ODocument doc)
	{
		this(new ODocumentModel(doc));
	}

	public ODocumentPage(IModel<ODocument> model) {
		super(model);
	}

	public ODocumentPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) setModeObject(mode);
	}
	
	@Override
	protected IModel<ODocument> resolveByPageParameters(PageParameters parameters) {
		String rid = parameters.get("rid").toOptionalString();
		if(rid!=null)
		{
			try
			{
				return new ODocumentModel(new ORecordId(rid));
			} catch (IllegalArgumentException e)
			{
				//NOP Support of case with wrong rid
			}
		}
		return new ODocumentModel((ODocument)null);
	}

	@Override
	public String getDomain() {
		return "document";
	}
	
	@Override
	public void initialize() {
		setWidgetsFilter(new IWidgetFilter<ODocument>() {
			
			@Override
			public boolean apply(IWidgetType<ODocument> input) {
				if(Strings.isEmpty(input.getSelector())) return true;
				else {
					ODocument doc = ODocumentPage.this.getModelObject();
					if(doc!=null) {
						OClass oClass = doc.getSchemaClass();
						return oClass!=null?oClass.isSubClassOf(input.getSelector()):false;
					} else {
						return false;
					}
				}
			}
		});
		super.initialize();
	}
	
	@Override
	public List<String> getTabs() {
		List<String> tabs = oClassIntrospector.listTabs(getModelObject().getSchemaClass());
		List<String> widgetsTabs = super.getTabs();
		if(widgetsTabs!=null) {
			for(String widgetTab: widgetsTabs) {
				if(!tabs.contains(widgetTab)) tabs.add(widgetTab);
			}
		}
		return tabs;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		ODocument doc = getModelObject();
		if(doc==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		//Support of case when metadata was changed in parallel
		else if(Strings.isEmpty(doc.getClassName()) && doc.getIdentity().isValid())
		{
			getDatabase().reload();
			if(Strings.isEmpty(doc.getClassName()))  throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return new ODocumentNameModel(getModel());
	}

	@Override
	protected Component newPageHeaderComponent(String componentId) {
		return new ODocumentPageHeader(componentId, getModel());
	}
	
	@Override
	protected DashboardPanel<ODocument> newDashboard(String id, String domain,
			String tab, IModel<ODocument> model, IWidgetFilter<ODocument> filter) {
		return new DashboardPanel<ODocument>(id, domain, tab, model, filter) {
			
			@Override
			protected ODocument lookupDashboardDocument(String domain,
					String tab, IModel<ODocument> model) {
				Map<String, Object> criteriesMap = new HashMap<String, Object>();
				criteriesMap.put(OWidgetsModule.OPROPERTY_CLASS, model.getObject().getSchemaClass().getName());
				return dashboardManager.getExistingDashboard(domain, tab, model, criteriesMap);
			}
			
			@Override
			protected void buildDashboard() {
				super.buildDashboard();
				//addWidget(ODocumentPropertiesWidget.WIDGET_TYPE_ID); //It will be added automatically!
				
				List<? extends OProperty> properties = oClassIntrospector.listProperties(getModelObject().getSchemaClass(), getTab(), true);
				
				ODocument widgetDoc;
				for (OProperty oProperty : properties) {
					widgetDoc = dashboardManager.createWidgetDocument(ExtendedVisualizerWidget.class);
					widgetDoc.field("property", oProperty.getName());
					addWidget(ExtendedVisualizerWidget.WIDGET_TYPE_ID, widgetDoc);
				}
			}
			
			@Override
			public ODocument storeDashboard() {
				ODocument doc = super.storeDashboard();
				doc.field(OWidgetsModule.OPROPERTY_CLASS, getModelObject().getSchemaClass().getName());
				doc.save();
				return doc;
			}
		};
	}

}
