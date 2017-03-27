package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 * Utility class for wotk with contents in metadata.xml
 */
class MetadataUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataUtil.class);
    private FileTime lastModified;

    private final Path metadataPath;
    private final Path modulesFolder;

    MetadataUtil(Path metadataPath, Path modulesFolder) {
        this.metadataPath = metadataPath;
        this.modulesFolder = modulesFolder;
    }

    public void createMetadata(List<OModule> modules) {
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.create(modules);
    }

    public List<OModule> readMetadata() {
        if (!metadataExists()) {
            createMetadata(Collections.<OModule>emptyList());
            return Lists.newArrayList();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readAllModules();
    }

    public List<OModule> readMetadataForLoad() {
        if (!metadataExists()) {
            createMetadata(Collections.<OModule>emptyList());
            return Lists.newArrayList();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readModulesForLoad();
    }

    public Map<Path, OModule> readModulesForLoadAsMap() {
        return readModulesAsMap(false, true);
    }

    public Map<Path, OModule> readModulesAsMap() {
        return readModulesAsMap(true, false);
    }

    private Map<Path, OModule> readModulesAsMap(boolean all, boolean load) {
        if (!metadataExists()) {
            createMetadata(Collections.<OModule>emptyList());
            return Maps.newHashMap();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        List<OModule> modules = all ? reader.readAllModules() :
                (load ? reader.readModulesForLoad() : reader.readAllModules());
        Map<Path, OModule> result = Maps.newHashMap();
        int id = 0;
        for (OModule module : modules) {
            if (module.getArtifact().getFile() == null) {
                result.put(Paths.get(OrienteerClassLoaderUtil.WITHOUT_JAR + id), module);
                id++;
            } else result.put(module.getArtifact().getFile().toPath(), module);
        }
        return result;
    }

    public void updateMetadata(OModule moduleMetadata) {
        if (!metadataExists()) {
            createMetadata(Lists.newArrayList(moduleMetadata));
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(moduleMetadata);
            updateModifiedTime();
        }
    }

    public void updateMetadata(OModule moduleForUpdate, OModule newModule) {
        if (!metadataExists()) {
            return;
        }

        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.update(moduleForUpdate, newModule);
        updateModifiedTime();
    }

    public void updateMetadata(List<OModule> modules) {
        if (!metadataExists()) {
            createMetadata(modules);
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(modules);
            updateModifiedTime();
        }
    }

    public void updateJarsInMetadata(List<OModule> modules) {
        if (!metadataExists()) {
            createMetadata(modules);
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(modules, true);
            updateModifiedTime();
        }
    }

    public void deleteMetadata() {
        try {
            Files.deleteIfExists(metadataPath);
        } catch (IOException e) {
            LOG.warn("File metadata.xml does not exists.");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    public void deleteModulesFromMetadata(List<OModule> modules) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.delete(modules);
        updateModifiedTime();
    }

    public void deleteModuleFromMetadata(OModule module) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.delete(module);
        updateModifiedTime();
    }

    public boolean isMetadataModify() {
        boolean isModify = false;
        try {
            FileTime modifiedTime = Files.getLastModifiedTime(metadataPath);
            if (lastModified == null || !lastModified.equals(modifiedTime)) {
                isModify = true;
                lastModified = modifiedTime;
            }
        } catch (IOException e) {
            LOG.warn("File metadata.xml does not exists.");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return isModify;
    }

    private void updateModifiedTime() {
        try {
            lastModified = Files.getLastModifiedTime(metadataPath);
        } catch (IOException e) {
            LOG.error("Cannot get last modified time ", e);
        }
    }

    private boolean metadataExists() {
        return Files.exists(metadataPath);
    }
}
