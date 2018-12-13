package org.orienteer.core.boot.loader.internal;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.eclipse.aether.repository.RemoteRepository;
import org.orienteer.core.OrienteerWebApplication;
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
public class OModulesMicroFrameworkConfig {
    private final static Logger LOG = LoggerFactory.getLogger(OModulesMicroFrameworkConfig.class);

    protected static final String MAVEN_REMOTE_REPOSITORY      = "orienteer.loader.repository.remote.";
    protected static final String MAVEN_REMOTE_REPOSITORY_ID   = "orienteer.loader.repository.remote.%d.id";
    protected static final String MAVEN_LOCAL_REPOSITORY       = "orienteer.loader.repository.local";
    protected static final String DEFAULT                      = "default";
    protected static final String LIBS_FOLDER 	               = "orienteer.loader.libs.folder";
    protected static final String RECURSIVELY_RESOLVING_DEPS   = "orienteer.loader.resolve.dependencies.recursively";
    protected static final String ORIENTEER_MODULES_URL        = "orienteer.loader.orienteer.modules.list.url";
    protected static final String ORIENTEER_MODULES_FILE       = "orienteer.loader.orienteer.modules.metadata";
    protected static final String ORIENTEER_GROUP_ID           = "orienteer.groupId";
    protected static final String ORIENTEER_ARTIFACT_ID        = "orienteer.artifactId";
    protected static final String ORIENTEER_VERSION            = "orienteer.version";
    protected static final String METADATA_FILE                = "metadata.xml";

    protected static final String DEFAULT_LIBS_FOLDER          = "libs/";
    protected static final String DEFAULT_MAVEN_LOCAL_REPOSITORY = DEFAULT_LIBS_FOLDER + "deps/";

    protected final Properties properties;

    public OModulesMicroFrameworkConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * @return maven local repository
     */
    public String getMavenLocalRepository() {
        String path = properties.getProperty(MAVEN_LOCAL_REPOSITORY);
        return path == null ? DEFAULT_MAVEN_LOCAL_REPOSITORY : path;
    }

    /**
     * @return recursively resolving dependencies property
     */
    public boolean isResolvingDependenciesRecursively() {
        if (properties == null)
            return Boolean.FALSE;
        return Boolean.valueOf(properties.getProperty(RECURSIVELY_RESOLVING_DEPS));
    }

    /**
     * @return {@link Path} of file metadata.xml
     */
    public Path getMetadataPath() {
        Path modulesFolder = getOrCreateModulesFolder();
        return modulesFolder.resolve(METADATA_FILE);
    }

    public Path getOrCreateModulesFolder() {
        return createIfNotExistsDirectory(resolvePathToModulesFolder());
    }

    /**
     * Create directory with path pathToDir
     * @param pathToDir {@link Path} of creating directory
     * @return {@link Path} of created directory
     */
    private Path createIfNotExistsDirectory(Path pathToDir) {
        try {
            if (!Files.exists(pathToDir))
                Files.createDirectory(pathToDir);
        } catch (IOException e) {
            LOG.error("Cannot create folder: " + pathToDir.toAbsolutePath(), e);
        }
        return pathToDir;
    }

    protected Path resolvePathToModulesFolder() {
        if (properties == null)
            return Paths.get(DEFAULT_LIBS_FOLDER);
        String folder = properties.getProperty(LIBS_FOLDER);
        return folder == null ? Paths.get(DEFAULT_LIBS_FOLDER) : Paths.get(folder);
    }

    /**
     * Search or create list with default Orienteer remote repositories.
     * @return list of {@link RemoteRepository}
     */
    public List<RemoteRepository> getRemoteRepositories() {
        if (properties == null)
            return getDefaultRepositories();

        List<RemoteRepository> repositories = Lists.newArrayList();
        String repository;
        int i = 1;
        while ((repository = (String) properties.get(MAVEN_REMOTE_REPOSITORY + i)) != null) {
            String id  = (String) properties.get(String.format(MAVEN_REMOTE_REPOSITORY_ID, i));
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
        return properties.getProperty(ORIENTEER_MODULES_URL);
    }

    public String getCurrentOrienteerGroupId() {
        return properties.getProperty(ORIENTEER_GROUP_ID);
    }

    public String getCurrentOrienteerArtifactId() {
        return properties.getProperty(ORIENTEER_ARTIFACT_ID);
    }

    public String getOrienteerModulesFile() {
        return properties.getProperty(ORIENTEER_MODULES_FILE);
    }

    /**
     * @return current Orienteer version
     */
    public String getOrienteerVersion() {
        String version = properties.getProperty(ORIENTEER_VERSION);
        return Strings.isEmpty(version)?OrienteerWebApplication.class.getPackage().getImplementationVersion():version;
    }
}
