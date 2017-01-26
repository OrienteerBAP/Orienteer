package org.orienteer.loader.module;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
public abstract class JarModules {
    private static final Set<IInitializer> CACHE = new HashSet<>();

    private static final Logger LOG = LoggerFactory.getLogger(JarModules.class);

    public static void addModule(IInitializer initializer) {
        boolean add = CACHE.add(initializer);
        if (add) {
            LOG.info("Add initializer " + initializer + " to cache");
        } else LOG.info("Cannot add initializer " + initializer + " to cache. Initializer is already exists in cache!");
    }

    public static void removeModule(IInitializer initializer) {
        boolean remove = CACHE.remove(initializer);
        if (remove) {
            LOG.info("Remove initializer " + initializer + " from cache");
        } else LOG.warn("Cannot remove initializer " + initializer + " from cache. Initializer does not exists in cache!");
    }

    public static void loadModules(Application app) {
        LOG.debug("Start load jar modules");
        for (IInitializer initializer : CACHE) {
            initializer.init(app);
        }
        LOG.debug("End load jar modules");
    }
}
