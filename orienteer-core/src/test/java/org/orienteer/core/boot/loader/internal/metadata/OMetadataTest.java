package org.orienteer.core.boot.loader.internal.metadata;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.http.util.Args;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;
import org.orienteer.junit.OrienteerTestRunner;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test work with metadata.xml
 */
@RunWith(OrienteerTestRunner.class)
public class OMetadataTest {
    private OArtifact metadata;

    @Inject
    private InternalOModuleManager moduleManager;

    @Before
    public void init() {
        moduleManager.deleteMetadataFile();
        Artifact artifact = new DefaultArtifact("org.company:artifact:1.0");
        artifact = artifact.setFile(new File("module.jar"));
        metadata = new OArtifact();
        metadata.setArtifactReference(OArtifactReference.valueOf(artifact));
        metadata.setLoad(true);
        metadata.setTrusted(true);
        moduleManager.createOArtifactsMetadata(Lists.newArrayList(metadata));
        OArtifact oArtifact = moduleManager.getOArtifactsMetadataAsList().get(0);
        testArtifact(metadata, oArtifact);
    }

    @Test
    public void readMetadata() {
        List<OArtifact> list = moduleManager.getOArtifactsMetadataAsList();
        assertTrue("list.size > 0", list.size() > 0);
    }

    @Test
    public void update() throws Exception {
        metadata.setLoad(false);
        metadata.setTrusted(true);
        moduleManager.updateOArtifactInMetadata(metadata);
        OArtifact oArtifact = moduleManager.getOArtifactsMetadataAsList().get(0);
        testArtifact(metadata, oArtifact);
    }

    @Test
    public void addListAndDeleteToMetadata() throws Exception {
        Artifact artifact1 = new DefaultArtifact("org.orienteer:orienteer-core:1.3-SNAPSHOT");
        artifact1 = artifact1.setFile(new File("orienteer-core.jar"));
        OArtifact oArtifact1 = new OArtifact();
        oArtifact1.setArtifactReference(OArtifactReference.valueOf(artifact1));
        oArtifact1.setLoad(true);
        oArtifact1.setTrusted(true);

        Artifact artifact2 = new DefaultArtifact("org.orienteer:devutils:1.3-SNAPSHOT");
        artifact2 = artifact2.setFile(new File("orienteer-devutils.jar"));
        OArtifact oArtifact2 = new OArtifact();
        oArtifact2.setArtifactReference(OArtifactReference.valueOf(artifact2));
        oArtifact2.setLoad(true);
        oArtifact2.setTrusted(true);
        List<OArtifact> list = Lists.newArrayList(metadata, oArtifact1, oArtifact2);
        moduleManager.updateOArtifactsJarsInMetadata(list);
        List<OArtifact> result = moduleManager.getOArtifactsMetadataAsList();

        testArtifact(metadata, result.get(0));
        testArtifact(oArtifact1, result.get(1));
        testArtifact(oArtifact2, result.get(2));

        moduleManager.deleteOArtifactsFromMetadata(Lists.newArrayList(oArtifact1, oArtifact2));
        List<OArtifact> metadataAsList = moduleManager.getOArtifactsMetadataAsList();

        assertTrue("metadata size must be 1", metadataAsList.size() == 1);
    }

    @After
    public void delete() throws Exception {
        moduleManager.deleteOArtifactFromMetadata(metadata);
        List<OArtifact> list = moduleManager.getOArtifactsMetadataAsList();
        assertTrue("metadata must be empty", list.size() == 0);
    }

    private static void testArtifact(OArtifact expected, OArtifact actual) {
        Args.notNull(expected, "expected");
        Args.notNull(actual, "actual");
        assertEquals("test artifact load", expected.isLoad(), actual.isLoad());
        assertEquals("test artifact trusted", expected.isTrusted(), actual.isTrusted());
        assertEquals("test artifact groupId", expected.getArtifactReference().getGroupId(),
                actual.getArtifactReference().getGroupId());
        assertEquals("test artifact artifactId", expected.getArtifactReference().getArtifactId(),
                actual.getArtifactReference().getArtifactId());
        assertEquals("test artifact version", expected.getArtifactReference().getVersion(),
                actual.getArtifactReference().getVersion());
        assertEquals("test artifact jar files", expected.getArtifactReference().getFile().getAbsoluteFile(),
                actual.getArtifactReference().getFile().getAbsoluteFile());
        assertEquals("test artifact descriptions", expected.getArtifactReference().getDescription(),
                actual.getArtifactReference().getDescription());
    }
}