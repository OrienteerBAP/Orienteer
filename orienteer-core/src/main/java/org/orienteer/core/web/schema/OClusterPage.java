package org.orienteer.core.web.schema;

import com.orientechnologies.orient.core.storage.OCluster;

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
import ru.ydn.wicket.wicketorientdb.model.OClusterModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import javax.servlet.http.HttpServletResponse;

/**
 * Page to show {@link OCluster}
 */
@MountPath("/cluster/${clusterName}/#{mode}")
@RequiredOrientResource(value=OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
public class OClusterPage extends AbstractWidgetDisplayModeAwarePage<OCluster> {

    public OClusterPage(PageParameters parameters) {
        super(parameters);
        DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
        if(mode!=null) setModeObject(mode);
    }

    @Override
    public String getDomain() {
        return "cluster";
    }

    @Override
    public void initialize() {
        super.initialize();
        selectTab("configuration");
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected IModel<OCluster> resolveByPageParameters(
            PageParameters pageParameters) {
        String name = pageParameters.get("clusterName").toOptionalString();
        return Strings.isEmpty(name)? null:new OClusterModel(name);
    }

    @Override
    public IModel<String> getTitleModel() {
        return new PropertyModel<String>(getModel(), "name");
    }
    
    @Override
	protected Component newPageHeaderComponent(String componentId) {
		SchemaPageHeader pageHeader = new SchemaPageHeader(componentId);
		pageHeader.addChild(new Label(pageHeader.newChildId(), getTitleModel()));
		return pageHeader;
	}
}
