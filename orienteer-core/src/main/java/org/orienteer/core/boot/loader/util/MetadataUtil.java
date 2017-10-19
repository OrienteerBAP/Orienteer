package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.util.Args;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utility class for work with contents in metadata.xml
 * Read, write, update metadata.xml
 */
class MetadataUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataUtil.class);

    private final Path metadataPath;

    /**
     * Constructor
     * @param metadataPath {@link Path} of metadata.xml
     * @throws IllegalArgumentException if metadataPath is null
     */
    MetadataUtil(Path metadataPath) {
        Args.notNull(metadataPath, "metadataPath");
        this.metadataPath = metadataPath;
    }

    /**
     * Create metadata.xml with artifacts
     * @param artifacts artifact for write in metadata.xml
     * @throws IllegalArgumentException if artifacts is null
     */
    public void createOArtifactsMetadata(List<OArtifact> artifacts) {
        Args.notNull(artifacts, "artifacts");
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.create(artifacts);
    }
    
    /**
     * Read artifacts from metadata.xml
     * @return list with {@link OArtifact} which are in metadata.xml or empty list when metadata.xml is empty or don't exists
     */
    public List<OArtifact> readOArtifactsAsList() {
        if (!metadataExists()) {
            createOArtifactsMetadata(Collections.<OArtifact>emptyList());
            return Lists.newArrayList();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readAllOoArtifacts();
    }

    /**
     * Read artifacts for load from metadata.xml
     * @return list with {@link OArtifact} for load from metadata.xml or empty list when metadata.xml is empty or don't exists
     */
    public List<OArtifact> readOArtifactsForLoadAsList() {
        if (!metadataExists()) {
            createOArtifactsMetadata(Collections.<OArtifact>emptyList());
            return Lists.newArrayList();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        return reader.readArtifactsForLoad();
    }

    /**
     * Read artifacts for load from metadata.xml as {@link Map<Path, OArtifact>}
     * key {@link Path} path to jar file of artifact
     * value {@link OArtifact} artifact for load from metadata.xml
     * @return {@link Map<Path, OArtifact>} with artifacts for load from metadata.xml
     */
    public Map<Path, OArtifact> readOArtifactsForLoadAsMap() {
        return readOArtifactsAsMap(false, true);
    }

    /**
     * Read artifacts from metadata.xml as {@link Map<Path, OArtifact>}
     * key {@link Path} path to jar file of artifact
     * value {@link OArtifact} artifact from metadata.xml
     * @return {@link Map<Path, OArtifact>} with artifacts from metadata.xml
     */
    public Map<Path, OArtifact> readOArtifactsAsMap() {
        return readOArtifactsAsMap(true, false);
    }

    /**
     * Read artifacts from metadata.xml as {@link Map<Path, OArtifact>}
     * @param all read all artifacts from metadata.xml
     * @param load read only artifacts for load from metadata.xml
     * @return {@link Map<Path, OArtifact>} with artifacts from metadata.xml or empty map when metadata.xml is empty or don't exists
     */
    private Map<Path, OArtifact> readOArtifactsAsMap(boolean all, boolean load) {
        if (!metadataExists()) {
            createOArtifactsMetadata(Collections.<OArtifact>emptyList());
            return Maps.newHashMap();
        }
        OMetadataReader reader = new OMetadataReader(metadataPath);
        List<OArtifact> modules = all ? reader.readAllOoArtifacts() :
                (load ? reader.readArtifactsForLoad() : reader.readAllOoArtifacts());
        Map<Path, OArtifact> result = Maps.newHashMap();
        int id = 0;
        for (OArtifact module : modules) {
            if (module.getArtifactReference().getFile() == null) {
                result.put(Paths.get(OrienteerClassLoaderUtil.WITHOUT_JAR + id), module);
                id++;
            } else result.put(module.getArtifactReference().getFile().toPath(), module);
        }
        return result;
    }

    /**
     * Update metadata.xml. Create new oArtifact in metadata.xml or update load or trusted of oArtifact.
     * @param oArtifact - {@link OArtifact} for update
     * @throws IllegalArgumentException if oArtifact is null.
     */
    public void updateOArtifactMetadata(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        if (!metadataExists()) {
            createOArtifactsMetadata(Lists.newArrayList(oArtifact));
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(oArtifact);
        }
    }

    /**
     * Update metadata.xml.
     * Search artifactForUpdate in metadata.xml and replace by newArtifact.
     * @param artifactForUpdate {@link OArtifact} for replace.
     * @param newArtifact new {@link OArtifact}.
     * @throws IllegalArgumentException if artifactForUpdate or newArtifact is null.
     */
    public void updateOArtifactMetadata(OArtifact artifactForUpdate, OArtifact newArtifact) {
        Args.notNull(artifactForUpdate, "artifactForUpdate");
        Args.notNull(newArtifact, "newArtifactConfig");
        if (!metadataExists()) {
            return;
        }

        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.update(artifactForUpdate, newArtifact);
    }

    /**
     * Update metadata.xml.
     * Add oArtifacts to metadata.xml or change load or trusted in metadata.xml.
     * @param oArtifacts list of {@link OArtifact} for update
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public void updateOArtifactsMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        if (!metadataExists()) {
            createOArtifactsMetadata(oArtifacts);
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(oArtifacts);
        }
    }

    /**
     * Update jar files for oArtifacts in metadata.xml
     * @param oArtifacts list of {@link OArtifact} for update
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public void updateJarsInOArtifactsMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        if (!metadataExists()) {
            createOArtifactsMetadata(oArtifacts);
        } else {
            OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
            updater.update(oArtifacts, true);
        }
    }

    /**
     * Delete metadata.xml if it's exists.
     */
    public void deleteMetadata() {
        try {
            Files.deleteIfExists(metadataPath);
        } catch (IOException e) {
            LOG.warn("File metadata.xml does not exists.");
            if (LOG.isDebugEnabled()) LOG.debug(e.getMessage(), e);
        }
    }

    /**
     * Delete oArtifacts from metadata.xml
     * @param oArtifacts list of {@link OArtifact} for delete from metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null.
     */
    public void deleteOArtifactsFromMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.delete(oArtifacts);
    }

    /**
     * Delete oArtifact from metadata.xml
     * @param oArtifact {@link OArtifact} for delete
     * @throws IllegalArgumentException if oArtifact is null
     */
    public void deleteOArtifactFromMetadata(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        if (!metadataExists()) return;
        OMetadataUpdater updater = new OMetadataUpdater(metadataPath);
        updater.delete(oArtifact);
    }

    /**
     * Checks if metadata.xml exists
     * @return true - if metadata.xml exists
     *         false - if metadata.xml does not exists
     */
    public boolean metadataExists() {
        return Files.exists(metadataPath);
    }
}
