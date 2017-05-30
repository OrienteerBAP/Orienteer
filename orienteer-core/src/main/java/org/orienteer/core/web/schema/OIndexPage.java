package org.orienteer.core.web.schema;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.SchemaPageHeader;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.web.AbstractWidgetDisplayModeAwarePage;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexModel;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.index.OIndex;

/**
 * Page to display {@link OIndex} specific parameters
 */
@MountPath("/index/${indexName}")
@RequiredOrientResource(value=OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
public class OIndexPage extends
		AbstractWidgetDisplayModeAwarePage<OIndex<?>> {

	public OIndexPage(IModel<OIndex<?>> model) {
		super(model);
	}

	public OIndexPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) setModeObject(mode);
	}

	@Override
	protected IModel<OIndex<?>> resolveByPageParameters(
			PageParameters pageParameters) {
		String indexName = pageParameters.get("indexName").toOptionalString();
		return Strings.isEmpty(indexName)?null:new OIndexModel(indexName);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return new StringResourceModel("index.title.format", getModel());
	}
	
	@Override
	protected Component newPageHeaderComponent(String componentId) {
		OClassModel oClassModel = new OClassModel(new PropertyModel<String>(getModel(), OIndexPrototyper.DEF_CLASS_NAME));
		SchemaPageHeader pageHeader = new SchemaPageHeader(componentId, oClassModel);
		pageHeader.addChild(new OClassViewPanel(pageHeader.newChildId(), oClassModel));
		pageHeader.addChild(new Label(pageHeader.newChildId(), new PropertyModel<String>(getModel(), "name")));
		return pageHeader;
	}
	
	@Override
	public String getDomain() {
		return "index";
	}

}
