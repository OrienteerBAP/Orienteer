package org.orienteer.core.loader;

import com.google.common.collect.Maps;
import org.kevoree.kcl.api.FlexyClassLoader;
import org.kevoree.kcl.api.FlexyClassLoaderFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
public abstract class OLoaderStorage {

    private static FlexyClassLoader rootLoader;
    private static FlexyClassLoader currentModuleLoader;

    private static final Map<String, FlexyClassLoader> MODULE_LOADERS = Maps.newConcurrentMap();


    private static FlexyClassLoader trustyModuleLoader;
    private static FlexyClassLoader sandboxModuleLoader;

    public static synchronized FlexyClassLoader getRootLoader() {
        return rootLoader;
    }

    public static synchronized FlexyClassLoader getTrustyModuleLoader(boolean create) {
        if (trustyModuleLoader == null || create)
            trustyModuleLoader = getClassLoader();
        return trustyModuleLoader;
    }

    public static synchronized FlexyClassLoader getSandboxModuleLoader(boolean create) {
        if (sandboxModuleLoader == null || create)
            sandboxModuleLoader = getClassLoader();
        return sandboxModuleLoader;
    }

    private static synchronized FlexyClassLoader getClassLoader() {
        FlexyClassLoader classLoader = FlexyClassLoaderFactory.INSTANCE.create();
        return classLoader;
    }

    public static synchronized FlexyClassLoader getModuleLoader(boolean create) {
        if (currentModuleLoader == null || create) currentModuleLoader = getClassLoader();
        return currentModuleLoader;
    }

    public static synchronized FlexyClassLoader createNewRootLoader() {
        rootLoader = FlexyClassLoaderFactory.INSTANCE.create();
//        if (sandboxModuleLoader != null) rootLoader.attachChild(sandboxModuleLoader);
        for (FlexyClassLoader moduleLoader : MODULE_LOADERS.values()) {
            rootLoader.attachChild(moduleLoader);
        }
        return rootLoader;
    }

    public static synchronized FlexyClassLoader updateRootLoader() {
        if (rootLoader == null) rootLoader = FlexyClassLoaderFactory.INSTANCE.create();
        for (FlexyClassLoader moduleLoader : MODULE_LOADERS.values()) {
            rootLoader.attachChild(moduleLoader);
        }
        return rootLoader;
    }

    public static synchronized FlexyClassLoader addModuleLoader(String name,
                                                                FlexyClassLoader moduleLoader) {
        rootLoader.attachChild(moduleLoader);
        return MODULE_LOADERS.put(name, moduleLoader);
    }

    public static synchronized FlexyClassLoader removeModuleLoader(String name) {
        return MODULE_LOADERS.remove(name);
    }

    public static Set<String> getNamesOfLoadedModules() {
        return Collections.unmodifiableSet(MODULE_LOADERS.keySet());
    }

    public static FlexyClassLoader getModuleLoader(String moduleName) {
        return MODULE_LOADERS.get(moduleName);
    }

    public static boolean isModuleClassLoaderPreset(FlexyClassLoader classLoader) {
        return MODULE_LOADERS.containsValue(classLoader);
    }

    public static void clear() {
        MODULE_LOADERS.clear();
    }
}
