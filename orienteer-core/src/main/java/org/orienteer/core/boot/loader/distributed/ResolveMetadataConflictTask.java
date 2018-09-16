package org.orienteer.core.boot.loader.distributed;

import com.google.common.collect.Sets;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Task for resolve modules conflicts between nodes
 */
//TODO: create tests
public class ResolveMetadataConflictTask implements Runnable, Serializable, HazelcastInstanceAware {
    private static final long serialVersionUID = 3699246504123452884L;

    private static final Logger LOG = LoggerFactory.getLogger(ResolveMetadataConflictTask.class);

    public static final String EXECUTOR_NAME = "resolve.metadata.conflict.executor";

    private transient HazelcastInstance hz;

    private final Set<OArtifact> remoteArtifacts;
    private final String remoteId;
    private final boolean inverse;

    public ResolveMetadataConflictTask(Set<OArtifact> remoteArtifacts, String remoteId) {
        this(remoteArtifacts, remoteId, false);
    }

    public ResolveMetadataConflictTask(Set<OArtifact> remoteArtifacts, String remoteId, boolean inverse) {
        this.remoteArtifacts = remoteArtifacts;
        this.remoteId = remoteId;
        this.inverse = inverse;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hz) {
        this.hz = hz;
    }

    @Override
    public void run() {
        Set<OArtifact> localArtifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet();
        Set<OArtifact> remoteDifference = Sets.difference(remoteArtifacts, localArtifacts);
        Set<OArtifact> localDifference = Sets.difference(localArtifacts, remoteDifference);

        LOG.info("Resolve conflict between modules. Local unique modules: {}, remote unique modules: {}", localDifference.size(), remoteDifference.size());

        IExecutorService executor = hz.getExecutorService(DownloadArtifactsTask.EXECUTOR_NAME);
        Set<OArtifact> artifacts = downloadArtifacts(executor, createClone(remoteDifference));
        executor.executeOnMember(new UpdateMetadataTask(artifacts), hz.getCluster().getLocalMember());

        if (!localDifference.isEmpty() && inverse) {
            resolveConflictOnOtherNodes(createClone(localDifference), executor);
        }
    }

    private Set<OArtifact> downloadArtifacts(IExecutorService executor, Set<OArtifact> artifacts) {
        Member remoteMember = getRemoteMember();
        Future<Set<OArtifact>> futureArtifacts = executor.submitToMember(new DownloadArtifactsTask(artifacts), remoteMember);
        try {
            return futureArtifacts.get(5, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new IllegalStateException("Can't get artifacts from future", e);
        }
    }

    private Set<OArtifact> createClone(Set<OArtifact> artifacts) {
        return artifacts.stream()
                .map(OArtifact::new)
                .collect(Collectors.toSet());
    }

    private void resolveConflictOnOtherNodes(Set<OArtifact> artifacts, IExecutorService executor) {
        String localId = hz.getCluster().getLocalMember().getUuid();
        Set<Member> members = getMembers(m -> !m.getUuid().equals(localId));
        executor.executeOnMembers(new ResolveMetadataConflictTask(artifacts, localId, false), members);
    }

    private Member getRemoteMember() {
        return hz.getCluster().getMembers()
                .stream()
                .filter(m -> m.getUuid().equals(remoteId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Hazelcast member with id: " + remoteId));
    }

    private Set<Member> getMembers(Predicate<Member> predicate) {
        return hz.getCluster().getMembers()
                .stream()
                .filter(predicate)
                .collect(Collectors.toSet());
    }
}
