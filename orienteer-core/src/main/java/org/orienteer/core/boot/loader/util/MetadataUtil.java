package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 * Utility class for wotk with contents in metadata.xml
 */
class MetadataUtil {
    static final String METADATA        = "metadata";
    static final String MODULE          = "module";
    static final String ID              = "id";
    static final String LOAD            = "load";
    static final String INITIALIZER     = "initializer";
    static final String MAVEN           = "maven";
    static final String GROUP_ID        = "groupId";
    static final String ARTIFACT_ID     = "artifactId";
    static final String VERSION         = "version";
    static final String JAR             = "jar";
    static final String JARS            = "jars";

    static final String LOAD_DEFAULT    = "true";
    static final String TRUSTED_DEFAULT = "false";

    static final String MAIN_DEPENDENCY = "mainDependency";
    static final String DEPENDENCY    = "dependency";
    static final String DEPENDENCIES  = "dependencies";

    static final int TWO_SPACES    = 2;
    static final int FOUR_SPACES   = 4;
    static final int SIX_SPACES    = 6;
    static final int EIGHT_SPACES  = 8;
    static final int TEN_SPACES    = 10;
    static final int TWELVE_SPACES = 12;

    static final String METADATA_TEMP = "metadata-temp.xml";

    private static final Logger LOG = LoggerFactory.getLogger(MetadataUtil.class);
    private FileTime lastModified;

    private final Path metadataPath;

    MetadataUtil(Path metadataPath) {
        this.metadataPath = metadataPath;
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
        for (OModuleMetadata module : modules) {
            result.put(module.getMainArtifact().getFile().toPath(), module);
        }
        return result;
    }

    /**
     * Read only modules with load=true.
     * @return load modules jars and their resources.
     */
    public List<Path> readLoadedOnResourcesInMetadata() {
        List<Path> resources = Lists.newArrayList();
        List<OModuleMetadata> loadedModules = readMetadataForLoad();
        for (OModuleMetadata module : loadedModules) {
            resources.add(module.getMainArtifact().getFile().toPath());
            for (Artifact dependency : module.getDependencies()) {
                resources.add(dependency.getFile().toPath());
            }
        }

        return resources;
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

    public void addModulesToMetadata(List<OModuleMetadata> modules) {
        updateMetadata(modules);
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
