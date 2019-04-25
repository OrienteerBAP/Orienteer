package org.orienteer.users.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.resource.CssResourceReference;
import org.orienteer.core.component.LoginPanel;
import org.orienteer.users.model.IOAuth2Provider;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import org.orienteer.users.repository.OAuth2Repository;
import org.orienteer.users.service.IOAuth2Service;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class OUsersLoginPanel extends LoginPanel {

    public static final CssResourceReference CSS_STYLE = new CssResourceReference(OUsersLoginPanel.class, "style.css");

    @Inject
    private IOAuth2Service auth2Service;

    public OUsersLoginPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createSocialNetworksServices("networks"));
    }

    private ListView<OAuth2Service> createSocialNetworksServices(String id) {
        List<OAuth2Service> services = OAuth2Repository.getOAuth2Services();

        return new ListView<OAuth2Service>(id, new ListModel<>(services)) {
            @Override
            protected void populateItem(ListItem<OAuth2Service> item) {
                IOAuth2Provider provider = item.getModelObject().getProvider();

                Image image = new Image("networkImage", provider.getIconResourceReference());
                image.setOutputMarkupPlaceholderTag(true);
                image.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                       onSocialImageClick(target, item.getModel());
                    }
                });
                image.add(new AttributeModifier("alt", new ResourceModel(provider.getLabel()).getObject()));

                item.add(image);
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                setReuseItems(true);
            }
        };
    }

    protected void onSocialImageClick(AjaxRequestTarget target, IModel<OAuth2Service> model) {
        OAuth2Service service = model.getObject();
        OAuth2ServiceContext ctx = auth2Service.requestAuthorizationUrl(service, UUID.randomUUID().toString());
        ctx.getAuthorizationUrl();
        DBClosure.sudoConsumer(db -> ctx.save());
        throw new RedirectToUrlException(ctx.getAuthorizationUrl());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS_STYLE));
    }
}
