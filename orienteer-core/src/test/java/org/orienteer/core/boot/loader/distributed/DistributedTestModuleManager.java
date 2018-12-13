package org.orienteer.core.boot.loader.distributed;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.junit.DistributedModulesTestRunner;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

@RunWith(DistributedModulesTestRunner.class)
public class DistributedTestModuleManager extends AbstractDistributedModulesTest {

    @Inject
    @Named("artifacts.test")
    private Set<OArtifact> artifacts;

    @Test
    public void testAddArtifact() throws InterruptedException {
        manager1.addArtifacts(artifacts);

        Thread.sleep(10_000);

        Set<OArtifact> artifactsInMetadata1 = internalManager1.getOArtifactsMetadataAsSet();
        assertFalse(artifactsInMetadata1.isEmpty());
        assertEquals(artifacts, artifactsInMetadata1);

        Set<OArtifact> artifactsInMetadata2 = internalManager2.getOArtifactsMetadataAsSet();
        assertFalse(artifactsInMetadata2.isEmpty());
        assertEquals(artifacts, artifactsInMetadata2);

        assertEquals(artifactsInMetadata1, artifactsInMetadata2);
    }
}
