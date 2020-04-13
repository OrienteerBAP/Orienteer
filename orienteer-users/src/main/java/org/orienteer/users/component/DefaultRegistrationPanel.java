package org.orienteer.users.component;

import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.OrienteerFeedbackPanel;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.repository.OAuth2Repository;
import org.orienteer.users.validation.UserEmailValidator;

import java.util.List;

/**
 * Default registration panel.
 * Contains form for fill fields in class {@link OrienteerUser}
 */
public class DefaultRegistrationPanel extends GenericPanel<OrienteerUser> {

    private final IModel<String> passwordModel;

    /**
     * Constructor
     * @param id component id
     * @param model model which contains new {@link OrienteerUser}
     */
    public DefaultRegistrationPanel(String id, IModel<OrienteerUser> model) {
        super(id, model);
        passwordModel = Model.of();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        IModel<OrienteerUser> model = getModel();

        Form<?> form = new Form<>("form");

        form.add(new TextField<>("firstName", new PropertyModel<>(model, "firstName")).setRequired(true));
        form.add(new TextField<>("lastName", new PropertyModel<>(model, "lastName")).setRequired(true));
        form.add(createEmailField("email", new PropertyModel<>(model, "email")));

        TextField<String> passwordField = new PasswordTextField("password", passwordModel);
        TextField<String> confirmPasswordField = new PasswordTextField("confirmPassword", Model.of(""));

        form.add(passwordField);
        form.add(confirmPasswordField);
        form.add(new EqualPasswordInputValidator(passwordField, confirmPasswordField));
        form.add(createRegisterButton("registerButton"));
        form.add(createSocialNetworksPanel("socialNetworks"));

        add(form);
        add(createFeedbackPanel("feedback"));
        setOutputMarkupPlaceholderTag(true);
    }

    private Panel createSocialNetworksPanel(String id) {
        List<OAuth2Service> services = OAuth2Repository.getOAuth2Services(true);
        if (services.isEmpty()) {
            return new EmptyPanel(id);
        }

        return new SocialNetworkPanel(id, "panel.registration.social.networks.title", new ListModel<>(services)) {
            @Override
            protected OAuth2ServiceContext createOAuth2ServiceContext(OAuth2Service service) {
                OAuth2ServiceContext ctx = super.createOAuth2ServiceContext(service);
                ctx.setRegistration(true);
                return ctx;
            }
        };
    }

    /**
     * Calls when form was success submitted
     * @param target target for update components
     * @param model model which contains filled user
     */
    protected void onRegister(AjaxRequestTarget target, IModel<OrienteerUser> model) {
        // Override in subclass
    }

    private EmailTextField createEmailField(String id, IModel<String> model) {
        EmailTextField field = new EmailTextField(id, model);
        field.setRequired(true);
        field.add(new UserEmailValidator());
        return field;
    }

    /**
     * Create register button, which set user password and then calls {@link DefaultRegistrationPanel#onRegister(AjaxRequestTarget, IModel)}
     * @param id component id
     * @return register button
     */
    private AjaxFormCommand<Void> createRegisterButton(String id) {
        return new AjaxFormCommand<Void>(id, "panel.registration.button.register") {
            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setBootstrapType(BootstrapType.PRIMARY);
            }

            @Override
            public void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                OrienteerUser user = DefaultRegistrationPanel.this.getModelObject();
                user.setName(user.getEmail());
                user.setPassword(passwordModel.getObject());
                user.setAccountStatus(OSecurityUser.STATUSES.SUSPENDED);
                onRegister(target, DefaultRegistrationPanel.this.getModel());
            }
        };
    }

    protected FeedbackPanel createFeedbackPanel(String id) {
        return new OrienteerFeedbackPanel(id);
    }

}
