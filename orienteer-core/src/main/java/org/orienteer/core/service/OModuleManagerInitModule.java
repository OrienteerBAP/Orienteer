package org.orienteer.core.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.orienteer.core.loader.OrienteerOutsideModules;
import org.orienteer.core.loader.OrienteerOutsideModulesManager;

/**
 * @author Vitaliy Gonchar
 */
public class OModuleManagerInitModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new OLoaderInitModule());
        bind(OrienteerOutsideModulesManager.class).in(Singleton.class);
        requestStaticInjection(OrienteerOutsideModules.class);
    }
}
