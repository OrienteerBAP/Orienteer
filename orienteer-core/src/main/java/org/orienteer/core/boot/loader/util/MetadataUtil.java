package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
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

    public void createOModulesConfigurationsMetadata(List<OArtifact> modules) {
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.create(modules);
    }

    public List<OArtifact> readOModulesConfigurationsAsList() {
        if (!metadataExists()) {
            createOModulesConfigurationsMetadata(Collections.<OArtifact>emptyList());
            return Lists.newArrayList();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readAllOModulesConfigurations();
    }

    public List<OArtifact> readOModulesConfigurationsForLoadAsList() {
        if (!metadataExists()) {
            createOModulesConfigurationsMetadata(Collections.<OArtifact>emptyList());
            return Lists.newArrayList();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readModulesForLoad();
    }

    public Map<Path, OArtifact> readOModulesConfigurationsForLoadAsMap() {
        return readOModulesConfigurationsAsMap(false, true);
    }

    public Map<Path, OArtifact> readOModulesConfigurationsAsMap() {
        return readOModulesConfigurationsAsMap(true, false);
    }

    private Map<Path, OArtifact> readOModulesConfigurationsAsMap(boolean all, boolean load) {
        if (!metadataExists()) {
            createOModulesConfigurationsMetadata(Collections.<OArtifact>emptyList());
            return Maps.newHashMap();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        List<OArtifact> modules = all ? reader.readAllOModulesConfigurations() :
                (load ? reader.readModulesForLoad() : reader.readAllOModulesConfigurations());
        Map<Path, OArtifact> result = Maps.newHashMap();
        int id = 0;
        for (OArtifact module : modules) {
            if (module.getArtifact().getFile() == null) {
                result.put(Paths.get(OrienteerClassLoaderUtil.WITHOUT_JAR + id), module);
                id++;
            } else result.put(module.getArtifact().getFile().toPath(), module);
        }
        return result;
    }

    public void updateOModulesConfigurationsMetadata(OArtifact moduleConfiguration) {
        if (!metadataExists()) {
            createOModulesConfigurationsMetadata(Lists.newArrayList(moduleConfiguration));
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(moduleConfiguration);
            updateModifiedTime();
        }
    }

    public void updateOModulesConfigurationsMetadata(OArtifact moduleConfigForUpdate, OArtifact newModuleConfig) {
        if (!metadataExists()) {
            return;
        }

        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.update(moduleConfigForUpdate, newModuleConfig);
        updateModifiedTime();
    }

    public void updateOModulesConfigurationsMetadata(List<OArtifact> moduleConfigurations) {
        if (!metadataExists()) {
            createOModulesConfigurationsMetadata(moduleConfigurations);
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(moduleConfigurations);
            updateModifiedTime();
        }
    }

    public void updateJarsInOModulesConfigurationsMetadata(List<OArtifact> moduleConfigurations) {
        if (!metadataExists()) {
            createOModulesConfigurationsMetadata(moduleConfigurations);
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(moduleConfigurations, true);
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

    public void deleteOModulesConfigurationsFromMetadata(List<OArtifact> moduleConfigurations) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.delete(moduleConfigurations);
        updateModifiedTime();
    }

    public void deleteOModuleConfigurationFromMetadata(OArtifact moduleConfiguration) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.delete(moduleConfiguration);
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
