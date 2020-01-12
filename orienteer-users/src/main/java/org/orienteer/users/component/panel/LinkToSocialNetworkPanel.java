package org.orienteer.users.component.panel;

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
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.MapModel;
import org.orienteer.users.component.SocialNetworkPanel;
import org.orienteer.users.model.*;
import org.orienteer.users.repository.OAuth2Repository;
import org.orienteer.users.service.IOAuth2Service;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Panel for link user with his Social Network
 */
public class LinkToSocialNetworkPanel extends GenericPanel<OrienteerUser> {

    @Inject
    private IOAuth2Service auth2Service;

    private Label feedbackLabel;

    public LinkToSocialNetworkPanel(String id, IModel<OrienteerUser> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(createSocialNetworksServices("networks"));
        add(feedbackLabel = new Label("feedback"));
        feedbackLabel.setOutputMarkupPlaceholderTag(true);
        feedbackLabel.setVisible(false);

        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(SocialNetworkPanel.CSS_STYLE));
    }


    private void onSocialImageClick(AjaxRequestTarget target, IModel<OAuth2Service> model) {
        OAuth2Service service = model.getObject();
        OAuth2ServiceContext ctx = createOAuth2ServiceContext(service);
        ctx.setSocialNetworkLink(true);
        DBClosure.sudoConsumer(db -> ctx.save());
        String url = ctx.getAuthorizationUrl();
        target.appendJavaScript(String.format("window.open('%s', '_blank', 'height=570,width=520')", url));


        Map<String, Object> macros = new HashMap<>();
        macros.put("network", new ResourceModel(service.getProvider().getLabel()).getObject());

        feedbackLabel.setDefaultModel(new StringResourceModel("info.social.network.added", new MapModel<>(macros)));
        feedbackLabel.setVisible(true);
        target.add(feedbackLabel);
    }

    private OAuth2ServiceContext createOAuth2ServiceContext(OAuth2Service service) {
        return auth2Service.requestAuthorizationUrl(service, UUID.randomUUID().toString());
    }

    private ListView<OAuth2Service> createSocialNetworksServices(String id) {
        return new ListView<OAuth2Service>(id, resolveNotLinkedOAuth2Services(getModelObject())) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setModelObject(resolveNotLinkedOAuth2Services(LinkToSocialNetworkPanel.this.getModelObject()));
            }

            @Override
            protected void populateItem(ListItem<OAuth2Service> item) {
                IOAuth2Provider provider = item.getModelObject().getProvider();

                Image image = new Image("networkImage", provider.getIconResourceReference());
                image.setOutputMarkupPlaceholderTag(true);
                image.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        onSocialImageClick(target, item.getModel());
                        image.setVisible(false);
                        target.add(image);
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
