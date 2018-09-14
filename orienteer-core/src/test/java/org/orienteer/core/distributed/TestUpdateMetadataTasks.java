package org.orienteer.core.distributed;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.orienteer.core.boot.loader.distributed.DownloadArtifactsTask;
import org.orienteer.core.boot.loader.distributed.ResolveMetadataConflictTask;
import org.orienteer.core.boot.loader.distributed.UpdateMetadataTask;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.junit.OrienteerTestRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(OrienteerTestRunner.class)
public class TestUpdateMetadataTasks {

    private OArtifact artifact;

    @Inject
    @Named("orienteer.loader.libs.folder")
    private String libFolder;

    private HazelcastInstance hz;

    @Before
    public void init() {
        initArtifact();
        assumeNotNull(artifact);
        hz = mock(HazelcastInstance.class);

        IExecutorService service = new TestExecutorService();


        Member localMember = mock(Member.class);
        when(localMember.getUuid()).thenReturn(UUID.randomUUID().toString());

        Member remoteMember = mock(Member.class);
        when(remoteMember.getUuid()).thenReturn(UUID.randomUUID().toString());

        Cluster cluster = mock(Cluster.class);
        when(cluster.getLocalMember()).thenReturn(localMember);
        when(cluster.getMembers()).thenReturn(Sets.newSet(localMember, remoteMember));

        when(hz.getExecutorService(anyString())).thenReturn(service);
        when(hz.getCluster()).thenReturn(cluster);

    }

    private void initArtifact() {
        OArtifactReference reference = new OArtifactReference(
                "org.orienteer",
                "orienteer-pages",
                "1.4-SNAPSHOT"
        );
        File file = getTestJarFile();
        if (file != null) {
            reference.setFile(file);
            artifact = new OArtifact();
            artifact.setArtifactReference(reference);
        }
    }

    @After
    public void destroy() throws IOException {
        artifact = null;
        List<OArtifact> artifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataAsList();
        OrienteerClassLoaderUtil.deleteOArtifactFiles(artifacts);
        OrienteerClassLoaderUtil.deleteMetadataFile();
        Files.deleteIfExists(Paths.get(libFolder));
    }

    private File getTestJarFile() {
        URL url = getClass().getResource("orienteer-pages.jar");
        try {
            return new File(url.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testUpdateTask() throws IOException {
        updateArtifacts();
        assertMetadataUpdated();
    }

    @Test
    public void testDownloadArtifactsTask() throws Exception {
        updateArtifacts();
        Set<OArtifact> artifacts = Collections.singleton(artifact);
        DownloadArtifactsTask task = new DownloadArtifactsTask(artifacts);
        Set<OArtifact> downloaded = task.call();

        assertFalse(downloaded.isEmpty());

        downloaded.forEach(d -> {
            OArtifactReference ref = d.getArtifactReference();
            assertNull(ref.getFile());
            assertNotNull(ref.getJarBytes());
            assertTrue(ref.getJarBytes().length > 0);
        });

        assertEquals(artifacts, downloaded);
    }


    private void updateArtifacts() throws IOException {
        updateJarBytes();
        IExecutorService service = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);
        UpdateMetadataTask task = new UpdateMetadataTask(Collections.singleton(artifact));
        service.execute(task);
    }

    private void updateJarBytes() throws IOException {
        OArtifactReference artifactReference = artifact.getArtifactReference();
        artifactReference.setJarBytes(Files.readAllBytes(artifactReference.getFile().toPath()));
    }

    private void assertMetadataUpdated() {
        Set<OArtifact> artifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet();
        assertFalse(artifacts.isEmpty());
        assertTrue(artifacts.contains(artifact));

        for (OArtifact a : artifacts) {
            if (a.equals(artifact)) {
                File file = a.getArtifactReference().getFile();
                assertNotNull(file);
                assertTrue(Files.exists(file.toPath()));
                break;
            }
        }
    }
}
