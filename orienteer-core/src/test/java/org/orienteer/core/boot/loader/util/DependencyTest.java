package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;


public class DependencyTest {

    @Test
    public void testSnapshot() throws Exception {
        String groupId = "org.orienteer";
        String artifactId = "orienteer-devutils";
        String version = "1.3-SNAPSHOT";
        Artifact artifact = new DefaultArtifact(gav(groupId, artifactId, version, "jar"));
        Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        assertTrue("Artifact must present", artifactOptional.isPresent());
        assertNotNull("Jar file of artifact can't be null", artifactOptional.get().getFile());
        List<ArtifactResult> resolvedArtifact = OrienteerClassLoaderUtil.getResolvedArtifact(artifactOptional.get());
        assertEquals("Size of resolved dependencies", true, resolvedArtifact.size() > 0);

        for (ArtifactResult res : resolvedArtifact) {
            Artifact resArtifact = res.getArtifact();
            assertNotNull("Result artifact", resArtifact);
            assertNotNull("Result jar file can't be null", resArtifact.getFile());
            Files.deleteIfExists(resArtifact.getFile().toPath());
        }
        Files.deleteIfExists(artifactOptional.get().getFile().toPath());
    }


    @Test
    public void downloadParentDependencyTest() throws Exception {
        String groupId = "org.orienteer";
        String artifactId = "orienteer-parent";
        String version = "1.2";
        Artifact artifact = new DefaultArtifact(gav(groupId, artifactId, version, "pom"));
        Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        assertTrue("Parent artifact must present", artifactOptional.isPresent());
        assertNotNull("File can't be null", artifactOptional.get().getFile());
        Files.deleteIfExists(artifactOptional.get().getFile().toPath());
    }

    @Test
    public void downloadOrienteerModulesFromServerTest() throws Exception {
        List<OArtifact> orienteerModulesFromServer = OrienteerClassLoaderUtil.getOrienteerArtifactsFromServer();
        for (OArtifact artifact : orienteerModulesFromServer) {
            assertNotNull("Module from server can't be null", artifact);
        }
    }

    private String gav(String groupId, String artifactId, String version, String extension) {
        return String.format("%s:%s:%s:%s", groupId, artifactId, extension, version);
    }
}
