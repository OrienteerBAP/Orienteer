package org.orienteer.core.service;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import org.apache.wicket.IInitializer;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.service.loader.MavenResolver;
import org.orienteer.core.service.loader.OClassLoaderStorage;
import org.orienteer.core.service.loader.OrienteerClassLoader;
import org.orienteer.core.service.loader.util.InitUtils;
import org.orienteer.core.service.loader.util.JarUtils;
import org.orienteer.core.service.loader.util.metadata.MetadataUtil;
import org.orienteer.core.service.loader.util.metadata.OModuleMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 * Add Initializer of outside Orienteer modules in classpath when Injector was created.
 */
public class OrienteerOutsideModule extends AbstractModule {
    private final List<OModuleMetadata> errors   = Lists.newArrayList();
    private final Path modulesFolder             = InitUtils.getPathToModulesFolder();
    private final boolean dependenciesFromPomXml = InitUtils.isDependenciesResolveFromPomXml();
    private final MavenResolver resolver         = MavenResolver.get();

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerOutsideModule.class);

    @Override
    protected void configure() {
        initOutsideModules();
        destroyErrors();
        clear();
    }

    /**
     * Initialization of outside Orienteer modules.
     */
    private void initOutsideModules() {
        List<OModuleMetadata> modules = getModulesForLoad();
        List<OModuleMetadata> trustyModules = searchTrustyModules(modules);
        if (modules.size() > 0) {
            registerModules(trustyModules, true);
        }
    }

    /**
     * Turn off all modules, which cannot be load.
     */
    private void destroyErrors() {
        List<OModuleMetadata> modulesForTurnOff = Lists.newArrayList();
        modulesForTurnOff.addAll(errors);
        turnOffModules(modulesForTurnOff);
    }

    /**
     * Search good modules in input modules list.
     * If module cannot be load it add to errors list and will be turn off.
     * @param modules list which contains all modules with load=true in metadata.xml
     * @return modules which can be load without errors.
     */
    private List<OModuleMetadata> searchTrustyModules(List<OModuleMetadata> modules) {
        List<OModuleMetadata> trustyModules = Lists.newArrayList();
        for (OModuleMetadata module : modules) {
            if (errors.contains(module)) {
                continue;
            }
            boolean moduleLoad = registerModule(module, false);

            if (moduleLoad) {
                trustyModules.add(module);
            } else {
                errors.add(module);
                OClassLoaderStorage.deleteSandboxModuleLoader();
                registerModules(trustyModules, false);
                LOG.warn("Cannot load module: " + module);
            }
        }
        OClassLoaderStorage.deleteSandboxModuleLoader();
        return trustyModules;
    }

    private List<OModuleMetadata> getModulesForLoad() {
        Map<Path, OModuleMetadata> modules = MetadataUtil.readModulesAsMap();
        boolean metadataModify = MetadataUtil.isMetadataModify();
        List<Path> jars = JarUtils.readJarsInFolder(modulesFolder);

        if (jars.isEmpty() && modules.isEmpty()) return Lists.newArrayList();

        List<OModuleMetadata> modulesForLoad;
        if (modules.isEmpty()) {
            modulesForLoad = createModules(jars);
        } else {
            List<OModuleMetadata> modulesWithoutDeps = getModulesWithoutDependencies(modules.values());
            if (!resolver.resolveModuleMetadata(modulesWithoutDeps)) {
                modulesForLoad = createModules(jars);

            } else modulesForLoad = getUpdateModules(jars, modules);
        }

        if (metadataModify) {
            OClassLoaderStorage.clear();
        }

        return modulesForLoad;
    }

    private List<OModuleMetadata> createModules(List<Path> jars) {
        List<OModuleMetadata> modulesForLoad = resolver.getResolvedModulesMetadata(jars, dependenciesFromPomXml);
        if (modulesForLoad.size() > 0) {
            MetadataUtil.createMetadata(modulesForLoad);
        } else MetadataUtil.deleteMetadata();

        return modulesForLoad;
    }

    private List<OModuleMetadata> getUpdateModules(List<Path> jars, Map<Path, OModuleMetadata> modules) {
        List<OModuleMetadata> modulesForWrite = getModulesForAddToMetadata(jars, modules);
        List<OModuleMetadata> modulesForDelete = getModulesForDelete(jars, modules);

        if (modulesForDelete.size() > 0) {
            if (modulesForDelete.size() == modules.values().size()) {
                MetadataUtil.deleteMetadata();
            } else {
                MetadataUtil.deleteModulesFromMetadata(modulesForDelete);
            }
        }
        if (modulesForWrite.size() > 0) {
            MetadataUtil.addModulesToMetadata(modulesForWrite);
        }

        modules = MetadataUtil.readModulesAsMap();

        return getModulesForLoad(modules.values());
    }

    private List<OModuleMetadata> getModulesWithoutDependencies(Collection<OModuleMetadata> modules) {
        List<OModuleMetadata> modulesWithoutDependencies = Lists.newArrayList();
        for (OModuleMetadata module : modules) {
            for (Artifact artifact : module.getDependencies()) {
                if (artifact.getFile() == null || !artifact.getFile().exists()) {
                    modulesWithoutDependencies.add(module);
                    break;
                }
            }
        }
        return modulesWithoutDependencies;
    }

    private List<OModuleMetadata> getModulesForAddToMetadata(List<Path> jars, Map<Path, OModuleMetadata> modules) {
        List<Path> modulesForWrite = Lists.newArrayList();
        Set<Path> jarsInMetadata = modules.keySet();
        for (Path pathToJar : jars) {
            if (!jarsInMetadata.contains(pathToJar)) {
                modulesForWrite.add(pathToJar);
            }
        }
        return resolver.getResolvedModulesMetadata(modulesForWrite, dependenciesFromPomXml);
    }

    private List<OModuleMetadata> getModulesForDelete(List<Path> jars, Map<Path, OModuleMetadata> modules) {
        List<OModuleMetadata> modulesForDelete = Lists.newArrayList();
        for (Path path : modules.keySet()) {
            if (!jars.contains(path)) {
                modulesForDelete.add(modules.get(path));
            }
        }
        return modulesForDelete;
    }

    private List<OModuleMetadata> getModulesForLoad(Collection<OModuleMetadata> modules) {
        List<OModuleMetadata> modulesForLoad = Lists.newArrayList();
        for (OModuleMetadata metadata : modules) {
            if (metadata.isLoad()) modulesForLoad.add(metadata);
        }
        return modulesForLoad;
    }

    private boolean turnOffModules(List<OModuleMetadata> modules) {
        int i = 0;
        List<OModuleMetadata> modulesForWrite = Lists.newArrayList();
        for (OModuleMetadata module : modules) {
            if (module.isLoad()) {
                module.setLoad(false);
                modulesForWrite.add(module);
                i++;
            }
        }
        MetadataUtil.updateMetadata(modulesForWrite);
        return i > 0;
    }

    private void registerModules(List<OModuleMetadata> modules, boolean trustyClassLoader) {
        for (OModuleMetadata metadata : modules) {
            registerModule(metadata, trustyClassLoader);
        }
    }

    /**
     * Add class Initializer (which is in outside Orienteer modules) in classpath.
     * @param metadata contains paths to dependencies and full name of Initializer class.
     * @param trustyClassLoader true - use trusty classloader
     *                          false - use sandbox classloader
     * @return true if class Initializer was successful add to classpath
     */
    private boolean registerModule(OModuleMetadata metadata, boolean trustyClassLoader) {
        ClassLoader classLoader = getClassLoader(metadata, trustyClassLoader);
        boolean loadModule = false;
        try {
            Class<? extends IInitializer> loadClass = (Class<? extends IInitializer>)
                    classLoader.loadClass(metadata.getInitializerName());
            loadModule = true;
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return loadModule;
    }

    /**
     * @param metadata contains paths to all dependencies
     * @param trustyClassLoader true - use trusty classloader
     *                          false - use sandbox classloader
     * @return classloader with dependency resources
     */
    private ClassLoader getClassLoader(OModuleMetadata metadata, boolean trustyClassLoader) {
        OrienteerClassLoader classLoader = trustyClassLoader ? OClassLoaderStorage.getTrustyModuleLoader(false) :
                OClassLoaderStorage.getSandboxModuleLoader(false);
        try {
            classLoader.load(metadata.getMainArtifact().getFile());
            for (Artifact dependency : metadata.getDependencies()) {
                classLoader.load(dependency.getFile());
            }
        } catch (MalformedURLException ex) {
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        } catch (IOException ex) {
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return classLoader;
    }

    private void clear() {
        JarUtils.deletePomFolder();
    }

}
