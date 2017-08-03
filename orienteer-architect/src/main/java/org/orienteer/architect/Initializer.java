package org.orienteer.architect;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;

/**
 * {@link IInitializer} for 'orienteer-architect' module
 */
public class Initializer implements IInitializer {
    @Override
    public void init(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        app.registerModule(OArchitectModule.class);
    }

    @Override
    public void destroy(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        app.unregisterModule(OArchitectModule.class);
    }
}
