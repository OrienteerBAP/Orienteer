package org.orienteer.users.web;

import com.google.common.base.Strings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;
import org.orienteer.core.web.BasePage;
import org.orienteer.users.component.DefaultRestorePasswordPanel;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.resource.RestorePasswordResource;
import org.orienteer.users.util.OUsersDbUtils;
import ru.ydn.wicket.wicketorientdb.model.ODocumentWrapperModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Default restore page which can be overridden in subclasses.
 * Contains exists {@link OrienteerUser} with given restore id as component model
 */
@MountPath("/restore")
public class DefaultRestorePasswordPage extends BasePage<OrienteerUser> {

    private WebMarkupContainer container;

    public DefaultRestorePasswordPage() {
        throw new RedirectToUrlException("/home");
    }

    public DefaultRestorePasswordPage(PageParameters params) {
        super(params);
    }

    /**
     * Create model which contains user
     * with restore id from params with key {@link RestorePasswordResource#PARAMETER_ID} or redirect user to home page
     * @param params params which contains user restore id
     * @return model if restore id present and user with given restore id exists in database
     * @throws RedirectToUrlException if user with given restore id doesn't exists in database or restore id is null or empty
     */
    @Override
    protected IModel<OrienteerUser> resolveByPageParameters(PageParameters params) {
        final String id = params.get(RestorePasswordResource.PARAMETER_ID).toOptionalString();
        if (Strings.isNullOrEmpty(id)) {
            throw new RedirectToUrlException("/home");
        }

        return OUsersDbUtils.getUserByRestoreId(id)
                .map(ODocumentWrapperModel::new)
                .orElseThrow(() -> new RedirectToUrlException("/home"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(container = createContainer("container"));
        add(new Label("restoreTitle", getRestoreTitle()));
    }

    /**
     * Creates container which will be used for Ajax update when user will be restored
     * @param id component id
     * @return container
     */
    private WebMarkupContainer createContainer(String id) {
        return new WebMarkupContainer(id) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(createRestorePasswordPanel("restorePanel"));
                add(createRestoreSuccessMessage("restoreSuccessMessage"));
                setOutputMarkupPlaceholderTag(true);
            }
        };
    }

    /**
     * Creates restore panel
     * @param id component id
     * @return restore password panel
     */
    protected GenericPanel<OrienteerUser> createRestorePasswordPanel(String id) {
        return new DefaultRestorePasswordPanel(id, getModel()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(DBClosure.sudo(db -> getModelObject()) != null);
            }

            @Override
            protected void onRestore(AjaxRequestTarget target, IModel<OrienteerUser> model) {
                model.setObject(null);
                target.add(container);
            }
        };
    }

    /**
     * Creates container which contains success message about restoring user password
     * @param id component id
     * @return restore success message
     */
    protected WebMarkupContainer createRestoreSuccessMessage(String id) {
        return new WebMarkupContainer(id) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(DBClosure.sudo(db -> getModelObject()) == null);
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(new Link<Void>("loginPage") {
                    @Override
                    public void onClick() {
                        throw new RedirectToUrlException("/login");
                    }
                });
                setOutputMarkupPlaceholderTag(true);
            }
        };
    }

    /**
     * @return model with text for restore password card title
     */
    protected IModel<String> getRestoreTitle() {
        return new ResourceModel("page.restore.title");
    }

    @Override
    public IModel<String> getTitleModel() {
        return new ResourceModel("page.restore.page.title");
    }

    @Override
    protected String getBodyAppSubClasses() {
        return "flex-row align-items-center footer-fixed";
    }
}
