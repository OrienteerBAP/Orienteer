package org.orienteer.users.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.orienteer.core.component.LoginButtonsPanel;
import org.orienteer.users.component.event.RestorePasswordEventPayload;
import org.orienteer.users.repository.OrienteerUserModuleRepository;

/**
 * Login buttons panel.
 * Contains restore password button if restore feature is active
 */
public class OUsersLoginButtonsPanel extends LoginButtonsPanel {

    /**
     * Constructor
     *
     * @param id            {@link String} component id
     * @param loginConsumer consumer which will be called when user clicks on login button
     */
    public OUsersLoginButtonsPanel(String id, SerializableConsumer<AjaxRequestTarget> loginConsumer) {
        super(id, loginConsumer);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createForgotPasswordLink("forgotPasswordLink"));
    }

    private AjaxLink<Void> createForgotPasswordLink(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(this, Broadcast.BUBBLE, new RestorePasswordEventPayload(target, true));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(OrienteerUserModuleRepository.isRestorePassword());
            }
        };
    }
}
