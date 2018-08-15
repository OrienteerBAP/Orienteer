package org.orienteer.users.component;

import com.google.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.OrienteerFeedbackPanel;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.service.IOrienteerUsersService;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Default restore user password panel.
 * After restore user password - save user and clear user restore password
 */
public class DefaultRestorePasswordPanel extends GenericPanel<OrienteerUser> {

    @Inject
    private IOrienteerUsersService userService;

    private final IModel<String> passwordModel;

    /**
     * Constructor
     * @param id {@link String} component id
     * @param model {@link IModel<OrienteerUser>} model which contains user for restore
     */
    public DefaultRestorePasswordPanel(String id, IModel<OrienteerUser> model) {
        super(id, model);
        passwordModel = Model.of("");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<?> form = new Form<>("form");

        TextField<String> passwordField = new PasswordTextField("password", passwordModel);
        TextField<String> confirmPasswordField = new PasswordTextField("confirmPassword", Model.of(""));

        form.add(passwordField);
        form.add(confirmPasswordField);
        form.add(new EqualPasswordInputValidator(passwordField, confirmPasswordField));
        form.add(createRestoreButton("restoreButton"));

        add(createFeedbackPanel("feedback"));
        add(form);

        setOutputMarkupPlaceholderTag(true);
    }

    /**
     * Calls when restore was success
     * @param target {@link AjaxRequestTarget} target for update components
     * @param model {@link IModel<OrienteerUser>} model which contains restored user
     */
    protected void onRestore(AjaxRequestTarget target, IModel<OrienteerUser> model) {
        // Override in subclass
    }

    /**
     * Creates restore button which set new password for user and clear user restore status.
     * Then calls {@link DefaultRestorePasswordPanel#onRestore(AjaxRequestTarget, IModel)}
     * @param id {@link String} component id
     * @return {@link AjaxFormCommand<Void>}
     */
    private AjaxFormCommand<Void> createRestoreButton(String id) {
        return new AjaxFormCommand<Void>(id, new ResourceModel("panel.restore.button.restore")) {
            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setBootstrapType(BootstrapType.PRIMARY);
            }

            @Override
            public void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                IModel<OrienteerUser> model = DefaultRestorePasswordPanel.this.getModel();

                DBClosure.sudoConsumer(db -> {
                    OrienteerUser user = model.getObject();
                    String password = passwordModel.getObject();
                    user.setPassword(password);
                    user.save();
                    userService.clearRestoring(user);
                });

                onRestore(target, model);
            }
        };
    }

    protected FeedbackPanel createFeedbackPanel(String id) {
        return new OrienteerFeedbackPanel(id);
    }

}
