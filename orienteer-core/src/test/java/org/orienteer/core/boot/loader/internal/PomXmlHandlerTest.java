package org.orienteer.core.boot.loader.internal;

import com.google.inject.Inject;
import org.eclipse.aether.artifact.Artifact;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(OrienteerTestRunner.class)
public class PomXmlHandlerTest {

    private static Path pomXml;

    @Inject
    private InternalOModuleManager moduleManager;

    @BeforeClass
    public static void init() {
        pomXml = Paths.get("pom.xml");
    }

    @Test
    public void readGroupArtifactVersionInPomXml() throws Exception {
        Artifact artifact = moduleManager.readGroupArtifactVersionInPomXml(pomXml);
        assertNotNull("Artifact from pom.xml", artifact);
    }

    @Test
    public void readDependencies() {
        Path parent = Paths.get("../pom.xml");
        Set<Artifact> artifacts = moduleManager.readDependencies(parent);
        assertTrue("Dependencies from parent pom.xml", artifacts.size() > 0);
    }

}