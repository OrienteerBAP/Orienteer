package org.orienteer.core.boot.loader.util;

import org.eclipse.aether.artifact.Artifact;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class PomXmlUtilsTest {

    private static Path pomXml;

    @BeforeClass
    public static void init() {
        pomXml = Paths.get("pom.xml");
    }

    @Test
    public void readGroupArtifactVersionInPomXml() throws Exception {
        Artifact artifact = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(pomXml);
        assertNotNull("Artifact from pom.xml", artifact);
    }

    @Test
    public void readDependencies() {
        Path parent = Paths.get("../pom.xml");
        Set<Artifact> artifacts = OrienteerClassLoaderUtil.readDependencies(parent);
        assertTrue("Dependencies from parent pom.xml", artifacts.size() > 0);
    }

}