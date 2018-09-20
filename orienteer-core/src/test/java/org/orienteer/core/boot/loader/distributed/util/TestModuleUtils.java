package org.orienteer.core.boot.loader.distributed.util;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.junit.DistributedModulesStaticInjectorProvider;

public final class TestModuleUtils {

    private TestModuleUtils() {}

    public static InternalOModuleManager getModuleManager(String id) {
        Injector injector = DistributedModulesStaticInjectorProvider.INJECTOR_PROVIDER.get();
        return injector.getInstance(Key.get(InternalOModuleManager.class, Names.named("internal.module.manager." + id)));
    }
}
