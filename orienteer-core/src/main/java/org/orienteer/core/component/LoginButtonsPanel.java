package org.orienteer.core.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.orienteer.core.component.command.AjaxFormCommand;

import java.util.function.Consumer;

import static org.apache.wicket.util.lang.Args.notNull;

/**
 * Contains buttons for login panel
 */
public class LoginButtonsPanel extends Panel {

    public static final JavaScriptResourceReference LOGIN_BUTTONS_JS = new JavaScriptResourceReference(LoginButtonsPanel.class, "login-buttons.js");

    private final Consumer<AjaxRequestTarget> loginConsumer;

    /**
     * Constructor
     * @param id {@link String} component id
     * @param loginConsumer {@link SerializableConsumer<AjaxRequestTarget>} consumer which will be called when user clicks on login button
     */
    public LoginButtonsPanel(String id, SerializableConsumer<AjaxRequestTarget> loginConsumer) {
        super(id);
        this.loginConsumer = notNull(loginConsumer, "loginConsumer ca't be null");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createLoginButton("loginButton"));
        setOutputMarkupPlaceholderTag(true);
    }

    /**
     * Creates login button. Calls {@link LoginButtonsPanel#loginConsumer} when user clicks on button
     * Used primary bootstrap type {@link BootstrapType#PRIMARY} and CSS class "mx-auto"
     * @param id {@link String} component id
     * @return {@link AjaxFormCommand<Void>}
     */
    protected AjaxFormCommand<Void> createLoginButton(String id) {
        return new AjaxFormCommand<Void>(id, new ResourceModel("login.panel.button.login")) {

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setBootstrapType(BootstrapType.PRIMARY);
                add(AttributeModifier.append("class", "mx-auto"));
            }

            @Override
            public void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                loginConsumer.accept(target);
            }
        };
    }

    /**
     * Added {@link LoginButtonsPanel#LOGIN_BUTTONS_JS} to head and calls
     * {@code
     *      loginOnEnter('commandId');
     * }
     * for send form by press "Enter" button
     * commandId - this is component id from component {@link AjaxFormCommand#link}
     * @param response {@link IHeaderResponse}
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(LOGIN_BUTTONS_JS));
        response.render(OnLoadHeaderItem.forScript(String.format("loginOnEnter('%s')", get("loginButton:command").getMarkupId())));
    }
}
