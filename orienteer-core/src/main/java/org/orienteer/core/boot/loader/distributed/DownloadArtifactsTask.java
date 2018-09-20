package org.orienteer.core.boot.loader.distributed;

import org.apache.wicket.util.file.Files;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Task for download artifacts from one node to another
 */
public class DownloadArtifactsTask extends AbstractTask implements Callable<Set<OArtifact>> {

    public static final String EXECUTOR_NAME = "download.task";

    private final Set<OArtifact> artifacts;

    public DownloadArtifactsTask(Set<OArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    private Set<OArtifact> createResult() {
        return getLocalArtifactsStream()
                .filter(artifacts::contains)
                .map(this::readJarBytes)
                .collect(Collectors.toSet());

    }

    private Stream<OArtifact> getLocalArtifactsStream() {
        return getModuleManager()
                .getOArtifactsMetadataAsSet()
                .stream();
    }

    private OArtifact readJarBytes(OArtifact artifact) {
        try {
            OArtifactReference ref = artifact.getArtifactReference();
            byte [] bytes = Files.readBytes(ref.getFile());
            ref.setJarBytes(bytes);
            ref.setFile(null);
            return artifact;
        } catch (IOException ex) {
            throw new IllegalStateException("Can't read artifact jar file: " + artifact, ex);
        }
    }

    @Override
    public Set<OArtifact> call() throws Exception {
        return createResult();
    }
}
