package org.orienteer.core.boot.loader.distributed.task;

import org.orienteer.core.boot.loader.distributed.DownloadArtifactsTask;
import org.orienteer.core.boot.loader.distributed.ResolveMetadataConflictTask;
import org.orienteer.core.boot.loader.distributed.util.TestModuleUtils;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;

import java.util.Set;

public class ResolveMetadataConflictTaskTest extends ResolveMetadataConflictTask {

    private final String id;

    public ResolveMetadataConflictTaskTest(String id, Set<OArtifact> remoteArtifacts, String remoteId) {
        super(remoteArtifacts, remoteId);
        this.id = id;
    }

    @Override
    protected InternalOModuleManager getModuleManager() {
        return TestModuleUtils.getModuleManager(id);
    }

//    @Override
//    protected AddModulesToMetadataTask createAddModulesTask(Set<OArtifact> artifacts) {
//        return new AddModulesToMetadataTaskTest(manager, artifacts);
//    }

    @Override
    protected DownloadArtifactsTask createDownloadTask(Set<OArtifact> artifacts) {
        return new DownloadArtifactsTaskTest(id, artifacts);
    }
}
