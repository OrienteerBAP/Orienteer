package org.orienteer.core.boot.loader.internal.service;

import com.google.inject.AbstractModule;
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
