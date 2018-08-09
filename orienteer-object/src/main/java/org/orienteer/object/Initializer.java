package org.orienteer.object;

import com.google.inject.persist.PersistService;
import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;

/**
 * Wicket initializer for Orienteer's module "orienteer-object"
 */
public class Initializer implements IInitializer {


    /**
     * Start {@link PersistService}
     * @see <a href="https://github.com/xvik/guice-persist-orient#lifecycle">Lifecycle of guice-persist-orient</a>
     * @param application Wicket application
     */
    @Override
    public void init(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        PersistService persistService = app.getServiceInstance(PersistService.class);
        persistService.start();
    }

    /**
     * Stop {@link PersistService}
     * @see <a href="https://github.com/xvik/guice-persist-orient#lifecycle">Lifecycle of guice-persist-orient</a>
     * @param application Wicket application
     */
    @Override
    public void destroy(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        PersistService persistService = app.getServiceInstance(PersistService.class);
        persistService.stop();
    }
}
