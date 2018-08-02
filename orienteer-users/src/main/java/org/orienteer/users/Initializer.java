package org.orienteer.users;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.users.module.OrienteerUsersModule;

/**
 * {@link IInitializer} for Orienteer Users module 
 */
public class Initializer implements IInitializer {
    @Override
    public void init(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        app.registerModule(OrienteerUsersModule.class);
    }

    @Override
    public void destroy(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        app.unregisterModule(OrienteerUsersModule.class);
    }
}
