package org.orienteer.core.boot.loader.distributed;

import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Delete Orienteer module task
 */
public class DeleteMetadataTask implements Runnable, Serializable {

    private final Set<OArtifact> artifacts;

    public DeleteMetadataTask(Set<OArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    @Override
    public void run() {
        InternalOModuleManager manager = InternalOModuleManager.get();
        Set<OArtifact> metadataArtifacts = manager.getOArtifactsMetadataAsSet();
        Set<OArtifact> artifactsForDelete = metadataArtifacts.stream()
                .filter(artifacts::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        manager.deleteOArtifactsFromMetadata(artifactsForDelete);
    }
}
