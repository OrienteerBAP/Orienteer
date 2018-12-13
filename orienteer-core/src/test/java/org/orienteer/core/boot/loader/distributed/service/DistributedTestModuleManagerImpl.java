package org.orienteer.core.boot.loader.distributed.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import org.orienteer.core.boot.loader.distributed.AddModulesToMetadataTask;
import org.orienteer.core.boot.loader.distributed.DeleteMetadataTask;
import org.orienteer.core.boot.loader.distributed.UpdateModulesInMetadataTask;
import org.orienteer.core.boot.loader.distributed.task.AddModulesToMetadataTaskTest;
import org.orienteer.core.boot.loader.distributed.task.DeleteMetadataTaskTest;
import org.orienteer.core.boot.loader.distributed.task.UpdateModulesInMetadataTaskTest;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.service.ModuleManager;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DistributedTestModuleManagerImpl extends ModuleManager {

    private final HazelcastInstance hz;

    public DistributedTestModuleManagerImpl(HazelcastInstance hz) {
        this.hz = hz;
    }

    @Override
    protected Optional<HazelcastInstance> getHazelcast() {
        return Optional.of(hz);
    }

    @Override
    protected UpdateModulesInMetadataTask createUpdateModulesTask(Member member, Map<OArtifact, OArtifact> map) {
        return new UpdateModulesInMetadataTaskTest(getIdByMember(member), map);
    }

    @Override
    protected AddModulesToMetadataTask createAddModulesTask(Member member, Set<OArtifact> artifacts) {
        return new AddModulesToMetadataTaskTest(getIdByMember(member), artifacts);
    }

    @Override
    protected DeleteMetadataTask createDeleteModulesTask(Member member, Set<OArtifact> artifacts) {
        return new DeleteMetadataTaskTest(getIdByMember(member), artifacts);
    }

    private String getIdByMember(Member member) {
        HazelcastInstance hz = getHazelcast().orElseThrow(IllegalStateException::new);
        Set<Member> members = hz.getCluster().getMembers();
        int i = 1;
        for (Member m : members) {
            if (m.equals(member)) {
                return i + "";
            }
            i++;
        }
        return "";
    }
}
