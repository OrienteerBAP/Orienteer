package org.orienteer.core.boot.loader.service;

import com.google.inject.ImplementedBy;
import org.danekja.java.misc.serializable.SerializableRunnable;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;

import java.util.Map;
import java.util.Set;

/**
 * Interface for manage Orienteer modules
 */
@ImplementedBy(ModuleManager.class)
public interface IModuleManager {

    void addArtifact(OArtifact artifact);
    void addArtifact(OArtifact artifact, SerializableRunnable callback);
    void updateArtifact(OArtifact previous, OArtifact artifact);
    void updateArtifact(OArtifact previous, OArtifact artifact, SerializableRunnable callback);
    void deleteArtifact(OArtifact artifact);
    void deleteArtifact(OArtifact artifact, SerializableRunnable callback);

    void addArtifacts(Set<OArtifact> artifacts);
    void addArtifacts(Set<OArtifact> artifacts, SerializableRunnable callback);
    void updateArtifacts(Map<OArtifact, OArtifact> artifacts);
    void updateArtifacts(Map<OArtifact, OArtifact> artifacts, SerializableRunnable callback);
    void deleteArtifacts(Set<OArtifact> artifacts);
    void deleteArtifacts(Set<OArtifact> artifacts, SerializableRunnable callback);

    void reloadOrienteer();
}
