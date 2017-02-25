package org.orienteer.core.service.loader;

import org.kevoree.kcl.api.FlexyClassLoader;
import org.kevoree.kcl.api.FlexyClassLoaderFactory;
import org.kevoree.kcl.impl.FlexyClassLoaderImpl;
import org.kevoree.kcl.impl.FlexyClassLoaderWrapper;
import org.orienteer.core.OrienteerWebApplication;

/**
 * @author Vitaliy Gonchar
 */
public abstract class OLoaderStorage {

    private static FlexyClassLoader rootLoader;
    private static FlexyClassLoader trustyModuleLoader;
    private static FlexyClassLoader sandboxModuleLoader;
    private static final ClassLoader ORIENTEER_CLASS_LOADER = OrienteerWebApplication.class.getClassLoader();

    private OLoaderStorage() {}

    public static synchronized ClassLoader getRootLoader() {
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
            rootLoader.attachChild(sandboxModuleLoader);
        }
        return sandboxModuleLoader;
    }

    public static synchronized FlexyClassLoader createNewRootLoader() {
        rootLoader = OrienteerLoader.get(ORIENTEER_CLASS_LOADER);
//        rootLoader = getClassLoader();
        if (sandboxModuleLoader != null) rootLoader.attachChild(sandboxModuleLoader);
        return rootLoader;
    }

    public static synchronized void clear() {
        deleteSandboxModuleLoader();
        deleteTrustyModuleLoader();
    }

    public static synchronized void deleteSandboxModuleLoader() {
        if (sandboxModuleLoader != null) {
            sandboxModuleLoader = null;
            rootLoader.detachChild(sandboxModuleLoader);
        }
    }

    private static void deleteTrustyModuleLoader() {
        if (trustyModuleLoader != null) {
            trustyModuleLoader = null;
            rootLoader.detachChild(trustyModuleLoader);
        }
    }

    private static FlexyClassLoader getClassLoader() {
        return FlexyClassLoaderFactory.INSTANCE.create();
    }

}
