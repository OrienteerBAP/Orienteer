package org.orienteer.core.loader.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
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
        artifact = new DefaultArtifact(gav(groupId, artifactId, version));
    }

    @Test
    public void test() {
        String groupId = "com.github.rubenlagus";
        String artifactId = "TelegramBots";
        String version = "2.4.0";
        Artifact artifact = new DefaultArtifact(gav(groupId, artifactId, version));
        List<ArtifactResult> resolvedArtifact = OrienteerClassLoaderUtil.getResolvedArtifact(artifact);
        LOG.info("Result size: " + resolvedArtifact.size());
        for (ArtifactResult res : resolvedArtifact) {
            LOG.info("result artifact: " + res.getArtifact());
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
    public void resolveArtifacts() {
        Path path = Paths.get("/home/vetal/workspace/Orienteer/modules/pom/OTelegramBot.pom.xml");
        Set<Artifact> artifacts = OrienteerClassLoaderUtil.readDependencies(path);
        List<ArtifactResult> results = OrienteerClassLoaderUtil.downloadArtifacts(artifacts);
        for (ArtifactResult result : results) {
            LOG.info("result: " + result);
        }

        resolveArtifacts(getArtifacts(results));
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

    private String gav(String groupId, String artifactId, String version) {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }
}
