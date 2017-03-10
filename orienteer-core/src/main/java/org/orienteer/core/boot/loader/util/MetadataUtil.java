package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
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

    public void createMetadata(List<OModuleMetadata> modules) {
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.create(modules);
    }

    public List<OModuleMetadata> readMetadata() {
        if (!metadataExists()) return Lists.newArrayList();
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readAllModules();
    }

    public List<OModuleMetadata> readMetadataForLoad() {
        if (!metadataExists()) return Lists.newArrayList();
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readModulesForLoad();
    }

    public Map<Path, OModuleMetadata> readModulesForLoadAsMap() {
        return readModulesAsMap(false, true);
    }

    public Map<Path, OModuleMetadata> readModulesAsMap() {
        return readModulesAsMap(true, false);
    }

    private Map<Path, OModuleMetadata> readModulesAsMap(boolean all, boolean load) {
        if (!metadataExists()) return Maps.newHashMap();
        OMetadataReader reader = new OMetadataReader(metadataPath);
        List<OModuleMetadata> modules = all ? reader.readAllModules() :
                (load ? reader.readModulesForLoad() : reader.readAllModules());
        Map<Path, OModuleMetadata> result = Maps.newHashMap();
        int id = 0;
        for (OModuleMetadata module : modules) {
            if (module.getMainArtifact().getFile() == null) {
                result.put(Paths.get(OrienteerClassLoaderUtil.WITHOUT_JAR + id), module);
                id++;
            } else result.put(module.getMainArtifact().getFile().toPath(), module);
        }
        return result;
    }

    public void updateMetadata(OModuleMetadata moduleMetadata) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.update(moduleMetadata);
        updateModifiedTime();
    }

    public void updateMetadata(List<OModuleMetadata> modules) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.update(modules);
        updateModifiedTime();
    }

    public void updateJarsInMetadata(List<OModuleMetadata> modules) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.update(modules, true);
        updateModifiedTime();
    }

    public void deleteMetadata() {
        try {
            Files.deleteIfExists(metadataPath);
        } catch (IOException e) {
            LOG.warn("File metadata.xml does not exists.");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    public void deleteModulesFromMetadata(List<OModuleMetadata> modules) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.delete(modules);
        updateModifiedTime();
    }

    public void deleteModuleFromMetadata(OModuleMetadata module) {
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
