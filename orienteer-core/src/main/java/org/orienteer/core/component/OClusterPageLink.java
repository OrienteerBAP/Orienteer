package org.orienteer.core.component;

import com.orientechnologies.orient.core.storage.OCluster;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.web.schema.OClusterPage;

/**
 * {@link BookmarkablePageLink} for {@link OCluster}
 */
public class OClusterPageLink extends BookmarkablePageLink<OCluster>
{
	private static final long serialVersionUID = 1L;
    private final IModel<DisplayMode> displayModeModel;

    public OClusterPageLink(String id, IModel<OCluster> model) {
        this(id, model, DisplayMode.VIEW);
    }

    public OClusterPageLink(String id, IModel<OCluster> model, DisplayMode mode)
    {
        this(id, model, OClusterPage.class, mode.asModel());
    }

    public <C extends Page> OClusterPageLink(String id, IModel<OCluster> model, Class<C> pageClass,
                                           IModel<DisplayMode> displayModeModel) {
        super(id, pageClass);
        setModel(model);
        this.displayModeModel = displayModeModel;
    }

    public OClusterPageLink setPropertyNameAsBody(boolean propertyName)
    {
        setBody(propertyName?new PropertyModel<String>(getModel(), "name"):null);
        return this;
    }

    @Override
    public PageParameters getPageParameters() {
        return super.getPageParameters().add("clusterName", getModelObject().getName());
    }
}
