package org.orienteer.loader;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.loader.module.JarModules;
import org.orienteer.loader.module.OModuleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vitaliy Gonchar
 */
public class Initializer implements IInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(Initializer.class);

    @Override
    public void init(Application application) {
        LOG.debug("Init module");
        OrienteerWebApplication app = (OrienteerWebApplication)application;
        JarModules.loadModules(app);
        app.registerWidgets("org.orienteer.loader.component.widget");
        app.registerModule(OModuleLoader.class);
    }

    @Override
    public void destroy(Application application) {
        LOG.debug("Destroy module");
    }
}
