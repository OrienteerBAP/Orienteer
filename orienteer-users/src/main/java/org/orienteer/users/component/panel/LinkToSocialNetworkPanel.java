package org.orienteer.users.component.panel;

import com.google.inject.Inject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.users.component.SocialNetworkPanel;
import org.orienteer.users.model.*;
import org.orienteer.users.repository.OAuth2Repository;
import org.orienteer.users.service.IOAuth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LinkToSocialNetworkPanel extends GenericPanel<OrienteerUser> {

    private static final Logger LOG = LoggerFactory.getLogger(LinkToSocialNetworkPanel.class);

    @Inject
    private IOAuth2Service auth2Service;

    public LinkToSocialNetworkPanel(String id, IModel<OrienteerUser> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createSocialNetworksServices("networks", resolveNotLinkedOAuth2Services(getModelObject())));

        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(SocialNetworkPanel.CSS_STYLE));
    }

    /**
     * Calls when user clicks on social image for login user.
     * Redirects user to social network authorization url
     * See {@link IOAuth2Service#requestAuthorizationUrl(OAuth2Service, String)}
     * @param target {@link AjaxRequestTarget}
     * @param model model with {@link OAuth2Service} for login
     */
    protected void onSocialImageClick(AjaxRequestTarget target, IModel<OAuth2Service> model) {
//        OAuth2Service service = model.getObject();
//        OAuth2ServiceContext ctx = createOAuth2ServiceContext(service);
//        DBClosure.sudoConsumer(db -> ctx.save());
//        throw new RedirectToUrlException(ctx.getAuthorizationUrl());
        LOG.info("Click on social image: {}", model.getObject().getProvider().getName());
    }

    protected OAuth2ServiceContext createOAuth2ServiceContext(OAuth2Service service) {
        return auth2Service.requestAuthorizationUrl(service, UUID.randomUUID().toString());
    }

    private ListView<OAuth2Service> createSocialNetworksServices(String id, List<OAuth2Service> services) {
        return new ListView<OAuth2Service>(id, services) {
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

    private List<OAuth2Service> resolveNotLinkedOAuth2Services(OrienteerUser user) {
        List<OAuth2Service> activeServices = OAuth2Repository.getOAuth2Services(true);
        List<OAuth2Service> usedServices = user.getSocialNetworks().stream()
                .map(OUserSocialNetwork::getService)
                .collect(Collectors.toCollection(LinkedList::new));

        return activeServices.stream()
                .filter(service -> !usedServices.contains(service))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
