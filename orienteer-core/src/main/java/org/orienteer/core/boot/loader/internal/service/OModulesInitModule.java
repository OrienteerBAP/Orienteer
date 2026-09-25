package org.orienteer.core.boot.loader.internal.service;

import com.google.inject.AbstractModule;
import org.orienteer.core.dao.AbstractDynamicProvider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.OModulesMicroFrameworkConfig;

import java.util.Properties;

/**
 * Guice Module for bind modules micro framework dependencies
 */
public class OModulesInitModule extends AbstractModule {

    private final Properties properties;

    public OModulesInitModule(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected void configure() {
        // This is the parent injector: just-in-time @ProvidedBy bindings are usually created here
        AbstractDynamicProvider.bindProvisionListener(binder());
    }

    @Provides
    public OModulesMicroFrameworkConfig provideConfig() {
        return new OModulesMicroFrameworkConfig(properties);
    }

    @Provides
    @Singleton
    public InternalOModuleManager provideModuleManager(OModulesMicroFrameworkConfig config) {
        return new InternalOModuleManager(config);
    }

}
