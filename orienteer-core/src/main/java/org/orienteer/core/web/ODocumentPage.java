package org.orienteer.core.web;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.ODocumentPageHeader;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.widget.document.ExtendedVisualizerWidget;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.module.RoutingModule;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.ByOClassWidgetFilter;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IWidgetFilter;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Widgets based page for {@link ODocument}s display
 * Alternative mount path uses for create custom routes for documents. See {@link RoutingModule}
 */
@MountPath(value = "/doc/#{rid}/#{mode}", alt = {"/address/#{address}/#{mode}"})
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
		String address = parameters.get("address").toOptionalString();

		if (rid != null) {
			try {
				return new ODocumentModel(new ORecordId(rid));
			} catch (IllegalArgumentException e) {
				//NOP Support of case with wrong rid
			}
		} else if (address != null) {
		    return resolveDocumentByAddress(address);
		}

		return new ODocumentModel(null);
	}

	/**
	 * Try to resolve current document by given custom address
	 * @param address custom address
	 * @return model with current document or model with null
	 * @throws RestartResponseException if current address contains list of documents. Redirects to {@link ODocumentsPage}
	 */
	protected IModel<ODocument> resolveDocumentByAddress(String address) throws RestartResponseException {
        RoutingModule.ORouterNode routerNode = DBClosure.sudo(db -> {
            RoutingModule routing = (RoutingModule) OrienteerWebApplication.lookupApplication().getModuleByName(RoutingModule.NAME);
            return routing.getRouterNode(db, address.startsWith("/") ? address : "/" + address);
        });

		List<ODocument> documents = routerNode.getDocuments();
		if (documents.size() == 1) {
            return new ODocumentModel(routerNode.getDocuments().get(0));
        } else if (!documents.isEmpty()) {
			silentRedirectToDocumentsPage(documents);
		}
		return new ODocumentModel(null);
    }

    private void silentRedirectToDocumentsPage(List<ODocument> documents) throws RestartResponseException {
		String docsParams = documents.stream().map(ODocument::getIdentity)
				.map(OIdentifiable::getIdentity)
				.map(ORID::toString)
				.map(rid -> rid.substring(1))
				.collect(Collectors.joining(","));


		PageParameters pageParameters = new PageParameters();
		pageParameters.add("docs", docsParams);
		PageProvider provider = new PageProvider(ODocumentsPage.class, pageParameters);
		throw new RestartResponseException(provider, RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT);
	}

	@Override
	public String getDomain() {
		return "document";
	}
	
	@Override
	public void initialize() {
		if (getModelObject() == null)
			throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);

		setWidgetsFilter(new ByOClassWidgetFilter<ODocument>() {

			@Override
			public OClass getOClass() {
				ODocument doc = ODocumentPage.this.getModelObject();
				return doc != null ? doc.getSchemaClass() : null;
			}
		});

		super.initialize();
	}

	@Override
	protected boolean switchToDefaultTab() {
		if(super.switchToDefaultTab()) return true;
		else {
			ODocument doc = getModelObject();
			if(doc!=null) {
				String defaultTab = CustomAttribute.TAB.<String>getValue(doc.getSchemaClass(),IOClassIntrospector.DEFAULT_TAB);
				return selectTab(defaultTab);
			}
			else return false;
		}
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
				return dashboardManager.getExistingDashboard(domain, tab, model, model.getObject().getSchemaClass());
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
