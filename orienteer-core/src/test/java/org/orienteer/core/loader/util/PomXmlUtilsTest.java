package org.orienteer.core.loader.util;

import com.google.common.base.Optional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orienteer.core.loader.ODependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

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
        Optional<ODependency> oDependencyOptional = PomXmlUtils.readGroupArtifactVersionInPomXml(pomXml);
        if (oDependencyOptional.isPresent()) {
            LOG.info("dependency: " + oDependencyOptional.get());
        } else throw new Exception("Cannot read dependency from pom.xml: " + pomXml);
    }

    @Test
    public void readDependencies() throws Exception {
        Set<ODependency> dependencies = PomXmlUtils.readDependencies(pomXml);
        for (ODependency dependency : dependencies) {
            LOG.info("dependency: " + dependency);
        }
    }

    @Test
    public void getArtifactVersions() throws Exception {
        Map<String, String> versions = PomXmlUtils.getVersionsInProperties(pomXml);
        if (versions.isEmpty()) throw new Exception("Cannot read versions in: " + pomXml);
        for (String key : versions.keySet()) {
            LOG.info(key + " : " + versions.get(key));
        }
    }

    @Test
    public void readGroupArtifactVersionInPomXmlParent() throws Exception {
        Optional<ODependency> oDependencyOptional = PomXmlUtils.readGroupArtifactVersionInPomXml(parentPomXml);
        if (oDependencyOptional.isPresent()) {
            LOG.info("dependency: " + oDependencyOptional.get());
        } else throw new Exception("Cannot read dependency from pom.xml: " + parentPomXml);
    }

    @Test
    public void readDependenciesParent() throws Exception {
        Set<ODependency> dependencies = PomXmlUtils.readDependencies(parentPomXml);
        for (ODependency dependency : dependencies) {
            LOG.info("dependency: " + dependency);
        }
    }

    @Test
    public void getArtifactVersionsParent() throws Exception {
        Map<String, String> versions = PomXmlUtils.getVersionsInProperties(parentPomXml);
        if (versions.isEmpty()) throw new Exception("Cannot read versions in: " + parentPomXml);
        for (String key : versions.keySet()) {
            LOG.info(key + " : " + versions.get(key));
        }
    }

}