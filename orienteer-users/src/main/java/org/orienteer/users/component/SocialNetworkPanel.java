package org.orienteer.users.component;

import com.google.inject.Inject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.resource.CssResourceReference;
import org.orienteer.users.model.IOAuth2Provider;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import org.orienteer.users.service.IOAuth2Service;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.UUID;

public class SocialNetworkPanel extends GenericPanel<List<OAuth2Service>> {

    public static final CssResourceReference CSS_STYLE = new CssResourceReference(SocialNetworkPanel.class, "style.css");


    @Inject
    private IOAuth2Service auth2Service;

    private final String titleKey;

    public SocialNetworkPanel(String id, String titleKey, IModel<List<OAuth2Service>> model) {
        super(id, model);
        this.titleKey = titleKey;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("title", new ResourceModel(titleKey)));
        add(createSocialNetworksServices("networks"));

        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS_STYLE));
    }

    /**
     * Calls when user clicks on social image for login user.
     * Redirects user to social network authorization url
     * See {@link IOAuth2Service#requestAuthorizationUrl(OAuth2Service, String)}
     * @param target {@link AjaxRequestTarget}
     * @param model model with {@link OAuth2Service} for login
     */
    protected void onSocialImageClick(AjaxRequestTarget target, IModel<OAuth2Service> model) {
        OAuth2Service service = model.getObject();
        OAuth2ServiceContext ctx = createOAuth2ServiceContext(service);
        DBClosure.sudoConsumer(db -> ctx.save());
        throw new RedirectToUrlException(ctx.getAuthorizationUrl());
    }

    protected OAuth2ServiceContext createOAuth2ServiceContext(OAuth2Service service) {
        return auth2Service.requestAuthorizationUrl(service, UUID.randomUUID().toString());
    }

    private ListView<OAuth2Service> createSocialNetworksServices(String id) {
        return new ListView<OAuth2Service>(id, getModel()) {
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

}
