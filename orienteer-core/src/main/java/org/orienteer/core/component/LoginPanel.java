package org.orienteer.core.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.OrienteerWebSession;

public class LoginPanel extends Panel {


    private final IModel<String> name;
    private final IModel<String> passwordModel;
    private final IModel<Boolean> rememberMeModel;

    public LoginPanel(String id) {
        super(id);
        name = Model.of();
        passwordModel = Model.of();
        rememberMeModel = Model.of(false);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<?> form = new Form<>("form");
        form.add(new TextField<>("username", name).setRequired(true));
        form.add(new PasswordTextField("password", passwordModel));
        form.add(new CheckBox("rememberMe", rememberMeModel));
        form.add(createButtonsPanel("loginButtonsPanel"));
        add(form);
        add(createFeedbackPanel("feedback"));
        setOutputMarkupPlaceholderTag(true);
    }

    protected FeedbackPanel createFeedbackPanel(String id) {
        return new OrienteerFeedbackPanel(id);
    }

    protected LoginButtonsPanel createButtonsPanel(String id) {
        return new LoginButtonsPanel(id, this::onLoginButtonClick);
    }

    protected void onLoginButtonClick(AjaxRequestTarget target) {
        IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                .getAuthenticationStrategy();
        String username = name.getObject();
        String password = passwordModel.getObject();

        if (OrienteerWebSession.get().signIn(username, password)) {
            if (rememberMeModel.getObject()) {
                strategy.save(username, password);
            } else {
                strategy.remove();
            }
            onSuccessLogin();
        } else {
            onFailedLogin(target);
            strategy.remove();
        }
    }

    private void onSuccessLogin() {
        continueToOriginalDestination();
        setResponsePage(getApplication().getHomePage());
    }

    private void onFailedLogin(AjaxRequestTarget target) {
        error(getLocalizer().getString("login.panel.error", this));
        target.add(LoginPanel.this);
    }
}
