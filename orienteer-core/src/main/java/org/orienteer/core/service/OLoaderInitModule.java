package org.orienteer.core.service;

import com.google.common.collect.Lists;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.orienteer.core.loader.MavenResolver;
import org.orienteer.core.loader.util.PomXmlUtils;
import org.orienteer.core.loader.util.aether.AetherUtils;
import org.orienteer.core.loader.util.aether.ConsoleRepositoryListener;
import org.orienteer.core.loader.util.aether.ConsoleTransferListener;
import org.orienteer.core.loader.util.metadata.MetadataUtil;
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
 */
public class OLoaderInitModule extends AbstractModule {

    private final static Logger LOG = LoggerFactory.getLogger(OLoaderInitModule.class);

    private static final String MAVEN_REMOTE_REPOSITORY      = "orienteer.loader.repository.remote.";
    private static final String MAVEN_REMOTE_REPOSITORY_ID   = "orienteer.loader.repository.remote.%d.id";
    private static final String MAVEN_LOCAL_REPOSITORY       = "orienteer.loader.repository.local";
    private static final String DEFAULT                      = "default";
    private static final String MODULES_FOLDER               = "orienteer.loader.modules.folder";
    private static final String METADATA_FILE                = "metadata.xml";
    private static final String METADATA_TEMP_FILE           = "metadata-temp.xml";

    private final String defaultModulesFolder             = System.getProperty("user.dir") + "/modules/";
    private final String defaultMavenLocalRepository      = System.getProperty("user.home") + "/.m2/repository/";
    private final String parentPom                        = "../pom.xml";
    private Properties properties;


    @Override
    protected void configure() {
        properties = OrienteerInitModule.retrieveProperties();
        bind(RepositorySystem.class).toProvider(RepositorySystemProvider.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("local-repo")).toProvider(new Provider<String>() {
            @Override
            public String get() {
                String path = properties.getProperty(MAVEN_LOCAL_REPOSITORY);
                return path == null ? defaultMavenLocalRepository : path;
            }
        }).in(Singleton.class);

        bind(DefaultRepositorySystemSessionProvider.class).in(Singleton.class);
        bind(RepositorySystemSession.class).to(DefaultRepositorySystemSession.class);
        bind(DefaultRepositorySystemSession.class)
                .toProvider(DefaultRepositorySystemSessionProvider.class);

        bind(Path.class).annotatedWith(Names.named("outside-modules")).toProvider(new Provider<Path>() {
            @Override
            public Path get() {
                if (properties == null)
                    return Paths.get(defaultModulesFolder);
                String folder = properties.getProperty(MODULES_FOLDER);
                return folder == null ? Paths.get(defaultModulesFolder) : Paths.get(folder);
            }
        });
        bind(Path.class).annotatedWith(Names.named("metadata-path")).toProvider(new Provider<Path>() {

            @Inject @Named("outside-modules")
            private Path modulesFolder;

            @Override
            public Path get() {
                return modulesFolder.resolve(METADATA_FILE);
            }
        });
        bind(Path.class).annotatedWith(Names.named("metadata-temp-path")).toProvider(new Provider<Path>() {
            @Inject @Named("outside-modules")
            private Path modulesFolder;

            @Override
            public Path get() {
                return modulesFolder.resolve(METADATA_TEMP_FILE);
            }
        });
        bind(MavenResolver.class).in(Singleton.class);
        requestStaticInjection(MetadataUtil.class);
        requestStaticInjection(AetherUtils.class);
    }

    @Singleton
    @Provides @Named("orienteer-default-dependencies")
    private Set<Artifact> orienteerCoreDependenciesProvider(
            @Named("orienteer-versions") Map<String, String> versions) {
        Path corePom = Paths.get( "pom.xml");
        Set<Artifact> coreDependencies = PomXmlUtils.readDependencies(corePom, versions);
        Set<Artifact> parentDependencies = PomXmlUtils.readDependencies(Paths.get(parentPom));
        parentDependencies.addAll(coreDependencies);
        parentDependencies.add(
                new DefaultArtifact(String.format("%s:%s:%s",
                        "org.orienteer", "orienteer-core", versions.get("${project.version}"))));
        return parentDependencies;
    }

    @Provides @Named("orienteer-versions")
    private Map<String, String> orienteerDependenciesVersions() {
        Path parentPom = Paths.get(this.parentPom);
        Map<String, String> versions = null;
        try {
            versions = PomXmlUtils
                    .getVersionsInProperties(Files.newInputStream(parentPom));
        } catch (IOException e) {
            LOG.error("Cannot load artifact versions from orienteer-parent pom.xml!");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return versions;
    }

    @Provides @Named("default-reps")
    private List<RemoteRepository> defaultRemoteRepositoriesProvider() {
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
            LOG.info("Read remote repositories in orienteer.properties. Remote repositories:");
            for (RemoteRepository r : repositories) {
                LOG.info("repository: " + r.toString());
            }
            if (repositories.isEmpty())
                LOG.info("In orienteer.properties does not exists any repositories. Use default repositories");
        }
        return repositories.isEmpty() ? getDefaultRepositories() : repositories;
    }

    @Singleton
    private static class RepositorySystemProvider implements Provider<RepositorySystem> {

        @Override
        public RepositorySystem get() {
            DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
            locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
            locator.addService(TransporterFactory.class, FileTransporterFactory.class);
            locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

            locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
                @Override
                public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception)
                {
                    exception.printStackTrace();
                }
            } );

            return locator.getService(RepositorySystem.class);
        }
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

    @Singleton
    private static class DefaultRepositorySystemSessionProvider implements Provider<DefaultRepositorySystemSession> {

        @Inject
        private RepositorySystem system;

        @Inject
        @Named("local-repo")
        private String localRepositoryPath;

        @Override
        public DefaultRepositorySystemSession get() {
            DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

            LocalRepository localRepo = new LocalRepository(localRepositoryPath);
            session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

            if (LOG.isDebugEnabled()) {
                session.setTransferListener(new ConsoleTransferListener());
                session.setRepositoryListener(new ConsoleRepositoryListener());
            }
            return session;
        }
    }

}
