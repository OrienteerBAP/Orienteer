package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.kevoree.kcl.api.FlexyClassLoader;
import org.orienteer.core.loader.util.JarReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

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

    public static synchronized void registerModules() {
        LOG.info("Start load Orienteer outside modules");
        Set<Path> jarsInFolder = readModulesInFolder(modulesFolder);
        showAlreadyLoaded();
        for (Path jarFile : Sets.difference(jarsInFolder, LOADED)) {
            manager.setModulePath(jarFile);
            Optional<String> className = getInitClass(jarFile);
            if (className.isPresent()) {
                FlexyClassLoader moduleLoader = executeInitClass(className.get());
                if (moduleLoader != null) {
                    LOG.info("Load module: " + className.get() + " file: " + jarFile);
                    OLoaderStorage.addModuleLoader(className.get(), moduleLoader);
                    LOADED.add(jarFile);
                }

            } else LOG.warn("Cannot found init class for " + jarFile);
        }
        OLoaderStorage.updateRootLoader();
        LOG.info("End load Orienteer outside modules");
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
        return JarReader.readJarsInFolder(folder);
    }

    private static Optional<String> getInitClass(Path jar) {
        return JarReader.searchOrienteerInitModule(jar);
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
