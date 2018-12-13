package org.orienteer.core.boot.loader.service;

import com.google.inject.Singleton;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import org.danekja.java.misc.serializable.SerializableRunnable;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.boot.loader.distributed.*;
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
        addArtifact(artifact, null);
    }

    @Override
    public void addArtifact(OArtifact artifact, SerializableRunnable callback) {
        addArtifacts(Collections.singleton(artifact), callback);
    }

    @Override
    public void updateArtifact(OArtifact previous, OArtifact artifact) {
        updateArtifact(previous, artifact, null);
    }

    @Override
    public void updateArtifact(OArtifact previous, OArtifact artifact, SerializableRunnable callback) {
        updateArtifacts(CommonUtils.toMap(previous, artifact), callback);
    }

    @Override
    public void deleteArtifact(OArtifact artifact) {
        deleteArtifact(artifact, null);
    }

    @Override
    public void deleteArtifact(OArtifact artifact, SerializableRunnable callback) {
        deleteArtifacts(Collections.singleton(artifact), callback);
    }

    @Override
    public void addArtifacts(Set<OArtifact> artifacts) {
        addArtifacts(artifacts, null);
    }

    @Override
    public void addArtifacts(Set<OArtifact> artifacts, SerializableRunnable callback) {
        LOG.info("add artifacts: {}", artifacts);
        Optional<HazelcastInstance> opt = getHazelcast();

        AddModulesToMetadataTask task = createAddModulesTask(null, artifacts);
        task.setCallback(callback);
        task.run();

        if (opt.isPresent()) {
            HazelcastInstance hz = opt.get();
            IExecutorService executor = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);

            executeOnOtherMember(hz, member -> {
                Set<OArtifact> copy = deepCopy(artifacts);
                executor.executeOnMember(createAddModulesTask(member, copy), member);
            });
        }
    }

    @Override
    public void updateArtifacts(Map<OArtifact, OArtifact> artifacts) {
        updateArtifacts(artifacts, null);
    }

    @Override
    public void updateArtifacts(Map<OArtifact, OArtifact> artifacts, SerializableRunnable callback) {
        LOG.info("update artifacts: {}", artifacts);

        Optional<HazelcastInstance> opt = getHazelcast();

        UpdateModulesInMetadataTask task = createUpdateModulesTask(null, artifacts);
        task.setCallback(callback);
        task.run();

        if (opt.isPresent()) {
            HazelcastInstance hz = opt.get();
            IExecutorService executor = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);

            executeOnOtherMember(hz, member -> {
                Map<OArtifact, OArtifact> copy = deepCopy(artifacts);
                executor.executeOnMember(createUpdateModulesTask(member, copy), member);
            });
        }
    }

    @Override
    public void deleteArtifacts(Set<OArtifact> artifacts) {
        deleteArtifacts(artifacts, null);
    }

    @Override
    public void deleteArtifacts(Set<OArtifact> artifacts, SerializableRunnable callback) {
        LOG.info("delete artifacts: {}", artifacts);
        Optional<HazelcastInstance> opt = getHazelcast();

        DeleteMetadataTask task = createDeleteModulesTask(null, artifacts);
        task.setCallback(callback);
        task.run();

        if (opt.isPresent()) {
            HazelcastInstance hz = opt.get();
            IExecutorService executor = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);

            executeOnOtherMember(hz, member -> {
                Set<OArtifact> copy = deepCopy(artifacts);
                executor.executeOnMember(createDeleteModulesTask(member, copy), member);
            });
        }
    }

    @Override
    public void reloadOrienteer() {
        LOG.info("Reload Orienteer");
        Optional<HazelcastInstance> hzOpt = getHazelcast();

        if (hzOpt.isPresent()) {
            HazelcastInstance hz = hzOpt.get();
            IExecutorService executor = hz.getExecutorService(ReloadOrienteerTask.EXECUTOR_NAME);
            executor.executeOnMember(createReloadOrienteerTask(), getLocalMember(hz));
            executeOnOtherMember(hz, member -> executor.executeOnMember(createReloadOrienteerTask(), member));
        } else createReloadOrienteerTask().run();
    }

    private void executeOnOtherMember(HazelcastInstance hz, Consumer<Member> consumer) {
        Cluster cluster = hz.getCluster();
        Member localMember = cluster.getLocalMember();

        cluster.getMembers().stream()
                .filter(m -> !m.equals(localMember))
                .forEach(consumer);
    }

    private Member getLocalMember(HazelcastInstance hz) {
        return hz != null ? hz.getCluster().getLocalMember() : null;
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

    protected ReloadOrienteerTask createReloadOrienteerTask() {
        return new ReloadOrienteerTask();
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
