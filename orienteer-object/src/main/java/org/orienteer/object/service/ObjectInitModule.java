package org.orienteer.object.service;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.orienteer.core.service.OverrideModule;
import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.RepositoryModule;

/**
 * Init module for Orineteer's module "orienteer-object"
 * @see <a href="https://github.com/xvik/guice-persist-orient">guice-persist-orient</a>
 */
@OverrideModule
public class ObjectInitModule extends AbstractModule {

    /**
     * Install "guice-persist-orient" modules and custom module for support objects {@link OAutoScanSchemeModule}
     */
    @Override
    protected void configure() {
        String orientUrl = System.getProperty("url");
        String username = System.getProperty("admin.username");
        String password = System.getProperty("admin.password");

        Module module = Modules.combine(
                new OrientModule(orientUrl, username, password),
                new OAutoScanSchemeModule(),
                new RepositoryModule()
        );
        install(module);
    }
}
