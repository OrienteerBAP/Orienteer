package org.orienteer.core.boot.loader.internal.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Class which manages Injector for modules micro framework
 */
public final class OModulesStaticInjector {

    private static Injector injector;

    /**
     * Init injector if need
     * @param modules Guice modules
     * @return {@link Injector} injector which was initialized
     */
    public static Injector init(Module...modules) {
        if (injector == null) {
            synchronized (OModulesStaticInjector.class) {
                if (injector == null) {
                    injector = Guice.createInjector(modules);
                }
            }
        }
        return injector;
    }

    /**
     * Destroy injector
     */
    public static void destroy() {
        injector = null;
    }

    /**
     * @return injector for modules micro framework
     * @throws IllegalStateException if injector didn't initialized
     */
    public static Injector getInjector() throws IllegalStateException {
        if (injector == null) {
            throw new IllegalStateException("Injector didn't initialized!");
        }
        return injector;
    }


    private OModulesStaticInjector() {
    }
}
