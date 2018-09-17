package org.orienteer.core.boot.loader.distributed;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.junit.OrienteerTestRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(OrienteerTestRunner.class)
public class TestAddModulesToMetadataTasks extends AbstractModulesTest {


    @Inject
    @Named("hazelcast.test")
    private HazelcastInstance hz;

    @Inject
    @Named("artifacts.test")
    private Set<OArtifact> artifacts;

    @Inject
    @Named("orienteer.artifacts.test")
    private Set<OArtifact> orienteerArtifacts;

    @Test
    public void testUpdateTask() throws IOException {
        updateArtifacts();
        assertMetadataUpdated();
    }

    @Test
    public void testDownloadArtifactsTask() throws Exception {
        updateArtifacts();
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


    private void updateArtifacts() {
        updateJarBytes();
        IExecutorService service = hz.getExecutorService(ResolveMetadataConflictTask.EXECUTOR_NAME);
        AddModulesToMetadataTask task = new AddModulesToMetadataTask(artifacts);
        service.execute(task);
    }

    private void updateJarBytes() {
        artifacts.forEach(artifact -> {
            try {
                OArtifactReference artifactReference = artifact.getArtifactReference();
                byte[] bytes = Files.readAllBytes(artifactReference.getFile().toPath());
                artifactReference.setJarBytes(bytes);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        });
    }

    private void assertMetadataUpdated() {
        Set<OArtifact> artifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataAsSet();
        assertFalse(artifacts.isEmpty());
        assertEquals(this.artifacts, artifacts);

        artifacts.forEach(artifact -> assertNotNull(artifact.getArtifactReference().getFile()));
    }
}
