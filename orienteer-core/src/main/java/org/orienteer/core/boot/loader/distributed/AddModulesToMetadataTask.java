package org.orienteer.core.boot.loader.distributed;

import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Task for add modules to metadata.xml on node
 */
public class AddModulesToMetadataTask implements Runnable, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(AddModulesToMetadataTask.class);

    private final Set<OArtifact> artifacts;

    public AddModulesToMetadataTask(Set<OArtifact> artifacts) {
        this.artifacts = artifacts.stream()
                .map(OArtifact::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void run() {
        LOG.info("Update metadata.xml with new artifacts: {}", artifacts.size());
        Set<OArtifact> orienteerArtifacts = OrienteerClassLoaderUtil.getOrienteerArtifacts(artifacts);
        Set<OArtifact> userArtifacts = Sets.difference(artifacts, orienteerArtifacts);

        updateOrienteerArtifacts(orienteerArtifacts);
        updateUserArtifacts(userArtifacts);

    }

    private void updateOrienteerArtifacts(Set<OArtifact> artifacts) {
        Set<OArtifact> preparedArtifacts = artifacts.stream()
                .peek(this::downloadArtifact)
                .filter(artifact -> artifact.getArtifactReference().getFile() != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        OrienteerClassLoaderUtil.updateOArtifactsInMetadata(preparedArtifacts);
    }


    private void updateUserArtifacts(Set<OArtifact> artifacts) {
        artifacts.stream()
                .map(OArtifact::getArtifactReference)
                .filter(a -> a.getJarBytes() != null && a.getJarBytes().length > 0)
                .forEach(this::createArtifactFile);
        OrienteerClassLoaderUtil.updateOArtifactsJarsInMetadata(new LinkedList<>(artifacts));
    }

    private void downloadArtifact(OArtifact artifact) {
        OArtifactReference ref = artifact.getArtifactReference();
        Artifact downloaded = OrienteerClassLoaderUtil.downloadArtifact(ref.toAetherArtifact());
        if (downloaded != null) {
            ref.setFile(downloaded.getFile());
        }
    }

    private void createArtifactFile(OArtifactReference reference) {
        String name = reference.getArtifactId() + "-" + reference.getVersion();
        File jarFile = OrienteerClassLoaderUtil.createJarFile(reference.getJarBytes(), name);
        reference.setFile(jarFile);
    }
}
