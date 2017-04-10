package org.orienteer.core.boot.loader.util.metadata;

import com.google.common.collect.Lists;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

import java.io.File;

/**
 * @author Vitaliy Gonchar
 */
public class OMetadataUpdaterTest {
    private static OArtifact metadata;

    @Before
    public void init() {
        Artifact artifact = new DefaultArtifact("org.company:module:1.0");
        artifact = artifact.setFile(new File("module.jar"));
        metadata = new OArtifact();
        metadata.setArtifact(OArtifactReference.valueOf(artifact));
        metadata.setLoad(true);
        metadata.setTrusted(true);
    }

    @Test
    public void create() throws Exception {
        OrienteerClassLoaderUtil.createOModulesConfigurationsMetadata(Lists.newArrayList(metadata));
    }

    @Test
    public void update() throws Exception {
        metadata.setLoad(false);
        metadata.setTrusted(true);
        OrienteerClassLoaderUtil.updateOModuleConfigurationInMetadata(metadata);
    }

    @AfterClass
    public static void delete() throws Exception {
        OrienteerClassLoaderUtil.deleteOModuleConfigurationFromMetadata(metadata);
    }
}