package org.orienteer.core.loader;

import org.kevoree.kcl.api.FlexyClassLoader;
import org.kevoree.kcl.api.FlexyClassLoaderFactory;

/**
 * @author Vitaliy Gonchar
 */
public abstract class OLoaderStorage {

    private static FlexyClassLoader rootLoader;
    private static FlexyClassLoader trustyModuleLoader;
    private static FlexyClassLoader sandboxModuleLoader;

    private OLoaderStorage() {}

    public static synchronized FlexyClassLoader getRootLoader() {
        return rootLoader;
    }

    public static synchronized FlexyClassLoader getTrustyModuleLoader(boolean create) {
        if (trustyModuleLoader == null || create) {
            trustyModuleLoader = getClassLoader();
            rootLoader.attachChild(trustyModuleLoader);
        }
        return trustyModuleLoader;
    }

    public static synchronized FlexyClassLoader getSandboxModuleLoader(boolean create) {
        if (sandboxModuleLoader == null || create) {
            sandboxModuleLoader = getClassLoader();
            getTrustyModuleLoader(false).attachChild(sandboxModuleLoader);
        }
        return sandboxModuleLoader;
    }

    public static synchronized FlexyClassLoader createNewRootLoader() {
        rootLoader = getClassLoader();
        if (trustyModuleLoader != null) rootLoader.attachChild(trustyModuleLoader);
        return rootLoader;
    }

    public static synchronized void clear() {
        deleteSandboxModuleLoader();
        deleteTrustyModuleLoader();
    }

    public static synchronized void deleteSandboxModuleLoader() {
        if (sandboxModuleLoader != null) {
            trustyModuleLoader.detachChild(sandboxModuleLoader);
            sandboxModuleLoader = null;
        }
    }

    private static void deleteTrustyModuleLoader() {
        if (trustyModuleLoader != null) {
            rootLoader.detachChild(trustyModuleLoader);
            trustyModuleLoader = null;
        }
    }

    private static FlexyClassLoader getClassLoader() {
        return FlexyClassLoaderFactory.INSTANCE.create();
    }
}
