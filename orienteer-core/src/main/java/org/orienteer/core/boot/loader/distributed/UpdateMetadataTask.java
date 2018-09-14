package org.orienteer.core.boot.loader.distributed;

import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Task for update metadata.xml on node
 */
public class UpdateMetadataTask implements Runnable, Serializable {

    private final Set<OArtifact> artifacts;

    public UpdateMetadataTask(Set<OArtifact> artifacts) {
        this.artifacts = artifacts.stream()
                .map(OArtifact::new)
                .collect(Collectors.toSet());
    }

    @Override
    public void run() {
        saveJarFiles();
        OrienteerClassLoaderUtil.updateOArtifactsJarsInMetadata(new LinkedList<>(artifacts));
    }

    private void saveJarFiles() {
        streamOfPrivateArtifacts()
                .forEach(artifact -> {
                    String name = artifact.getArtifactId() + "-" + artifact.getVersion();
                    File jarFile = OrienteerClassLoaderUtil.createJarFile(artifact.getJarBytes(), name);
                    artifact.setFile(jarFile);
                });
    }


    private Stream<OArtifactReference> streamOfPrivateArtifacts() {
        return artifacts.stream()
                .map(OArtifact::getArtifactReference)
                .filter(a -> a.getJarBytes() != null && a.getJarBytes().length > 0);
    }
}
