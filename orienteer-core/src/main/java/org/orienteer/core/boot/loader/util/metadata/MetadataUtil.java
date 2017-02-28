package org.orienteer.core.boot.loader.util.metadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.InitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
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
public abstract class MetadataUtil {
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
    private static FileTime lastModified;

    private static final Path METADATA_PATH = InitUtils.getMetadataPath();

    private MetadataUtil() {}

    public static void createMetadata(List<OModuleMetadata> modules) {
        OMetadataUpdater updater = new OMetadataUpdater(METADATA_PATH);
        updater.create(modules);
    }

    public static List<OModuleMetadata> readMetadata() {
        if (!metadataExists()) return Lists.newArrayList();
        OMetadataReader reader = new OMetadataReader(METADATA_PATH);
        return reader.readAllModules();
    }

    public static List<OModuleMetadata> readMetadataForLoad() {
        if (!metadataExists()) return Lists.newArrayList();
        OMetadataReader reader = new OMetadataReader(METADATA_PATH);
        return reader.readModulesForLoad();
    }

    public static Map<Path, OModuleMetadata> readModulesForLoadAsMap() {
        return readModulesAsMap(false, true);
    }

    public static Map<Path, OModuleMetadata> readModulesAsMap() {
        return readModulesAsMap(true, false);
    }

    private static Map<Path, OModuleMetadata> readModulesAsMap(boolean all, boolean load) {
        if (!metadataExists()) return Maps.newHashMap();
        OMetadataReader reader = new OMetadataReader(METADATA_PATH);
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
    public static List<Path> readLoadedOnResourcesInMetadata() {
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

    public static void updateMetadata(OModuleMetadata moduleMetadata) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(METADATA_PATH);
        updater.update(moduleMetadata);
        updateModifiedTime();
    }

    public static void updateMetadata(List<OModuleMetadata> modules) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(METADATA_PATH);
        updater.update(modules);
        updateModifiedTime();
    }

    public static void addModulesToMetadata(List<OModuleMetadata> modules) {
        updateMetadata(modules);
    }

    public static void deleteMetadata() {
        try {
            Files.deleteIfExists(METADATA_PATH);
        } catch (IOException e) {
            LOG.warn("File metadata.xml does not exists.");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    public static void deleteModulesFromMetadata(List<OModuleMetadata> modules) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(METADATA_PATH);
        updater.delete(modules);
        updateModifiedTime();
    }

    public static void deleteModuleFromMetadata(OModuleMetadata module) {
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(METADATA_PATH);
        updater.delete(module);
        updateModifiedTime();
    }

    public static boolean isMetadataModify() {
        boolean isModify = false;
        try {
            FileTime modifiedTime = Files.getLastModifiedTime(METADATA_PATH);
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

    private static void updateModifiedTime() {
        try {
            lastModified = Files.getLastModifiedTime(METADATA_PATH);
        } catch (IOException e) {
            LOG.error("Cannot get last modified time ", e);
        }
    }

    private static boolean metadataExists() {
        return Files.exists(METADATA_PATH);
    }
}
