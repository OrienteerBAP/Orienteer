package org.orienteer.core.boot.loader.distributed.task;

import org.orienteer.core.boot.loader.distributed.AddModulesToMetadataTask;
import org.orienteer.core.boot.loader.distributed.util.TestModuleUtils;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;

import java.util.Set;

public class AddModulesToMetadataTaskTest extends AddModulesToMetadataTask {

    private final String id;

    public AddModulesToMetadataTaskTest(String id, Set<OArtifact> artifacts) {
        super(artifacts);
        this.id = id;
    }

    @Override
    protected InternalOModuleManager getModuleManager() {
        return TestModuleUtils.getModuleManager(id);
    }
}
