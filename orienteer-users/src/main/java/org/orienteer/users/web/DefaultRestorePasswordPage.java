package org.orienteer.users.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.web.BasePage;
import org.orienteer.users.model.OrienteerUser;

public class DefaultRestorePasswordPage extends BasePage<OrienteerUser> {

    public DefaultRestorePasswordPage(PageParameters parameters) {
        super(parameters);
    }

    // TODO: query user by id
    @Override
    protected IModel<OrienteerUser> resolveByPageParameters(PageParameters pageParameters) {
        return super.resolveByPageParameters(pageParameters);
    }
}
