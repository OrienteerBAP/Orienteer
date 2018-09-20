package org.orienteer.core.boot.loader.distributed.service;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.OModulesMicroFrameworkConfig;
import org.orienteer.core.boot.loader.service.IModuleManager;
import org.orienteer.core.util.StartupPropertiesLoader;
import org.orienteer.junit.DistributedModulesStaticInjectorProvider;

import java.util.Properties;

public class DistributedOModulesInitModule extends AbstractModule {

    private final Properties properties1;
    private final Properties properties2;

    public DistributedOModulesInitModule() {
        properties1 = StartupPropertiesLoader.retrieveProperties();
        properties2 = StartupPropertiesLoader.retrieveProperties();

        properties1.put("orienteer.loader.libs.folder", "test-libs-1");
        properties2.put("orienteer.loader.libs.folder", "test-libs-2");
    }


    @Override
    protected void configure() {
        super.configure();
    }

    @Provides
    @Named("config.1")
    @Singleton
    public OModulesMicroFrameworkConfig provideConfig1() {
        return new OModulesMicroFrameworkConfig(properties1);
    }

    @Provides
    @Named("config.2")
    @Singleton
    public OModulesMicroFrameworkConfig provideConfig2() {
        return new OModulesMicroFrameworkConfig(properties2);
    }

    @Provides
    @Named("internal.module.manager.1")
    @Singleton
    public InternalOModuleManager provideModule1(@Named("config.1")OModulesMicroFrameworkConfig config) {
        return createOModuleManager(config);
    }

    @Provides
    @Named("internal.module.manager.2")
    @Singleton
    public InternalOModuleManager provideModule2(@Named("config.2")OModulesMicroFrameworkConfig config) {
        return createOModuleManager(config);
    }

    @Provides
    @Named("hazelcast.1")
    @Singleton
    public HazelcastInstance provideHazelcast1() {
        return Hazelcast.newHazelcastInstance();
    }

    @Provides
    @Named("hazelcast.2")
    @Singleton
    public HazelcastInstance provideHazelcast2() {
        return Hazelcast.newHazelcastInstance();
    }

    @Provides
    @Named("module.manager.1")
    @Singleton
    public IModuleManager provideModuleManager1(
            @Named("hazelcast.1") HazelcastInstance hz
    ) {
        return new DistributedTestModuleManagerImpl(hz);
    }

    @Provides
    @Named("module.manager.2")
    @Singleton
    public IModuleManager provideModuleManager2(
            @Named("hazelcast.2") HazelcastInstance hz
    ) {
        return new DistributedTestModuleManagerImpl(hz);
    }

    private InternalOModuleManager createOModuleManager(OModulesMicroFrameworkConfig config) {
        return new InternalOModuleManager(config) {
            @Override
            protected Injector getInjector() {
                return DistributedModulesStaticInjectorProvider.INJECTOR_PROVIDER.get();
            }
        };
    }
}

