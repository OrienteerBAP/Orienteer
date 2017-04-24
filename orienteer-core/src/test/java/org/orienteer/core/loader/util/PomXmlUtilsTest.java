package org.orienteer.core.loader.util;

import com.google.common.base.Optional;
import org.eclipse.aether.artifact.Artifact;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * @author Vitaliy Gonchar
 */
public class PomXmlUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(PomXmlUtilsTest.class);

    private static Path pomXml;
    private static Path parentPomXml;

    @BeforeClass
    public static void init() {
        String pathToPomXml = "src/test/java/org/orienteer/core/loader/util/pom.xml";
        String pathToParentPomXml = "../pom.xml";
        pomXml = Paths.get(pathToPomXml);
        parentPomXml = Paths.get(pathToParentPomXml);
    }

    @Test
    public void readGroupArtifactVersionInPomXml() throws Exception {
        Optional<Artifact> artifact = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(pomXml);
        assertEquals("Artifact from pom.xml", true, artifact.isPresent());
    }

}