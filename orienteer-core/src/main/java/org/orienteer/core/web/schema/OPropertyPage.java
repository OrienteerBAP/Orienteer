package org.orienteer.core.web.schema;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.SchemaPageHeader;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.web.AbstractWidgetDisplayModeAwarePage;
import org.orienteer.core.web.AbstractWidgetPage;

import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * Page to show {@link OProperty} specific parameters
 */
@MountPath("/property/${className}/${propertyName}")
@RequiredOrientResource(value=OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
public class OPropertyPage extends AbstractWidgetDisplayModeAwarePage<OProperty> {

	public OPropertyPage() {
		super();
	}

	public OPropertyPage(IModel<OProperty> model) {
		super(model);
	}

	public OPropertyPage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	protected IModel<OProperty> resolveByPageParameters(
			PageParameters pageParameters) {
		String className = pageParameters.get("className").toOptionalString();
		String propertyName = pageParameters.get("propertyName").toOptionalString();
		return Strings.isEmpty(className) || Strings.isEmpty(propertyName)?null:new OPropertyModel(className, propertyName) ;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}
	
	@Override
	protected Component newPageHeaderComponent(String componentId) {
		PropertyModel<OClass> oClassModel = new PropertyModel<OClass>(getModel(), "ownerClass");
		SchemaPageHeader pageHeader = new SchemaPageHeader(componentId, oClassModel);
		pageHeader.addChild(new OClassViewPanel(pageHeader.newChildId(), oClassModel));
		pageHeader.addChild(new Label(pageHeader.newChildId(), getTitleModel()));
		return pageHeader;
	}

	@Override
	public String getDomain() {
		return "property";
	}
	
	public static PageParameters preparePageParameters(OProperty oProperty, DisplayMode mode) {
		PageParameters ret = new PageParameters();
		ret.add("className", oProperty.getOwnerClass().getName());
		ret.add("propertyName", oProperty.getName());
		if(!DisplayMode.VIEW.equals(mode)) ret.add("mode", mode.getName());
		return ret;
	}

}
