package org.orienteer.core.boot.loader.distributed.task;

import org.orienteer.core.boot.loader.distributed.UpdateModulesInMetadataTask;
import org.orienteer.core.boot.loader.distributed.util.TestModuleUtils;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;

import java.util.Map;

public class UpdateModulesInMetadataTaskTest extends UpdateModulesInMetadataTask {

    private final String id;

    public UpdateModulesInMetadataTaskTest(String id, Map<OArtifact, OArtifact> modulesForUpdate) {
        super(modulesForUpdate);
        this.id = id;
    }

    @Override
    protected InternalOModuleManager getModuleManager() {
        return TestModuleUtils.getModuleManager(id);
    }
}
