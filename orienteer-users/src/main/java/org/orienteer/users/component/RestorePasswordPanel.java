package org.orienteer.users.component;

import com.google.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.users.component.event.RestorePasswordEventPayload;
import org.orienteer.users.repository.OrienteerUserRepository;
import org.orienteer.users.service.IOrienteerUsersService;

/**
 * Panel for restore user password
 */
public class RestorePasswordPanel extends GenericPanel<Boolean> {

    private final IModel<String> emailModel;

    @Inject
    private IOrienteerUsersService usersService;

    public RestorePasswordPanel(String id, IModel<Boolean> model) {
        super(id, model);
        emailModel = Model.of();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<?> form = new Form<>("form");
        form.add(createEmailTextField("email"));
        form.add(createRestorePasswordButton("restorePasswordButton"));
        form.add(createLoginLink("loginLink"));

        add(form);
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        Boolean restore = getModelObject();
        setVisible(restore != null && restore);
    }

    private TextField<String> createEmailTextField(String id) {
        TextField<String> emailTextField = new EmailTextField(id, emailModel);
        emailTextField.setRequired(true);
        return emailTextField;
    }

    private AjaxFormCommand<Void> createRestorePasswordButton(String id) {
        return new AjaxFormCommand<Void>(id, new ResourceModel("panel.restore.password.button.restore")) {

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setBootstrapType(BootstrapType.PRIMARY);
            }

            @Override
            public void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                OrienteerUserRepository.getUserByEmail(emailModel.getObject())
                        .ifPresent(user -> usersService.restoreUserPassword(user));
                emailModel.setObject(null);
                send(getParent(), Broadcast.BUBBLE, new RestorePasswordEventPayload(target, false));
            }
        };
    }

    private AjaxLink<Void> createLoginLink(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                emailModel.setObject(null);
                send(getParent(), Broadcast.BUBBLE, new RestorePasswordEventPayload(target, false));
            }
        };
    }
}
