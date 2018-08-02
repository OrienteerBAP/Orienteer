package org.orienteer.web;

import org.orienteer.core.MountPath;
import org.orienteer.core.web.BasePage;
import org.orienteer.model.OrienteerUser;

/**
 * Default registration page which can be overridden in subclasses
 */
@MountPath("/register")
public class DefaultRegistrationPage extends BasePage<OrienteerUser> {

    public DefaultRegistrationPage() {
        super();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
