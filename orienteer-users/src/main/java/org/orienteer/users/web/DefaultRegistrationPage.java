package org.orienteer.users.web;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.web.BasePage;
import org.orienteer.core.web.HomePage;
import org.orienteer.users.component.DefaultRegistrationPanel;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.resource.RegistrationResource;
import org.orienteer.users.service.IOrienteerUsersService;
import ru.ydn.wicket.wicketorientdb.model.ODocumentWrapperModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Default registration page which can be overridden in subclasses.
 * Contains new {@link OrienteerUser} as component model
 */
@MountPath("/register")
public class DefaultRegistrationPage extends BasePage<OrienteerUser> {

    @Inject
    private IOrienteerUsersService service;
    
    private WebMarkupContainer container;

    /**
     * Constructor.
     * Creates and set new {@link OrienteerUser} as component model
     */
    public DefaultRegistrationPage() {
        super();
        setModel(new ODocumentWrapperModel<>(service.createUser()));
    }

    /**
     * Constructor
     * Retrieve parameter {@link RegistrationResource#PARAMETER_ID} from parameters.
     * If user exists with given id and account status is {@link OrienteerUser.STATUSES#SUSPENDED}, so account status
     * set to {@link OSecurityUser.STATUSES#ACTIVE}.
     * If user exists, so user will be redirect to {@link HomePage}.
     * Otherwise will be created new {@link OrienteerUser} for register user
     * @param parameters {@link PageParameters} parameters which uses for activate account
     */
    public DefaultRegistrationPage(PageParameters parameters) {
        super(parameters);
        setModel(new ODocumentWrapperModel<>(service.createUser()));
    }

    /**
     * Initialize component.
     * If user already logged in Orienteer, so user will be redirect to {@link HomePage}
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (OrienteerWebSession.get().getUser() != null) {
            setResponsePage(HomePage.class);
        }
        add(container = createContainer("container"));
        add(new Label("registrationTitle", getRegistrationTitleModel()));
    }

    /**
     * Creates container which will be used for Ajax update when user will be registered
     * @param id {@link String} component id
     * @return {@link WebMarkupContainer}
     */
    private WebMarkupContainer createContainer(String id) {
        return new WebMarkupContainer(id) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(createRegistrationPanel("registrationPanel"));
                add(createRegistrationSuccessLabel("registrationSuccessLabel"));
                setOutputMarkupPlaceholderTag(true);
            }
        };
    }

    /**
     * Created default registration panel
     * Overrides {@link DefaultRegistrationPanel#onRegister(AjaxRequestTarget, IModel)}:
     *      1. set user name equals user email
     *      2. set account status {@link OSecurityUser.STATUSES#SUSPENDED}
     *      3. save new user account
     *      4. send mail to user with activation link
     * If user document was saved into database, so this panel doesn't visible
     * @param id {@link String} component id
     * @return {@link DefaultRegistrationPanel} default registration panel
     */
    protected GenericPanel<OrienteerUser> createRegistrationPanel(String id) {
        return new DefaultRegistrationPanel(id, getModel()) {
            @Override
            protected void onRegister(AjaxRequestTarget target, IModel<OrienteerUser> model) {
                OrienteerUser user = model.getObject();

                DBClosure.sudoSave(user);
                service.notifyUserAboutRegistration(user);

                target.add(container);
            }

            @Override
            protected void onConfigure() {
                OrienteerUser user = getModelObject();
                setVisible(!user.getDocument().getIdentity().isValid());
                super.onConfigure();
            }
        };
    }

    /**
     * @return {@link IModel<String>} which uses in registration card title
     */
    protected IModel<String> getRegistrationTitleModel() {
        return new ResourceModel("page.registration.title");
    }

    /**
     * Creates label which will be displayed when user successfully registered
     * If user document didn't saved in database, so component doesn't visible
     * @param id {@link String} component id
     * @return {@link Label}
     */
    protected Label createRegistrationSuccessLabel(String id) {
        return new Label(id, new ResourceModel("page.registration.success")) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                OrienteerUser user = getModelObject();
                setVisible(user.getDocument().getIdentity().isValid());
            }
        };
    }
    
    @Override
    public IModel<String> getTitleModel() {
        return new ResourceModel("page.registration.page.title");
    }

    @Override
    protected String getBodyAppSubClasses() {
        return "flex-row align-items-center footer-fixed";
    }
}
