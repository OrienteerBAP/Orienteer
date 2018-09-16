package org.orienteer.core.boot.loader.distributed;

import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

import java.io.Serializable;
import java.util.Set;

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
        OrienteerClassLoaderUtil.deleteOArtifactsFromMetadata(artifacts);
    }
}
