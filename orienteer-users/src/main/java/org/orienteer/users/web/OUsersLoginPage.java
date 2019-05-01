package org.orienteer.users.web;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.LoginButtonsPanel;
import org.orienteer.core.component.LoginPanel;
import org.orienteer.core.web.LoginPage;
import org.orienteer.users.component.OUsersLoginButtonsPanel;
import org.orienteer.users.component.OUsersLoginFooterPanel;
import org.orienteer.users.component.OUsersLoginPanel;
import org.orienteer.users.component.RestorePasswordPanel;
import org.orienteer.users.component.event.RestorePasswordEventPayload;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import org.orienteer.users.repository.OAuth2Repository;
import org.orienteer.users.repository.OrienteerUserModuleRepository;
import org.orienteer.users.service.IOAuth2Service;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;

/**
 * Extends {@link LoginPage} for allow :login throughout social networks, registration, restore user password
 */
@MountPath("/login")
public class OUsersLoginPage extends LoginPage {

    @Inject
    private IOAuth2Service authService;

    private final IModel<Boolean> restoreModel;

    public OUsersLoginPage() {
        this(Model.of(false));
    }

    public OUsersLoginPage(IModel<Boolean> restoreModel) {
        super();
        this.restoreModel = restoreModel;
    }

    public OUsersLoginPage(PageParameters params) {
        this();

        if (OrienteerUserModuleRepository.isOAuth2Active()) {
            String code = params.get("code").toOptionalString();
            String state = params.get("state").toOptionalString();

            if (!Strings.isNullOrEmpty(code) && !Strings.isNullOrEmpty(state)) {
                OAuth2Repository.getServiceContextByState(state)
                        .ifPresent(ctx -> authorize(ctx, code));
            }
        }
    }

    private void authorize(OAuth2ServiceContext ctx, String code) {
        try {
            boolean authorized;
            if (ctx.isRegistration()) {
                authorized = authService.register(ctx.getService(), code);
            } else {
                authorized = authService.authorize(ctx.getService(), code);
            }
            if (authorized) {
                continueToOriginalDestination();
                setResponsePage(getApplication().getHomePage());
            }
        } catch (IllegalStateException ex) {
            error(ex.getMessage());
        }

        ctx.setUsed(true);
        DBClosure.sudoConsumer(db -> ctx.save());
    }

    @Override
    protected void initialize(WebMarkupContainer container) {
        container.add(createRestorePasswordPanel("restorePasswordPanel"));
    }

    protected WebMarkupContainer createRestorePasswordPanel(String id) {
        if (!OrienteerUserModuleRepository.isRestorePassword()) {
            return new EmptyPanel(id);
        }
        return new RestorePasswordPanel(id, restoreModel);
    }

    @Override
    protected WebMarkupContainer createLoginPanel(String id) {
        if (OrienteerUserModuleRepository.isOAuth2Active()) {
            List<OAuth2Service> services = OAuth2Repository.getOAuth2Services(true);
            if (!services.isEmpty()) {
                return new OUsersLoginPanel(id, new ListModel<>(services)) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(restoreModel.getObject() == null || !restoreModel.getObject());
                    }
                };
            }
        }

        return new LoginPanel(id) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(restoreModel.getObject() == null || !restoreModel.getObject());
            }

            @Override
            protected LoginButtonsPanel createButtonsPanel(String id) {
                return new OUsersLoginButtonsPanel(id, this::onLoginButtonClick);
            }
        };
    }

    @Override
    protected WebMarkupContainer createLoginFooter(String id) {
        if (OrienteerUserModuleRepository.isRegistrationActive()) {
            return new OUsersLoginFooterPanel(id);
        }
        return super.createLoginFooter(id);
    }

    @Override
    protected String getContainerClasses(WebMarkupContainer loginPanel) {
        if (loginPanel instanceof OUsersLoginPanel) {
            return "col-md-6 card-group";
        }
        return super.getContainerClasses(loginPanel);
    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof RestorePasswordEventPayload) {
            RestorePasswordEventPayload restorePayload = (RestorePasswordEventPayload) event.getPayload();
            if (restoreModel.getObject() != restorePayload.isRestore()) {
                restoreModel.setObject(restorePayload.isRestore());
                restorePayload.getAjaxRequestTarget().add(container);
            }
        }
    }
}
