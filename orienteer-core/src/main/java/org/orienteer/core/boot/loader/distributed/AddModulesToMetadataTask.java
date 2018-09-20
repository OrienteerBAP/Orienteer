package org.orienteer.core.boot.loader.distributed;

import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Task for add modules to metadata.xml on node
 */
public class AddModulesToMetadataTask extends AbstractTask implements Runnable {

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
        InternalOModuleManager manager = getModuleManager();
        Set<OArtifact> orienteerArtifacts = manager.getOrienteerArtifacts(artifacts);
        Set<OArtifact> userArtifacts = Sets.difference(artifacts, orienteerArtifacts);

        updateOrienteerArtifacts(manager, orienteerArtifacts);
        updateUserArtifacts(manager, userArtifacts);
        executeCallback();
    }

    private void updateOrienteerArtifacts(InternalOModuleManager manager, Set<OArtifact> artifacts) {
        Set<OArtifact> preparedArtifacts = artifacts.stream()
                .peek(a -> downloadArtifact(manager, a))
                .filter(artifact -> artifact.getArtifactReference().getFile() != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        manager.updateOArtifactsInMetadata(preparedArtifacts);
    }


    private void updateUserArtifacts(InternalOModuleManager manager, Set<OArtifact> artifacts) {
        Set<OArtifact> prepared = artifacts.stream()
                .filter(a -> a.getArtifactReference().isContainsJarBytes())
                .peek(a -> createArtifactFile(manager, a.getArtifactReference()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        manager.updateOArtifactsInMetadata(prepared);
    }

    private void downloadArtifact(InternalOModuleManager manager, OArtifact artifact) {
        OArtifactReference ref = artifact.getArtifactReference();
        Artifact downloaded = manager.downloadArtifact(ref.toAetherArtifact());
        if (downloaded != null) {
            ref.setFile(downloaded.getFile());
        }
    }

    private void createArtifactFile(InternalOModuleManager manager, OArtifactReference reference) {
        String name = reference.getArtifactId() + "-" + reference.getVersion();
        File jarFile = manager.getJarsManager().createJarFile(reference.getJarBytes(), name);
        reference.setFile(jarFile);
    }
}
