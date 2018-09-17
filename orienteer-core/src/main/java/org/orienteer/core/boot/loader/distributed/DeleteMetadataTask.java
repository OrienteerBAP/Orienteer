package org.orienteer.core.boot.loader.distributed;

import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

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
        Set<OArtifact> metadataArtifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet();
        Set<OArtifact> artifactsForDelete = metadataArtifacts.stream()
                .filter(artifacts::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        OrienteerClassLoaderUtil.deleteOArtifactsFromMetadata(artifactsForDelete);
    }
}
