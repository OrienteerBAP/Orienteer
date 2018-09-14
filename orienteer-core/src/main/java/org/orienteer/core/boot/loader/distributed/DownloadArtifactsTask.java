package org.orienteer.core.boot.loader.distributed;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import org.apache.wicket.util.file.Files;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Task for download artifacts from one node to another
 */
public class DownloadArtifactsTask implements Callable<Set<OArtifact>>, Serializable, HazelcastInstanceAware {

    public static final String EXECUTOR_NAME = "download.task";

    private final Set<OArtifact> artifacts;
    private transient HazelcastInstance hz;

    public DownloadArtifactsTask(Set<OArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hz) {
        this.hz = hz;
    }

    private Set<OArtifact> createResult() {
        return getLocalArtifactsStream()
                .filter(artifacts::contains)
                .map(this::readJarBytes)
                .collect(Collectors.toSet());

    }

    private Stream<OArtifact> getLocalArtifactsStream() {
        return OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet()
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
