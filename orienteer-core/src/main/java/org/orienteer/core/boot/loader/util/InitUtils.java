package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.eclipse.aether.repository.RemoteRepository;
import org.orienteer.core.OrienteerWebApplication;
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
 * Utility class for initialization resources for load outside Orienteer modules.
 * Contains all information for starting Orienteer loader micro-framework.
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
    private static final String ORIENTEER_GROUP_ID           = "orienteer.groupId";
    private static final String ORIENTEER_ARTIFACT_ID        = "orienteer.artifactId";
    private static final String ORIENTEER_VERSION            = "orienteer.version";
    private static final String METADATA_FILE                = "metadata.xml";

    private static final String DEFAULT_LIBS_FOLDER          = "libs/";
    private static final String DEFAULT_MAVEN_LOCAL_REPOSITORY = DEFAULT_LIBS_FOLDER + "deps/";
    private static final Properties PROPERTIES                 = StartupPropertiesLoader.retrieveProperties();


    /**
     * @return maven local repository
     */
    public String getMavenLocalRepository() {
        String path = PROPERTIES.getProperty(MAVEN_LOCAL_REPOSITORY);
        return path == null ? DEFAULT_MAVEN_LOCAL_REPOSITORY : path;
    }

    /**
     * @return recursively resolving dependencies property
     */
    public boolean resolvingDependenciesRecursively() {
        if (PROPERTIES == null)
            return Boolean.FALSE;
        return Boolean.valueOf(PROPERTIES.getProperty(RECURSIVELY_RESOLVING_DEPS));
    }

    /**
     * @return {@link Path} of file metadata.xml
     */
    public Path getMetadataPath() {
        Path modulesFolder = getPathToModulesFolder();
        return modulesFolder.resolve(METADATA_FILE);
    }

    /**
     * @return {@link Path} of modules folder
     */
    public Path getPathToModulesFolder() {
        if (PROPERTIES == null)
            return createDirectory(Paths.get(DEFAULT_LIBS_FOLDER));
        String folder = PROPERTIES.getProperty(LIBS_FOLDER);
        Path pathToModules = folder == null ? Paths.get(DEFAULT_LIBS_FOLDER) : Paths.get(folder);
        return createDirectory(pathToModules);
    }

    /**
     * Create directory with path pathToDir
     * @param pathToDir {@link Path} of creating directory
     * @return {@link Path} of created directory
     */
    private Path createDirectory(Path pathToDir) {
        try {
            if (!Files.exists(pathToDir))
                Files.createDirectory(pathToDir);
        } catch (IOException e) {
            LOG.error("Cannot create folder: " + pathToDir.toAbsolutePath(), e);
        }
        return pathToDir;
    }

    /**
     * Search or create list with default Orienteer remote repositories.
     * @return list of {@link RemoteRepository}
     */
    public List<RemoteRepository> getRemoteRepositories() {
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

    /**
     * @return url to modules.xml which contains descriptions of Orienteer modules in cloud
     */
    public String getOrienteerModulesUrl() {
        return PROPERTIES.getProperty(ORIENTEER_MODULES_URL);
    }

    public String getCurrentOrienteerGroupId() {
        return PROPERTIES.getProperty(ORIENTEER_GROUP_ID);
    }

    public String getCurrentOrienteerArtifactId() {
        return PROPERTIES.getProperty(ORIENTEER_ARTIFACT_ID);
    }

    /**
     * @return current Orienteer version
     */
    public String getOrienteerVersion() {
        String version = PROPERTIES.getProperty(ORIENTEER_VERSION);
        return Strings.isEmpty(version)?OrienteerWebApplication.class.getPackage().getImplementationVersion():version;
    }
}
