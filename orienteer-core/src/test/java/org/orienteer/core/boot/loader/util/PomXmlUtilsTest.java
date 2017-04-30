package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import org.eclipse.aether.artifact.Artifact;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * @author Vitaliy Gonchar
 */
public class PomXmlUtilsTest {

    private static Path pomXml;

    @BeforeClass
    public static void init() {
        String pathToPomXml = "src/test/java/org/orienteer/core/loader/util/pom.xml";
        pomXml = Paths.get(pathToPomXml);
    }

    @Test
    public void readGroupArtifactVersionInPomXml() throws Exception {
        Optional<Artifact> artifact = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(pomXml);
        assertEquals("Artifact from pom.xml", true, artifact.isPresent());
    }
}