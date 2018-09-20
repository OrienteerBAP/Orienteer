package org.orienteer.core.boot.loader.service;

import com.google.inject.Singleton;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.boot.loader.distributed.AddModulesToMetadataTask;
import org.orienteer.core.boot.loader.distributed.DeleteMetadataTask;
import org.orienteer.core.boot.loader.distributed.ResolveMetadataConflictTask;
import org.orienteer.core.boot.loader.distributed.UpdateModulesInMetadataTask;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Default implementation for {@link IModuleManager}
 */
@Singleton
public class ModuleManager implements IModuleManager {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleManager.class);

    @Override
    public void addArtifact(OArtifact artifact) {
        addArtifacts(Collections.singleton(artifact));
    }

    @Override
    public void updateArtifact(OArtifact previous, OArtifact artifact) {
        updateArtifacts(CommonUtils.toMap(previous, artifact));
    }

    @Override
    public void deleteArtifact(OArtifact artifact) {
        deleteArtifacts(Collections.singleton(artifact));
    }

    @Override
    public void addArtifacts(Set<OArtifact> artifacts) {
        LOG.info("add artifacts: {}", artifacts);
        Optional<HazelcastInstance> opt = getHazelcast();
        if (opt.isPresent()) {
            HazelcastInstance hz = opt.get();
            IExecutorService executor = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);
            createAddModulesTask(hz.getCluster().getLocalMember(), artifacts).run();

            executeOnOtherMember(hz, member -> {
                Set<OArtifact> copy = deepCopy(artifacts);
                executor.executeOnMember(createAddModulesTask(member, copy), member);
            });
        } else {
            createAddModulesTask(null, artifacts).run();
        }
    }

    @Override
    public void updateArtifacts(Map<OArtifact, OArtifact> artifacts) {
        LOG.info("update artifacts: {}", artifacts);

        Optional<HazelcastInstance> opt = getHazelcast();
        if (opt.isPresent()) {
            HazelcastInstance hz = opt.get();
            IExecutorService executor = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);

            createUpdateModulesTask(hz.getCluster().getLocalMember(), artifacts).run();

            executeOnOtherMember(hz, member -> {
                Map<OArtifact, OArtifact> copy = deepCopy(artifacts);
                executor.executeOnMember(createUpdateModulesTask(member, copy), member);
            });
        } else {
            createUpdateModulesTask(null, artifacts).run();
        }
    }

    @Override
    public void deleteArtifacts(Set<OArtifact> artifacts) {
        LOG.info("delete artifacts: {}", artifacts);
        Optional<HazelcastInstance> opt = getHazelcast();

        if (opt.isPresent()) {
            HazelcastInstance hz = opt.get();
            IExecutorService executor = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);

            createDeleteModulesTask(hz.getCluster().getLocalMember(), artifacts).run();

            executeOnOtherMember(hz, member -> {
                Set<OArtifact> copy = deepCopy(artifacts);
                executor.executeOnMember(createDeleteModulesTask(member, copy), member);
            });
        } else {
            createDeleteModulesTask(null, artifacts).run();
        }
    }


    private void executeOnEveryMember(HazelcastInstance hz, Consumer<Member> consumer) {
        Cluster cluster = hz.getCluster();
        cluster.getMembers().forEach(consumer);
    }

    private void executeOnOtherMember(HazelcastInstance hz, Consumer<Member> consumer) {
        Cluster cluster = hz.getCluster();
        Member localMember = cluster.getLocalMember();

        cluster.getMembers().stream()
                .filter(m -> !m.equals(localMember))
                .forEach(consumer);
    }

    protected Optional<HazelcastInstance> getHazelcast() {
        return getApp().getHazelcast();
    }

    private OrienteerWebApplication getApp() {
        return OrienteerWebApplication.lookupApplication();
    }

    protected UpdateModulesInMetadataTask createUpdateModulesTask(Member target, Map<OArtifact, OArtifact> map) {
        return new UpdateModulesInMetadataTask(map);
    }

    protected AddModulesToMetadataTask createAddModulesTask(Member target, Set<OArtifact> artifacts) {
        return new AddModulesToMetadataTask(artifacts);
    }

    protected DeleteMetadataTask createDeleteModulesTask(Member target, Set<OArtifact> artifacts) {
        return new DeleteMetadataTask(artifacts);
    }

    private Set<OArtifact> deepCopy(Set<OArtifact> artifacts) {
        return artifacts.stream()
                .map(OArtifact::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<OArtifact, OArtifact> deepCopy(Map<OArtifact, OArtifact> artifacts) {
        Map<OArtifact, OArtifact> result = new LinkedHashMap<>(artifacts.size());
        artifacts.forEach((k, v) -> result.put(new OArtifact(k), new OArtifact(v)));
        return result;
    }
}
