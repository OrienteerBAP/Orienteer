package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.loader.util.metadata.MetadataUtil;
import org.orienteer.core.loader.util.metadata.OModuleMetadata;
import org.orienteer.core.service.OrienteerFilter;
import org.orienteer.core.service.Reload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy Gonchar
 */
public abstract class OrienteerOutsideModules {
    private static final Set<Integer> LOADED_MODULES       = Sets.newHashSet();
    private static final List<OModuleMetadata> LOAD_ERRORS = Lists.newArrayList();

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerOutsideModules.class);

    @Inject @Named("outside-modules")
    private static Path modulesFolder;
    @Inject
    private static OrienteerOutsideModulesManager manager;

    @Inject
    private static Injector injector;

    private OrienteerOutsideModules() {}

    public static synchronized void registerModules() {
        int i = 0;

        for (OModuleMetadata module : getModulesForLoad()) {
            if (LOADED_MODULES.contains(module.getId()) || LOAD_ERRORS.contains(module)) {
                continue;
            }

            boolean moduleLoad = manager.registerModule(module);
            if (moduleLoad) {
                LOG.info("Load module: " + module.getInitializerName()
                        + " file: " + module.getMainArtifact().getFile().getAbsolutePath());
                LOADED_MODULES.add(module.getId());
                i++;
            } else {
                module.setTrusted(false);
                LOAD_ERRORS.add(module);
                LOG.warn("Cannot load module: " + module);
            }
        }

        if (i > 0) {
            reload();
        } else {
            turnOffModules(LOAD_ERRORS);
            LOG.info("End load Orienteer outside modules.");
        }
    }

    public static synchronized void unregisterModule(String moduleName) {
//        if (OLoaderStorage.getNamesOfLoadedModules().contains(moduleName)) {
//            FlexyClassLoader classLoader = OLoaderStorage.getModuleLoader(moduleName);
//            FlexyClassLoader loader = manager.unregisterModule(classLoader);
//            if (loader != null) {
//                LOG.info("Unregistering module success");
//                OLoaderStorage.removeModuleLoader(moduleName);
//            }
//        } else LOG.warn(String.format("Module with name %s is not loaded", moduleName));
    }

    private static void reload() {
        OrienteerFilter orienteerFilter = injector.getInstance(OrienteerFilter.class);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.schedule(new Reload(orienteerFilter), 1, TimeUnit.SECONDS);
    }

    private static List<OModuleMetadata> getModulesForLoad() {
        Map<Path, OModuleMetadata> modules = MetadataUtil.readModulesForLoadAsMap();
        List<Path> jars = JarUtils.readJarsInFolder(modulesFolder);
        if (jars.isEmpty() && modules.isEmpty()) {
            return Lists.newArrayList();
        } if (modules.isEmpty()) {
            LOADED_MODULES.clear();
            OLoaderStorage.clear();
            List<OModuleMetadata> newModules = getModulesMetadata(jars);
            MetadataUtil.createMetadata(newModules);
            return newModules;
        } else {
            List<OModuleMetadata> modulesForWrite = getModulesForWrite(jars, modules.keySet());
            List<OModuleMetadata> modulesForDelete = getModulesForDelete(jars, modules);
            MetadataUtil.deleteMetadata(modulesForDelete);
            MetadataUtil.addModulesToMetadata(modulesForWrite);
            modulesForWrite.addAll(modules.values());
            return Collections.unmodifiableList(modulesForWrite);
        }
    }

    private static List<OModuleMetadata> getModulesForWrite(List<Path> jars, Set<Path> jarsInMetadata) {
        List<Path> modulesForWrite = Lists.newArrayList();
        for (Path pathToJar : jars) {
            if (!jarsInMetadata.contains(pathToJar)) {
                modulesForWrite.add(pathToJar);
            }
        }
        return getModulesMetadata(modulesForWrite);
    }

    private static List<OModuleMetadata> getModulesForDelete(List<Path> jars, Map<Path, OModuleMetadata> modules) {
        List<OModuleMetadata> modulesForDelete = Lists.newArrayList();
        for (Path path : modules.keySet()) {
            if (!jars.contains(path)) {
                modulesForDelete.add(modules.get(path));
            }
        }
        return modulesForDelete;
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

    private static void turnOffModules(List<OModuleMetadata> modules) {
        List<OModuleMetadata> modulesForWrite = Lists.newArrayList();
        for (OModuleMetadata module : modules) {
            if (module.isLoad()) {
                module.setLoad(false);
                modulesForWrite.add(module);
            }
        }
        MetadataUtil.updateMetadata(modulesForWrite);
    }

    public static List<OModuleMetadata> getLoadErrors() {
        List<OModuleMetadata> errors = Collections.unmodifiableList(LOAD_ERRORS);
        LOAD_ERRORS.clear();
        return errors;
    }
}
