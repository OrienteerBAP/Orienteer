package org.orienteer.junit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import org.orienteer.core.boot.loader.distributed.service.DistributedOModulesInitModule;
import org.orienteer.core.boot.loader.distributed.service.OModulesTestInitModule;

public class DistributedModulesStaticInjectorProvider implements Provider<Injector> {

    public static final Provider<Injector> INJECTOR_PROVIDER;

    static {
        INJECTOR_PROVIDER = new DistributedModulesStaticInjectorProvider(new DistributedOModulesInitModule(), new OModulesTestInitModule());
    }

    private final Injector injector;

    public DistributedModulesStaticInjectorProvider(Module...modules) {
        injector = Guice.createInjector(modules);
    }

    @Override
    public Injector get() {
        return injector;
    }
}
