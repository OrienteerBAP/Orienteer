package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.orienteer.core.util.StartupPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
    private static final String MODULES_FOLDER               = "orienteer.loader.modules.folder";
    private static final String DEPENDENCIES_FROM_POM_XML    = "orienteer.loader.dependencies.pomXml";
    private static final String METADATA_FILE                = "metadata.xml";

    private static final String DEFAULT_MODULES_FOLDER         = System.getProperty("user.home") + "/modules/";
    private static final String DEFAULT_MAVEN_LOCAL_REPOSITORY = System.getProperty("user.home") + "/.m2/repository/";
    private static final String PARENT_POM                     = "../pom.xml";
    private static final String CORE_POM                       = "pom.xml";
    private static final Properties PROPERTIES                 = StartupPropertiesLoader.retrieveProperties();

    private final PomXmlUtils pomXmlUtils;

    public InitUtils(PomXmlUtils pomXmlUtils) {
        initOrienteerDependenciesVersions(pomXmlUtils);
        this.pomXmlUtils = pomXmlUtils;
    }


    private Map<String, String> initOrienteerDependenciesVersions(PomXmlUtils pomXmlUtils) {
        Path parentPom = Paths.get(PARENT_POM);
        Map<String, String> versions = null;
        try {
            versions = pomXmlUtils.getVersionsInProperties(Files.newInputStream(parentPom));
        } catch (IOException e) {
            LOG.error("Cannot load artifact versions from orienteer-parent pom.xml!");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        pomXmlUtils.setOrienteerVersions(versions);
        return versions;
    }

    public String getMavenLocalRepository() {
        String path = PROPERTIES.getProperty(MAVEN_LOCAL_REPOSITORY);
        return path == null ? DEFAULT_MAVEN_LOCAL_REPOSITORY : path;
    }

    public boolean isDependenciesResolveFromPomXml() {
        if (PROPERTIES == null)
            return Boolean.FALSE;
        return Boolean.valueOf(PROPERTIES.getProperty(DEPENDENCIES_FROM_POM_XML));
    }

    public Path getMetadataPath() {
        Path modulesFolder = getPathToModulesFolder();
        return modulesFolder.resolve(METADATA_FILE);
    }

    public Path getPathToModulesFolder() {
        if (PROPERTIES == null)
            return createDirectory(Paths.get(DEFAULT_MODULES_FOLDER));
        String folder = PROPERTIES.getProperty(MODULES_FOLDER);
        Path pathToModules = folder == null ? Paths.get(DEFAULT_MODULES_FOLDER) : Paths.get(folder);
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

    private Path createFile(Path pathToFile) {
        try {
            if (!Files.exists(pathToFile))
                Files.createFile(pathToFile);
        } catch (IOException e) {
            LOG.error("Cannot create file: " + pathToFile.toAbsolutePath());
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return pathToFile;
    }

    public Optional<Artifact> getOrienteerParent() {
        Path pathToPom = Paths.get(CORE_POM);
        return pomXmlUtils.readParentInPomXml(pathToPom);
    }

//    public Set<Artifact> getOrienteerParentDependencies() {
//        Path corePom = Paths.get(CORE_POM);
//        Set<Artifact> coreDependencies = pomXmlUtils.readDependencies(corePom);
//        Set<Artifact> parentDependencies = pomXmlUtils.readDependencies(Paths.get(PARENT_POM));
//        parentDependencies.addAll(coreDependencies);
//        Map<String, String> versions = pomXmlUtils.getOrienteerVersions();
//        parentDependencies.add(
//                new DefaultArtifact(String.format("%s:%s:%s",
//                        "org.orienteer", "orienteer-core", versions.get("${project.version}"))));
//        return parentDependencies;
//    }

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
}
