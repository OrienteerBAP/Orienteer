package org.orienteer.core.boot.loader.util;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
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
        artifact = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        assertNotNull("Artifact must present", artifact);
        assertNotNull("Jar file of artifact can't be null", artifact.getFile());
        List<ArtifactResult> resolvedArtifact = OrienteerClassLoaderUtil.getResolvedArtifact(artifact);
        assertEquals("Size of resolved dependencies", true, resolvedArtifact.size() > 0);

        for (ArtifactResult res : resolvedArtifact) {
            Artifact resArtifact = res.getArtifact();
            assertNotNull("Result artifact", resArtifact);
            assertNotNull("Result jar file can't be null", resArtifact.getFile());
//            Files.deleteIfExists(resArtifact.getFile().toPath());
        }
//        Files.deleteIfExists(artifactOptional.get().getFile().toPath());
    }


    @Test
    public void resolveDependencies() {
        String groupId = "org.orienteer";
        String artifactId = "orienteer-birt";
        String version   = "1.3-SNAPSHOT";
        String gav = String.format("%s:%s:%s", groupId, artifactId, version);
        Dependency dependency = new Dependency(new DefaultArtifact(gav), "compile");
        List<Artifact> resolvedDependency = OrienteerClassLoaderUtil.getResolvedDependency(dependency);
        assertTrue("resolved dependencies can't be 0", resolvedDependency.size() > 0);
        for (Artifact artifact : resolvedDependency) {
            assertTrue("artifact don't contains file or file don't exists",
                    artifact.getFile() != null && artifact.getFile().exists());
        }
    }

    @Test
    public void downloadParentDependencyTest() throws Exception {
        String groupId = "org.orienteer";
        String artifactId = "orienteer-parent";
        String version = "1.2";
        Artifact artifact = new DefaultArtifact(gav(groupId, artifactId, version, "pom"));
        artifact = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        assertNotNull("Parent artifact must present", artifact);
        assertNotNull("File can't be null", artifact.getFile());
        Files.deleteIfExists(artifact.getFile().toPath());
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
