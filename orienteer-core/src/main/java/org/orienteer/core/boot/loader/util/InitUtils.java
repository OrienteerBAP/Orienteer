package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.eclipse.aether.repository.RemoteRepository;
import org.orienteer.core.util.StartupPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * @author Vitaliy Gonchar
 * Class for initialization resources for load outside Orienteer modules.
 */
class InitUtils {
    private final static Logger LOG = LoggerFactory.getLogger(InitUtils.class);

    private static final String MAVEN_REMOTE_REPOSITORY      = "orienteer.loader.repository.remote.";
    private static final String MAVEN_REMOTE_REPOSITORY_ID   = "orienteer.loader.repository.remote.%d.id";
    private static final String MAVEN_LOCAL_REPOSITORY       = "orienteer.loader.repository.local";
    private static final String DEFAULT                      = "default";
    private static final String LIBS_FOLDER 	             = "orienteer.loader.libs.folder";
    private static final String RECURSIVELY_RESOLVING_DEPS   = "orienteer.loader.resolve.dependencies.recursively";
    private static final String ORIENTEER_MODULES_URL        = "orienteer.loader.orienteer.modules.list.url";
    private static final String METADATA_FILE                = "metadata.xml";

    private static final String DEFAULT_LIBS_FOLDER          = "libs/";
    private static final String DEFAULT_MAVEN_LOCAL_REPOSITORY = DEFAULT_LIBS_FOLDER + "deps/";
    private static final Properties PROPERTIES                 = StartupPropertiesLoader.retrieveProperties();


    String getMavenLocalRepository() {
        String path = PROPERTIES.getProperty(MAVEN_LOCAL_REPOSITORY);
        return path == null ? DEFAULT_MAVEN_LOCAL_REPOSITORY : path;
    }

    boolean resolvingDependenciesRecursively() {
        if (PROPERTIES == null)
            return Boolean.FALSE;
        return Boolean.valueOf(PROPERTIES.getProperty(RECURSIVELY_RESOLVING_DEPS));
    }

    Path getMetadataPath() {
        Path modulesFolder = getPathToModulesFolder();
        return modulesFolder.resolve(METADATA_FILE);
    }

    Path getPathToModulesFolder() {
        if (PROPERTIES == null)
            return createDirectory(Paths.get(DEFAULT_LIBS_FOLDER));
        String folder = PROPERTIES.getProperty(LIBS_FOLDER);
        Path pathToModules = folder == null ? Paths.get(DEFAULT_LIBS_FOLDER) : Paths.get(folder);
        return createDirectory(pathToModules);
    }

    private Path createDirectory(Path pathToDir) {
        try {
            if (!Files.exists(pathToDir))
                Files.createDirectory(pathToDir);
        } catch (IOException e) {
            LOG.error("Cannot create folder: " + pathToDir.toAbsolutePath());
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return pathToDir;
    }

    List<RemoteRepository> getRemoteRepositories() {
        if (PROPERTIES == null)
            return getDefaultRepositories();

        List<RemoteRepository> repositories = Lists.newArrayList();
        String repository;
        int i = 1;
        while ((repository = (String) PROPERTIES.get(MAVEN_REMOTE_REPOSITORY + i)) != null) {
            String id  = (String) PROPERTIES.get(String.format(MAVEN_REMOTE_REPOSITORY_ID, i));
            if (id == null) id = "" + i;
            repositories.add(new RemoteRepository.Builder(id, DEFAULT, repository).build());
            i++;
        }
        if (LOG.isDebugEnabled()) {
            LOG.info("Read remote repositories in orienteer.PROPERTIES. Remote repositories:");
            for (RemoteRepository r : repositories) {
                LOG.info("repository: " + r.toString());
            }
            if (repositories.isEmpty())
                LOG.info("In orienteer.PROPERTIES does not exists any repositories. Use default repositories");
        }
        return repositories.isEmpty() ? getDefaultRepositories() : repositories;
    }

    private List<RemoteRepository> getDefaultRepositories() {
        List<RemoteRepository> repositories = Lists.newArrayList();
        repositories.add(new RemoteRepository.Builder(
                "central", "default", "http://repo1.maven.org/maven2/" ).build());
        repositories.add(new RemoteRepository.Builder(
                "sonatype-release", "default", "https://oss.sonatype.org/content/repositories/releases/").build());
        repositories.add(new RemoteRepository.Builder(
                "sonatype-snapshot", "default", "https://oss.sonatype.org/content/repositories/snapshots/").build());
        repositories.add(new RemoteRepository.Builder(
                "jitpack", "default", "https://jitpack.io/").build());
        return repositories;
    }

    String getOrienteerModulesUrl() {
        return PROPERTIES.getProperty(ORIENTEER_MODULES_URL);
    }
}
