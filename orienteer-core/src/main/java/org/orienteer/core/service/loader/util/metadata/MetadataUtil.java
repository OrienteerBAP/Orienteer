package org.orienteer.core.service.loader.util.metadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.service.loader.util.InitUtils;
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
        OModuleUpdater.OModuleUpdaterBuilder builder = new OModuleUpdater.OModuleUpdaterBuilder(METADATA_PATH);
        try {
            builder.setCreateNewMetadata(modules)
                    .build().write();
            updateModifiedTime();
        } catch (IOException | XMLStreamException e) {
            LOG.error("Cannot write to metadata.xml");
            if(LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    public static List<OModuleMetadata> readMetadata() {
        OModuleReader.OModuleReaderBuilder builder = new OModuleReader.OModuleReaderBuilder(METADATA_PATH);
        OModuleReader reader = builder.setAllModules(true).build();
        return reader.read();
    }

    public static List<OModuleMetadata> readMetadataForLoad() {
        OModuleReader.OModuleReaderBuilder builder = new OModuleReader.OModuleReaderBuilder(METADATA_PATH);
        OModuleReader reader = builder.setLoad(true).build();
        return reader != null ? reader.read() : Lists.<OModuleMetadata>newArrayList();
    }

    public static Map<Path, OModuleMetadata> readModulesForLoadAsMap() {
        return readModulesAsMap(false, true);
    }

    public static Map<Path, OModuleMetadata> readModulesAsMap() {
        return readModulesAsMap(true, false);
    }

    private static Map<Path, OModuleMetadata> readModulesAsMap(boolean all, boolean load) {
        if (!Files.exists(METADATA_PATH)) return Maps.newHashMap();

        OModuleReader.OModuleReaderBuilder builder = new OModuleReader.OModuleReaderBuilder(METADATA_PATH);
        OModuleReader reader = builder
                .setAllModules(all)
                .setLoad(load)
                .build();
        List<OModuleMetadata> modules = reader != null ? reader.read() : Lists.<OModuleMetadata>newArrayList();
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
        OModuleReader.OModuleReaderBuilder builder = new OModuleReader.OModuleReaderBuilder(METADATA_PATH);
        OModuleReader reader = builder.setLoad(true).build();
        List<Path> resources = Lists.newArrayList();

        List<OModuleMetadata> loadedModules = reader.read();
        for (OModuleMetadata module : loadedModules) {
            resources.add(module.getMainArtifact().getFile().toPath());
            for (Artifact dependency : module.getDependencies()) {
                resources.add(dependency.getFile().toPath());
            }
        }

        return resources;
    }

    /**
     * Read only modules with load=false.
     * @return load modules jars and their resources.
     */
    public static List<Path> readLoadedOffResourcesInMetadata() {
        OModuleReader.OModuleReaderBuilder builder = new OModuleReader.OModuleReaderBuilder(METADATA_PATH);
        OModuleReader reader = builder.setLoad(false).build();
        List<Path> resources = Lists.newArrayList();

        List<OModuleMetadata> loadedModules = reader.read();
        for (OModuleMetadata module : loadedModules) {
            resources.add(module.getMainArtifact().getFile().toPath());
            for (Artifact dependency : module.getDependencies()) {
                resources.add(dependency.getFile().toPath());
            }
        }

        return resources;
    }

    public static void updateMetadata(OModuleMetadata moduleMetadata) {
        OModuleUpdater.OModuleUpdaterBuilder builder = new OModuleUpdater.OModuleUpdaterBuilder(METADATA_PATH);
        List<OModuleMetadata> modules = Lists.newArrayList();
        modules.add(moduleMetadata);
        try {
            builder.setOverwriteExistsModulesInMetadata(modules)
                    .build().write();
            updateModifiedTime();
        } catch (IOException | XMLStreamException e) {
            LOG.error("Cannot write to metadata.xml");
            if(LOG.isDebugEnabled()) e.printStackTrace();
        }

    }

    public static void updateMetadata(List<OModuleMetadata> modules) {
        OModuleUpdater.OModuleUpdaterBuilder builder = new OModuleUpdater.OModuleUpdaterBuilder(METADATA_PATH);
        try {
            builder.setOverwriteExistsModulesInMetadata(modules)
                    .build()
                    .write();
            updateModifiedTime();
        } catch (IOException | XMLStreamException e) {
            LOG.error("Cannot write to metadata.xml");
            if(LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    public static void addModulesToMetadata(List<OModuleMetadata> modules) {
        OModuleUpdater.OModuleUpdaterBuilder builder = new OModuleUpdater.OModuleUpdaterBuilder(METADATA_PATH);
        try {
            builder.setAddModulesToExistsMetadata(modules)
                    .build()
                    .write();
            updateModifiedTime();
        } catch (IOException | XMLStreamException e) {
            LOG.error("Cannot write to metadata.xml");
            if(LOG.isDebugEnabled()) e.printStackTrace();
        }
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
        OModuleUpdater.OModuleUpdaterBuilder builder = new OModuleUpdater.OModuleUpdaterBuilder(METADATA_PATH);
        try {
            builder.setDelete(modules)
                    .build()
                    .write();
            updateModifiedTime();
        } catch (IOException | XMLStreamException e) {
            LOG.error("Cannot write to metadata.xml");
            if(LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    public static void deleteModuleFromMetadata(OModuleMetadata moduleMetadata) {
        OModuleUpdater.OModuleUpdaterBuilder builder = new OModuleUpdater.OModuleUpdaterBuilder(METADATA_PATH);
        List<OModuleMetadata> modulesToDelete = Lists.newArrayList();
        modulesToDelete.add(moduleMetadata);
        try {
            builder.setDelete(modulesToDelete)
                    .build()
                    .write();
            updateModifiedTime();
        } catch (IOException | XMLStreamException e) {
            LOG.error("Cannot write to metadata.xml");
            if(LOG.isDebugEnabled()) e.printStackTrace();
        }
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

    private static void updateModifiedTime() throws IOException {
        lastModified = Files.getLastModifiedTime(METADATA_PATH);
    }
}
