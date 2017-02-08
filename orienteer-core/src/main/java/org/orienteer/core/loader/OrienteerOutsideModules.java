package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.kevoree.kcl.api.FlexyClassLoader;
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.service.OrienteerFilter;
import org.orienteer.core.service.Reload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy Gonchar
 */
public abstract class OrienteerOutsideModules {
    private static final Set<Path> LOADED = Sets.newConcurrentHashSet();
    private static final Logger LOG = LoggerFactory.getLogger(OrienteerOutsideModules.class);

    @Inject @Named("outside-modules")
    private static Path modulesFolder;
    @Inject
    private static OrienteerOutsideModulesManager manager;

    @Inject
    private static Injector injector;

    public static synchronized void registerModules() {
        LOG.info("Start load Orienteer outside modules");
        Set<Path> jarsInFolder = readModulesInFolder(modulesFolder);
        showAlreadyLoaded();
        int i = 0;
        for (Path jarFile : Sets.difference(jarsInFolder, LOADED)) {
            manager.setModulePath(jarFile);
            Optional<String> className = getInitClass(jarFile);
            if (className.isPresent()) {
                FlexyClassLoader moduleLoader = executeInitClass(className.get());
                if (moduleLoader != null) {
                    LOG.info("Load module: " + className.get() + " file: " + jarFile);
                    OLoaderStorage.addModuleLoader(className.get(), moduleLoader);
                    LOADED.add(jarFile);
                    i++;
                }

            } else LOG.warn("Cannot found init class for " + jarFile);
        }
        OLoaderStorage.updateRootLoader();
        LOG.info("End load Orienteer outside modules");
        if (i > 0) {
            reload();
        }
    }

    private static void reload() {
        OrienteerFilter orienteerFilter = injector.getInstance(OrienteerFilter.class);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
        executor.schedule(new Reload(orienteerFilter), 0, TimeUnit.SECONDS);
    }

    public static synchronized void unregisterModule(String moduleName) {
        if (OLoaderStorage.getNamesOfLoadedModules().contains(moduleName)) {
            FlexyClassLoader classLoader = OLoaderStorage.getModuleLoader(moduleName);
            FlexyClassLoader loader = manager.unregisterModule(classLoader);
            if (loader != null) {
                LOG.info("Unregistering module success");
                OLoaderStorage.removeModuleLoader(moduleName);
            }
        } else LOG.warn(String.format("Module with name %s is not loaded", moduleName));
    }

    private static void showAlreadyLoaded() {
        Iterator<Path> iterator = LOADED.iterator();
        for (String module : OLoaderStorage.getNamesOfLoadedModules()) {
            String file = iterator.hasNext() ? iterator.next().toString() : "";
            LOG.info("Already loaded: " + module + " file: " + file);
        }
    }

    private static String getModuleName(String initClassName) {
        String[] split = initClassName.split("\\.");
        return split[split.length - 2];
    }
    private static Set<Path> readModulesInFolder(Path folder) {
        return JarUtils.readJarsInFolder(folder);
    }

    private static Optional<String> getInitClass(Path jar) {
        return JarUtils.searchOrienteerInitModule(jar);
    }

    private static FlexyClassLoader executeInitClass(String className) {
        FlexyClassLoader moduleLoader = null;
        try {
            moduleLoader = manager.registerModule(className);
        } catch (ClassNotFoundException e) {
            LOG.error("Cannot load class");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return moduleLoader;
    }

}
