package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.kevoree.kcl.api.FlexyClassLoader;
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.loader.util.metadata.MetadataUtil;
import org.orienteer.core.loader.util.metadata.OModuleMetadata;
import org.orienteer.core.service.OrienteerFilter;
import org.orienteer.core.service.Reload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy Gonchar
 */
public abstract class OrienteerOutsideModules {
    private static final Logger LOG = LoggerFactory.getLogger(OrienteerOutsideModules.class);
    private static final Set<Integer> LOADED_MODULES = Sets.newHashSet();
    @Inject @Named("outside-modules")
    private static Path modulesFolder;
    @Inject
    private static OrienteerOutsideModulesManager manager;

    @Inject
    private static Injector injector;

    private OrienteerOutsideModules() {}

    public static synchronized void registerModules() {
        int i = 0;

        for (OModuleMetadata metadata : getModulesForLoad()) {
            if (LOADED_MODULES.contains(metadata.getId())) continue;
            String className = metadata.getInitializerName();
            if (className != null) {
                FlexyClassLoader moduleLoader = executeInitClass(className, metadata);
                if (moduleLoader != null) {
                    LOG.info("Load module: " + className + " file: " + metadata.getMainArtifact().getFile().getAbsolutePath());
                    OLoaderStorage.addModuleLoader(className, moduleLoader);
                    LOADED_MODULES.add(metadata.getId());
                    i++;
                }
            } else LOG.warn("Cannot found init class for " + metadata.getMainArtifact().getFile().getAbsolutePath());
        }

        if (i > 0) {
            reload();
        }
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

    private static void reload() {
        OrienteerFilter orienteerFilter = injector.getInstance(OrienteerFilter.class);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
        executor.schedule(new Reload(orienteerFilter), 1, TimeUnit.SECONDS);
    }

    private static List<OModuleMetadata> getModulesForLoad() {
        Map<Path, OModuleMetadata> modules = MetadataUtil.readModulesForLoadAsMap();
        List<Path> jars = readModulesInFolder(modulesFolder);

        if (modules.isEmpty()) {
            List<OModuleMetadata> newModules = getModulesMetadata(jars);
            MetadataUtil.createMetadata(newModules);
            return newModules;
        } else {
            List<Path> modulesForWrite = Lists.newArrayList();
            for (Path pathToModule : jars) {
                if (!modules.keySet().contains(pathToModule.toAbsolutePath())) {
                    modulesForWrite.add(pathToModule);
                }
            }
            List<OModuleMetadata> modulesForAdd = getModulesMetadata(modulesForWrite);
            MetadataUtil.addModulesToMetadata(modulesForAdd);
            modulesForAdd.addAll(modules.values());
            return Collections.unmodifiableList(modulesForAdd);
        }
    }

    private static List<OModuleMetadata> getModulesMetadata(List<Path> modules) {
        List<OModuleMetadata> metadata = Lists.newArrayList();
        MavenResolver resolver = injector.getInstance(MavenResolver.class);
        for (Path jarFile : modules) {
            Optional<OModuleMetadata> moduleMetadata = resolver.getModuleMetadata(jarFile);
            if (moduleMetadata.isPresent()) {
                metadata.add(moduleMetadata.get());
            }
        }
        return metadata;
    }

    private static List<Path> readModulesInFolder(Path folder) {
        return JarUtils.readJarsInFolder(folder);
    }


    private static FlexyClassLoader executeInitClass(String className, OModuleMetadata metadata) {
        FlexyClassLoader moduleLoader = null;
        try {
            moduleLoader = manager.registerModule(className, metadata);
        } catch (ClassNotFoundException e) {
            LOG.error("Cannot load class");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return moduleLoader;
    }

}
