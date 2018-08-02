package org.orienteer.users.web;

import org.orienteer.core.web.BasePage;
import org.orienteer.users.model.OrienteerUser;

/**
 * Default registration page which can be overridden in subclasses
 */
public class DefaultRegistrationPage extends BasePage<OrienteerUser> {

    public DefaultRegistrationPage() {
        super();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
