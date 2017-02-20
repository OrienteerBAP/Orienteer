package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.loader.util.metadata.MetadataUtil;
import org.orienteer.core.loader.util.metadata.OModuleMetadata;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.service.OrienteerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public final class OrienteerOutsideModules {
    private static final List<OModuleMetadata> ERRORS = Lists.newArrayList();
    @Inject @Named("outside-modules")
    private Path modulesFolder;

    @Inject
    private OrienteerOutsideModulesManager manager;

    @Inject @Named("dependencies-from-pom-xml")
    private Boolean dependenciesFromPomXml;

    @Inject
    private MavenResolver resolver;
    private List<Path> modulesForUnregister;
    private boolean refleshMetadataXml = false;

    private static boolean reload = false;

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerOutsideModules.class);

    public synchronized void init() {
        if (reload) {
            reload = false;
            LOG.info("End loading Orienteer outside modules.");
            if (LOG.isDebugEnabled()) printOrienteerModules();
            return;
        }
        if (refleshMetadataXml) {
            MetadataUtil.deleteMetadata();
        }
        registerModules();
        unregisterModules();

        if (reload) {
            OrienteerFilter.reloadOrienteer(1);
        }
    }

    private void registerModules() {
        List<OModuleMetadata> modules = getModulesForLoad();
        List<OModuleMetadata> trustyModules = getTrustyModules(modules);
        if (trustyModules.size() > 0) {
            manager.registerModules(trustyModules, true);
        }
    }

    private List<OModuleMetadata> getTrustyModules(List<OModuleMetadata> modules) {
        List<OModuleMetadata> trustyModules = Lists.newArrayList();
        for (OModuleMetadata module : modules) {
            if (ERRORS.contains(module)) {
                continue;
            }
            boolean moduleLoad = manager.registerModule(module);
            if (moduleLoad) {
                trustyModules.add(module);
            } else {
                ERRORS.add(module);
                OLoaderStorage.deleteSandboxModuleLoader();
                manager.registerModules(trustyModules);
                LOG.warn("Cannot load module: " + module);
            }
        }
        OLoaderStorage.deleteSandboxModuleLoader();
        return trustyModules;
    }

    private void unregisterModules() {
        List<OModuleMetadata> modulesForTurnOff = modulesForUnregister != null ?
                getResolvedModulesMetadata(modulesForUnregister) : Lists.<OModuleMetadata>newArrayList();
        modulesForTurnOff.addAll(ERRORS);
        boolean needForReload = turnOffModules(modulesForTurnOff);
        if (!reload) reload = needForReload;
    }

    private List<OModuleMetadata> getModulesForLoad() {
        Map<Path, OModuleMetadata> modules = MetadataUtil.readModulesAsMap();
        boolean metadataModify = MetadataUtil.isMetadataModify();
        List<Path> jars = JarUtils.readJarsInFolder(modulesFolder);

        if (jars.isEmpty() && modules.isEmpty()) return Lists.newArrayList();

        List<OModuleMetadata> modulesForLoad;
        if (modules.isEmpty()) {
            modulesForLoad = getNewModules(jars);
            reload = modulesForLoad.size() > 0;
        } else {
            modulesForLoad = getUpdateModules(jars, modules);
            if (metadataModify) {
                reload = true;
            }
        }
        if (reload) {
            OLoaderStorage.clear();
        }

        return modulesForLoad;
    }

    private List<OModuleMetadata> getNewModules(List<Path> jars) {
        List<OModuleMetadata> modulesForLoad = getResolvedModulesMetadata(jars);
        if (modulesForLoad.size() > 0) {
            MetadataUtil.createMetadata(modulesForLoad);
        } else MetadataUtil.deleteMetadata();

        return modulesForLoad;
    }

    private List<OModuleMetadata> getUpdateModules(List<Path> jars, Map<Path, OModuleMetadata> modules) {
        List<OModuleMetadata> modulesForLoad = Lists.newArrayList();
        List<OModuleMetadata> modulesForWrite = getModulesForAddToMetadata(jars, modules);
        List<OModuleMetadata> modulesForDelete = getModulesForDelete(jars, modules);
        if (modulesForDelete.size() > 0) {
            reload = true;
            if (modulesForDelete.size() == modules.values().size()) {
                MetadataUtil.deleteMetadata();
            } else {
                MetadataUtil.deleteModulesFromMetadata(modulesForDelete);
            }
        }
        if (modulesForWrite.size() > 0) {
            reload = true;
            MetadataUtil.addModulesToMetadata(modulesForWrite);
        }

        modules = MetadataUtil.readModulesAsMap();
        modulesForLoad.addAll(getModulesForLoad(modules.values()));
        return modulesForLoad;
    }

    private List<OModuleMetadata> getModulesForAddToMetadata(List<Path> jars, Map<Path, OModuleMetadata> modules) {
        List<Path> modulesForWrite = Lists.newArrayList();
        Set<Path> jarsInMetadata = modules.keySet();
        for (Path pathToJar : jars) {
            if (!jarsInMetadata.contains(pathToJar)) {
                modulesForWrite.add(pathToJar);
            }
        }
        return getResolvedModulesMetadata(modulesForWrite);
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

    private List<OModuleMetadata> getResolvedModulesMetadata(List<Path> modules) {
        List<OModuleMetadata> metadata = Lists.newArrayList();
        for (Path jarFile : modules) {
            Optional<OModuleMetadata> moduleMetadata = resolver.getModuleMetadata(jarFile, dependenciesFromPomXml);
            if (moduleMetadata.isPresent()) {
                metadata.add(moduleMetadata.get());
            }
        }
        return metadata;
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

    public List<OModuleMetadata> getLoadErrors() {
        List<OModuleMetadata> errors = Collections.unmodifiableList(ERRORS);
        ERRORS.clear();
        return errors;
    }

    public OrienteerOutsideModules setModulesForUnregister(List<Path> modulesForUnregister) {
        this.modulesForUnregister = modulesForUnregister;
        return this;
    }

    public OrienteerOutsideModules setRefleshMetadataXml() {
        refleshMetadataXml = true;
        return this;
    }

    private void printOrienteerModules() {
        OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
        if (app != null) {
            for (IOrienteerModule module : app.getRegisteredModules()) {
                LOG.info("registered module: " + module.getName());
            }
        }
    }
}
