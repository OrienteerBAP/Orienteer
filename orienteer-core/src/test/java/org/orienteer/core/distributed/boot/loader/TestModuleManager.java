package org.orienteer.core.distributed.boot.loader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.boot.loader.service.IModuleManager;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.junit.OrienteerTestRunner;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

@RunWith(OrienteerTestRunner.class)
public class TestModuleManager extends AbstractModulesTest {

    @Inject
    private IModuleManager manager;

    @Inject
    @Named("user.artifacts.test")
    private Set<OArtifact> artifacts;


    @Test
    public void testAddArtifact() {
        OArtifact artifact = artifacts.iterator().next();
        manager.addArtifact(artifact);
        Set<OArtifact> artifactsInMetadata = OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet();
        assertEquals(1, artifactsInMetadata.size());
        assertEquals(artifact, artifactsInMetadata.iterator().next());
    }

    @Test
    public void testAddArtifacts() {
        manager.addArtifacts(artifacts);
        Set<OArtifact> artifactsInMetadata = OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet();
        assertFalse(artifactsInMetadata.isEmpty());
        assertEquals(artifacts, artifactsInMetadata);
    }
}
