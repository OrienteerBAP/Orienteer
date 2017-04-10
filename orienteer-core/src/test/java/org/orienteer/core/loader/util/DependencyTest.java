package org.orienteer.core.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.junit.Before;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
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
        LOG.info("Result size: " + resolvedArtifact.size());
        for (ArtifactResult res : resolvedArtifact) {
            Artifact resArtifact = res.getArtifact();
            LOG.info("result artifact: " + resArtifact);
        }
    }

    @Test
    public void resolveDevutils() {
        List<ArtifactResult> resolvedArtifact = OrienteerClassLoaderUtil.getResolvedArtifact(artifact);
        LOG.info("Result size: " + resolvedArtifact.size());

        for (ArtifactResult res : resolvedArtifact) {
            LOG.info("result artifact: " + res.getArtifact());
        }
        resolveArtifacts(getArtifacts(resolvedArtifact));
    }

    @Test
    public void downloadParentDependecy() throws Exception {
        String groupId = "org.orienteer";
        String artifactId = "orienteer-parent";
        String version = "1.3-SNAPSHOT";
        Artifact artifact = new DefaultArtifact(gav(groupId, artifactId, version, "pom"));
        Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        Artifact result;
        if (artifactOptional.isPresent()) {
            result = artifactOptional.get();
            LOG.info("Orienteer parent dependency: " + result);
        } else throw new Exception("Cannot download Orienteer parent dependency!");
    }

    @Test
    public void getModulesTest() throws Exception {
        List<OArtifact> orienteerModulesFromServer = OrienteerClassLoaderUtil.getOrienteerArtifactsFromServer();
        for (OArtifact artifact : orienteerModulesFromServer) {
            LOG.info("artifact: " + artifact);
        }
    }

    private void resolveArtifacts(Set<Artifact> artifacts) {
        List<ArtifactResult> resultList = OrienteerClassLoaderUtil.resolveArtifacts(artifacts);
        for (ArtifactResult result : resultList) {
            LOG.info("result: " + result);
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
