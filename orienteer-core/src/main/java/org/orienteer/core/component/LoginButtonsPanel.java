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

public class LoginButtonsPanel extends Panel {

    public static final JavaScriptResourceReference LOGIN_BUTTONS_JS = new JavaScriptResourceReference(LoginButtonsPanel.class, "login-buttons.js");

    private final Consumer<AjaxRequestTarget> loginConsumer;

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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(LOGIN_BUTTONS_JS));
        response.render(OnLoadHeaderItem.forScript(String.format("loginOnEnter('%s')", get("loginButton:command").getMarkupId())));
    }
}
