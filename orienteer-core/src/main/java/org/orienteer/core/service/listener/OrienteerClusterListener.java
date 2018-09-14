package org.orienteer.core.service.listener;

import com.hazelcast.core.*;
import org.orienteer.core.boot.loader.distributed.ResolveMetadataConflictTask;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Orienteer cluster listener
 * @see <a href="https://docs.hazelcast.org//docs/3.10.4/manual/html-single/index.html#cluster-events">Hazelcast</a>
 */
public class OrienteerClusterListener implements MembershipListener {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClusterListener.class);

    private final HazelcastInstance hz;

    public OrienteerClusterListener(HazelcastInstance hz) {
        this.hz = hz;
    }

    @Override
    public void memberAdded(MembershipEvent event) {
        LOG.info("added member: {}", event.getMember());
        if (isMasterNode()) {
            executeResolvingMetadataConflict(event.getMember());
        }
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {

    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {

    }

    private void executeResolvingMetadataConflict(Member member) {
        IExecutorService executor = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);
        Set<OArtifact> artifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet();
        Member localMember = hz.getCluster().getLocalMember();
        executor.executeOnMember(new ResolveMetadataConflictTask(artifacts, localMember.getUuid(), true), member);
    }

    private boolean isMasterNode() {
        Cluster cluster = hz.getCluster();
        return cluster.getMembers().iterator().next().equals(cluster.getLocalMember());
    }
}
