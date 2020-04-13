package org.orienteer.users.component;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.LoginButtonsPanel;
import org.orienteer.core.component.LoginPanel;
import org.orienteer.users.model.OAuth2Service;

import java.util.List;

/**
 * Login panel with possibility for login throughout social networks
 */
public class OUsersLoginPanel extends LoginPanel {



    private final IModel<List<OAuth2Service>> services;

    public OUsersLoginPanel(String id, IModel<List<OAuth2Service>> services) {
        super(id);
        this.services = services;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SocialNetworkPanel("socialNetworks", "panel.login.social.networks.title", services));
    }

    @Override
    protected LoginButtonsPanel createButtonsPanel(String id) {
        return new OUsersLoginButtonsPanel(id, this::onLoginButtonClick);
    }
}
