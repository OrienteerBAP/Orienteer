package org.orienteer.core.boot.loader.util.metadata;

import com.google.common.collect.Lists;
import org.apache.http.util.Args;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test work with metadata.xml
 */
public class OMetadataTest {
    private static OArtifact metadata;

    @BeforeClass
    public static void init() {
        OrienteerClassLoaderUtil.deleteMetadataFile();
        Artifact artifact = new DefaultArtifact("org.company:artifact:1.0");
        artifact = artifact.setFile(new File("module.jar"));
        metadata = new OArtifact();
        metadata.setArtifactReference(OArtifactReference.valueOf(artifact));
        metadata.setLoad(true);
        metadata.setTrusted(true);
        OrienteerClassLoaderUtil.createOArtifactsMetadata(Lists.newArrayList(metadata));
        OArtifact oArtifact = OrienteerClassLoaderUtil.getOoArtifactsMetadataAsList().get(0);
        testArtifact(metadata, oArtifact);
    }

    @Test
    public void readMetadata() {
        List<OArtifact> list = OrienteerClassLoaderUtil.getOoArtifactsMetadataAsList();
        assertTrue("list.size > 0", list.size() > 0);
    }

    @Test
    public void update() throws Exception {
        metadata.setLoad(false);
        metadata.setTrusted(true);
        OrienteerClassLoaderUtil.updateOArtifactInMetadata(metadata);
        OArtifact oArtifact = OrienteerClassLoaderUtil.getOoArtifactsMetadataAsList().get(0);
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
        OrienteerClassLoaderUtil.updateOArtifactsJarsInMetadata(list);
        List<OArtifact> result = OrienteerClassLoaderUtil.getOoArtifactsMetadataAsList();

        testArtifact(metadata, result.get(0));
        testArtifact(oArtifact1, result.get(1));
        testArtifact(oArtifact2, result.get(2));

        OrienteerClassLoaderUtil.deleteOArtifactsFromMetadata(Lists.newArrayList(oArtifact1, oArtifact2));
        List<OArtifact> metadataAsList = OrienteerClassLoaderUtil.getOoArtifactsMetadataAsList();

        assertTrue("metadata size must be 1", metadataAsList.size() == 1);
    }

    @AfterClass
    public static void delete() throws Exception {
        OrienteerClassLoaderUtil.deleteOArtifactFromMetadata(metadata);
        List<OArtifact> list = OrienteerClassLoaderUtil.getOoArtifactsMetadataAsList();
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