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
import org.orienteer.core.web.AbstractWidgetDisplayModeAwarePage;
import org.orienteer.core.web.BrowseOClassPage;
import org.orienteer.core.widget.ByOClassWidgetFilter;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Page to show {@link OClass} specific things: class parameters, properties, indexes and etc.
 */
@MountPath("/class/${className}/#{mode}")
@RequiredOrientResource(value=OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
public class OClassPage extends AbstractWidgetDisplayModeAwarePage<OClass> {

	public OClassPage(IModel<OClass> model) {
		super(model);
	}

	public OClassPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) setModeObject(mode);
	}
	
	@Override
	public void initialize() {
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		setWidgetsFilter(new ByOClassWidgetFilter<OClass>() {

			@Override
			public OClass getOClass() {
				return OClassPage.this.getModelObject();
			}
		});
		super.initialize();
		selectTab("configuration");
	}

	@Override
	protected IModel<OClass> resolveByPageParameters(
			PageParameters pageParameters) {
		String className = pageParameters.get("className").toOptionalString();
		return Strings.isEmpty(className)?null:new OClassModel(className);
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
		SchemaPageHeader pageHeader = new SchemaPageHeader(componentId, getModel());
		pageHeader.addChild(new Label(pageHeader.newChildId(), getTitleModel()));
		return pageHeader;
	}
	
	@Override
	public String getDomain() {
		return "class";
	}
	
	public static PageParameters preparePageParameters(OClass oClass, DisplayMode mode) {
		PageParameters ret = new PageParameters();
		ret.add("className", oClass.getName());
		if(!DisplayMode.VIEW.equals(mode)) ret.add("mode", mode.getName());
		return ret;
	}

}
