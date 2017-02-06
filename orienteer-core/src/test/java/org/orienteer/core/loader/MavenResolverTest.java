package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.service.OLoaderInitModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class MavenResolverTest {

    private static final Logger LOG = LoggerFactory.getLogger(MavenResolverTest.class);

    private final String gavRelease  = "org.orienteer:orienteer-devutils:1.2";
    private final String gavSnapshot = "org.orienteer:orienteer-devutils:1.3-SNAPSHOT";

    private static MavenResolver resolver;

    @BeforeClass
    public static void init() {
        Injector injector = Guice.createInjector(new OLoaderInitModule());
        resolver = injector.getInstance(MavenResolver.class);
    }

    @Test
    public void resolveArtifact() throws Exception {
        Optional<Path> pathToArtifactRelease = getPathToArtifact(gavRelease);
        Optional<Path> pathToArtifactSnapshot = getPathToArtifact(gavSnapshot);
        LOG.info("**********************************************************************");
        LOG.info(String.format("Path to release %s:" +
                "\n is present: %s." +
                "\n path: %s.", gavRelease, pathToArtifactRelease.isPresent(), pathToArtifactRelease.orNull()));
        LOG.info(String.format("Path to snapshot %s:" +
                "\n is present: %s." +
                "\n path: %s.", gavSnapshot, pathToArtifactSnapshot.isPresent(), pathToArtifactSnapshot.orNull()));
        LOG.info("**********************************************************************");
    }

    @Test
    public void resolveDependenciesPomXML() throws Exception {
        Optional<Path> pathToRelease = getPathToArtifact(gavRelease);
        Optional<Path> pathToSnapshot = getPathToArtifact(gavSnapshot);
        LOG.info("**********************************************************************");
        if (pathToRelease.isPresent()) {
            LOG.info(String.format("Release %s: ", gavRelease));
            printPaths(getResolvedDependenciesFromPomXml(pathToRelease.get()));
        } else throw new Exception("Cannot get path to release " + gavRelease);

        if (pathToSnapshot.isPresent()) {
            LOG.info(String.format("Snapshot %s: ", gavSnapshot));
            printPaths(getResolvedDependenciesFromPomXml(pathToSnapshot.get()));
        } else throw new Exception("Cannot get path to release " + gavSnapshot);
        LOG.info("**********************************************************************");
    }

    @Test
    public void resolveDependenciesGAVRelease() throws Exception {
        List<Path> pathsRelease = resolver.resolveDependencies(gavRelease);

        LOG.info("**********************************************************************");
        LOG.info(String.format("Release %s: ", gavRelease));
        printPaths(pathsRelease);
        LOG.info("**********************************************************************");
    }

    @Test
    public void resolveDependenciesGAVSnapshot() throws Exception {
        List<Path> pathsSnapshot = resolver.resolveDependencies(gavSnapshot);
        LOG.info("**********************************************************************");
        LOG.info(String.format("Snapshot %s: ", gavSnapshot));
        printPaths(pathsSnapshot);
        LOG.info("**********************************************************************");
    }

    @Test
    public void resolveDependenciesFromJarSnapshot() throws Exception {
        Optional<Path> pathToSnapshot = getPathToArtifact(gavSnapshot);
        LOG.info("**********************************************************************");
        if (pathToSnapshot.isPresent()) {
            LOG.info(String.format("Snapshot %s: ", gavSnapshot));
            printPaths(resolver.resolveDependencies(pathToSnapshot.get()));
        } else throw new Exception("Cannot get path to release " + gavSnapshot);
        LOG.info("**********************************************************************");
    }

    @Test
    public void resolveDependenciesFromJarRelease() throws Exception {
        Optional<Path> pathToRelease = getPathToArtifact(gavRelease);

        LOG.info("**********************************************************************");
        if (pathToRelease.isPresent()) {
            LOG.info(String.format("Release %s: ", gavRelease));
            printPaths(resolver.resolveDependencies(pathToRelease.get()));
        } else throw new Exception("Cannot get path to release " + gavRelease);
    }


    private List<Path> getResolvedDependenciesFromPomXml(Path pathToArtifact) throws Exception{
        if (pathToArtifact == null) throw new Exception("Path to artifact can't be null!");
        Optional<Path> pomFromJar = JarUtils.getPomFromJar(pathToArtifact);

        if (!pomFromJar.isPresent()) throw new Exception(
                String.format("Pom from artifact %s is missing!", pathToArtifact.toString()));
        Path pomXml = pomFromJar.get();
        return resolver.resolveDependenciesFromPomXml(pomXml);
    }

    private void printPaths(List<Path> paths) {
        for (Path path : paths) {
            LOG.info("path:" + path);
        }
    }

    private Optional<Path> getPathToArtifact(String gav) {
        return resolver.resolveArtifact(gav);
    }
}