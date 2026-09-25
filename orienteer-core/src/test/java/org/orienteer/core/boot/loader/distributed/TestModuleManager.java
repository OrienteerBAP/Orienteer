package org.orienteer.core.boot.loader.distributed;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.service.IModuleManager;
import org.orienteer.junit.OrienteerTestRunner;

import java.util.Set;

import static junit.framework.TestCase.*;

@RunWith(OrienteerTestRunner.class)
public class TestModuleManager extends AbstractModulesTest {

    @Inject
    private IModuleManager manager;

    @Inject
    @Named("artifacts.test")
    private Set<OArtifact> artifacts;

    @Inject
    @Named("user.artifacts.test")
    private Set<OArtifact> userArtifacts;

    @Inject
    private InternalOModuleManager moduleManager;

    @Test
    public void testAddArtifact() {
        OArtifact artifact = artifacts.iterator().next();
        manager.addArtifact(artifact);
        Set<OArtifact> artifactsInMetadata = moduleManager.getOArtifactsMetadataAsSet();
        assertEquals(1, artifactsInMetadata.size());
        assertEquals(artifact, artifactsInMetadata.iterator().next());
    }

    @Test
    @Ignore("Downloads org.orienteer:orienteer-birt:2.0-SNAPSHOT from a remote repository, which no longer resolves (birt is parked, OSSRH is gone). Make hermetic with a local file-repository fixture: REFRESH_PLAN.md P4")
    public void testAddArtifacts() {
        manager.addArtifacts(artifacts);
        Set<OArtifact> artifactsInMetadata = moduleManager.getOArtifactsMetadataAsSet();
        assertFalse(artifactsInMetadata.isEmpty());
        assertEquals(artifacts, artifactsInMetadata);
    }

    @Test
    @Ignore("Downloads org.orienteer:orienteer-birt:2.0-SNAPSHOT from a remote repository, which no longer resolves (birt is parked, OSSRH is gone). Make hermetic with a local file-repository fixture: REFRESH_PLAN.md P4")
    public void testDeleteArtifact() {
        manager.addArtifacts(artifacts);

        Set<OArtifact> metadata = moduleManager.getOArtifactsMetadataAsSet();
        assertEquals(artifacts, metadata);

        manager.deleteArtifact(userArtifacts.iterator().next());

        metadata = moduleManager.getOArtifactsMetadataAsSet();
        assertTrue(metadata.size() < artifacts.size());

        Set<OArtifact> diff = Sets.difference(artifacts, metadata);
        assertEquals(1, diff.size());
        assertEquals(userArtifacts.iterator().next(), diff.iterator().next());
    }

    @Test
    @Ignore("Downloads org.orienteer:orienteer-birt:2.0-SNAPSHOT from a remote repository, which no longer resolves (birt is parked, OSSRH is gone). Make hermetic with a local file-repository fixture: REFRESH_PLAN.md P4")
    public void testDeleteArtifacts() {
        manager.addArtifacts(artifacts);

        Set<OArtifact> metadata = moduleManager.getOArtifactsMetadataAsSet();
        assertEquals(artifacts, metadata);

        manager.deleteArtifacts(userArtifacts);

        metadata = moduleManager.getOArtifactsMetadataAsSet();
        assertTrue(metadata.size() < artifacts.size());
        assertEquals(userArtifacts, Sets.difference(artifacts, metadata));
    }
}
