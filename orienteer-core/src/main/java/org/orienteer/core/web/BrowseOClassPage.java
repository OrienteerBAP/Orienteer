package org.orienteer.core.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.web.schema.OClassPage;
import org.orienteer.core.widget.ByOClassWidgetFilter;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IWidgetFilter;
import org.orienteer.core.widget.IWidgetType;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Page to browse all documents for a specific {@link OClass}
 */
@MountPath("/browse/${className}")
public class BrowseOClassPage extends AbstractWidgetPage<OClass> implements ISecuredComponent {

	public BrowseOClassPage(String className)
	{
		this(new OClassModel(className));
	}
	
	public BrowseOClassPage(IModel<OClass> model)
	{
		super(model);
	}

	public BrowseOClassPage(PageParameters parameters)
	{
		super(parameters);
	}

	@Override
	protected IModel<OClass> resolveByPageParameters(
			PageParameters pageParameters) {
		return new OClassModel(pageParameters.get("className").toOptionalString());
	}
	
	@Override
	public void initialize() {
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		setWidgetsFilter(new ByOClassWidgetFilter<OClass>() {

			@Override
			public OClass getOClass() {
				return BrowseOClassPage.this.getModelObject();
			}
		});
		super.initialize();
	}
	
	@Override
	protected DashboardPanel<OClass> newDashboard(String id, String domain,
			String tab, IModel<OClass> model, IWidgetFilter<OClass> filter) {
		
		return new DashboardPanel<OClass>(id, domain, tab, model, filter) {
			@Override
			protected ODocument lookupDashboardDocument(String domain,
					String tab, IModel<OClass> model) {
				return dashboardManager.getExistingDashboard(domain, tab, model, model.getObject());
			}
			
			@Override
			public ODocument storeDashboard() {
				ODocument doc = super.storeDashboard();
				doc.field(OWidgetsModule.OPROPERTY_CLASS, getModelObject().getName());
				doc.save();
				return doc;
			}
		};
	}
	
	@Override
	public String getDomain() {
		return "browse";
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return new StringResourceModel("class.browse.title", new OClassNamingModel(getModel()));
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		return OSecurityHelper.requireOClass(getModelObject(), OrientPermission.READ);
	}
	
	public static PageParameters preparePageParameters(OClass oClass, DisplayMode mode) {
		return OClassPage.preparePageParameters(oClass, mode);
	}

}
