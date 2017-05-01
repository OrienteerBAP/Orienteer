package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Vitaliy Gonchar
 */
@Ignore
public class DependencyTest {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyTest.class);

    private Artifact artifact;

    @Before
    public void init() {
        String groupId = "org.orienteer";
        String artifactId = "orienteer-devutils";
        String version = "1.3-SNAPSHOT";
        artifact = new DefaultArtifact(gav(groupId, artifactId, version, "jar"));
    }

    @Test
    public void test() {
        String groupId = "com.github.rubenlagus";
        String artifactId = "TelegramBots";
        String version = "2.4.0";
        Artifact artifact = new DefaultArtifact(gav(groupId, artifactId, version, "jar"));
        List<ArtifactResult> resolvedArtifact = OrienteerClassLoaderUtil.getResolvedArtifact(artifact);
        assertEquals("Size of resolved dependencies", true, resolvedArtifact.size() > 0);
        LOG.debug("Result size: " + resolvedArtifact.size());
        for (ArtifactResult res : resolvedArtifact) {
            Artifact resArtifact = res.getArtifact();
            assertNotNull("Result artifact", resArtifact);
            LOG.debug("result artifact: " + resArtifact);
        }
    }

    @Test
    public void resolveDevutils() {
        List<ArtifactResult> resolvedArtifact = OrienteerClassLoaderUtil.getResolvedArtifact(artifact);
        assertEquals("Size of resolved dependencies", true, resolvedArtifact.size() > 0);
        LOG.debug("Result size: " + resolvedArtifact.size());

        for (ArtifactResult res : resolvedArtifact) {
            Artifact resArtifact = res.getArtifact();
            assertNotNull("Result artifact", resArtifact);
            LOG.debug("Result artifact: " + resArtifact);
        }
        resolveArtifacts(getArtifacts(resolvedArtifact));
    }

    @Test
    public void downloadParentDependecy() throws Exception {
        String groupId = "org.orienteer";
        String artifactId = "orienteer-parent";
        String version = "1.2";
        Artifact artifact = new DefaultArtifact(gav(groupId, artifactId, version, "pom"));
        Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        assertEquals("Parent dependecy is not present!", true, artifactOptional.isPresent());
        LOG.debug("Parent artifact: " + artifactOptional.orNull());
    }

    @Test
    public void getModulesTest() throws Exception {
        List<OArtifact> orienteerModulesFromServer = OrienteerClassLoaderUtil.getOrienteerArtifactsFromServer();
        for (OArtifact artifact : orienteerModulesFromServer) {
            assertNotNull("Module from server cannot be null", artifact);
            LOG.debug("Module from server: " + artifact);
        }
    }

    private void resolveArtifacts(Set<Artifact> artifacts) {
        List<ArtifactResult> resolvedArtifacts = OrienteerClassLoaderUtil.resolveArtifacts(artifacts);
        assertEquals("Size of resolved dependencies cannot be 0", true, resolvedArtifacts.size() > 0);
        for (ArtifactResult result : resolvedArtifacts) {
            Artifact resultArtifact = result.getArtifact();
            assertNotNull("Result artifact cannot be null", resultArtifact);
            LOG.debug("Result result: " + resultArtifact);
        }
    }

    private Set<Artifact> getArtifacts(List<ArtifactResult> results) {
        Set<Artifact> artifacts = Sets.newHashSet();
        for (ArtifactResult result : results) {
            artifacts.add(result.getArtifact());
        }
        return artifacts;
    }

    private String gav(String groupId, String artifactId, String version, String extension) {
        return String.format("%s:%s:%s:%s", groupId, artifactId, extension, version);
    }
}
