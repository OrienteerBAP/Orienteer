package org.orienteer.core.service;

import com.google.common.collect.Lists;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.orienteer.core.loader.Dependency;
import org.orienteer.core.loader.MavenResolver;
import org.orienteer.core.loader.OrienteerOutsideModulesManager;
import org.orienteer.core.loader.OrienteerOutsideModules;
import org.orienteer.core.loader.util.ConsoleRepositoryListener;
import org.orienteer.core.loader.util.ConsoleTransferListener;
import org.orienteer.core.loader.util.PomXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.proxy.CglibProxyProvider;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;

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
public class OModuleExecutorInitModule extends AbstractModule {


    private final static Logger LOG = LoggerFactory.getLogger(OModuleExecutorInitModule.class);

    private final String localRepositoryPath = System.getProperty("user.home") + "/.m2/repository/";
//    private final String localRepositoryPath = System.getProperty("user.dir") + "/tmp/dependencies/";
    private final String parentOrienteerPom = "../pom.xml";

    private final Properties properties;

    public OModuleExecutorInitModule(Properties properties) {
        this.properties = properties;
    }

    public OModuleExecutorInitModule() {
        this.properties = null;
    }

    @Override
    protected void configure() {
        bind(RepositorySystem.class).toProvider(RepositorySystemProvider.class).in(Singleton.class);
        bindConstant().annotatedWith(Names.named("local-repo")).to(localRepositoryPath);
        bindConstant().annotatedWith(Names.named("parent-pom")).to(parentOrienteerPom);
        bind(DefaultRepositorySystemSessionProvider.class).in(Singleton.class);
        bind(RepositorySystemSession.class).to(DefaultRepositorySystemSession.class);
        bind(DefaultRepositorySystemSession.class)
                .toProvider(DefaultRepositorySystemSessionProvider.class)
                .in(Singleton.class);
        bind(Path.class).annotatedWith(Names.named("jars")).toProvider(new Provider<Path>() {
            @Override
            public Path get() {
                if (properties == null)
                    return Paths.get(System.getProperty("user.dir") + "/tmp/");
                String property = properties.getProperty("orienteer.loader.jar.folder");
                Path path;
                if (property == null) path = Paths.get(System.getProperty("user.dir" + "/tmp/"));
                else path = Paths.get(properties.getProperty("orienteer.loader.jar.folder"));
                return path;
            }
        });
        bind(OrienteerOutsideModulesManager.class).in(Singleton.class);
        bind(MavenResolver.class).in(Singleton.class);
        requestStaticInjection(OrienteerOutsideModules.class);
        ProxyProviderFactory.setDefaultProxyProvider(new CglibProxyProvider());
    }

    @Singleton
    @Provides @Named("orienteer-default-dependencies")
    private Set<Dependency> orienteerCoreDependenciesProvider(
            @Named("orienteer-versions") Map<String, String> versions, @Named("parent-pom") String parentPom) {
        Path corePom = Paths.get( "pom.xml");
        Set<Dependency> coreDependencies = PomXmlParser.readDependencies(corePom, versions);
        Set<Dependency> parentDependencies = PomXmlParser.readDependencies(Paths.get(parentPom));
        parentDependencies.addAll(coreDependencies);
        parentDependencies.add(new Dependency("org.orienteer", "orienteer-core", versions.get("${project.version}")));
        return parentDependencies;
    }

    @Provides @Named("orienteer-versions")
    private Map<String, String> orienteerDependenciesVersions() {
        Path parentPom = Paths.get(parentOrienteerPom);
        Map<String, String> versions = null;
        try {
            versions = PomXmlParser
                    .getArtifactVersions(Files.newInputStream(parentPom));
        } catch (IOException e) {
            LOG.error("Cannot load artifact versions from orienteer-parent pom.xml!");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return versions;
    }

    @Provides @Named("default-reps")
    private List<RemoteRepository> defaultRemoteRepositoriesProvider() {
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
    private static class RepositorySystemProvider implements Provider<RepositorySystem> {

        @Override
        public RepositorySystem get() {
            DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
            locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
            locator.addService( TransporterFactory.class, FileTransporterFactory.class );
            locator.addService( TransporterFactory.class, HttpTransporterFactory.class );

            locator.setErrorHandler( new DefaultServiceLocator.ErrorHandler()
            {
                @Override
                public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )
                {
                    exception.printStackTrace();
                }
            } );

            return locator.getService( RepositorySystem.class );
        }
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

            LocalRepository localRepo = new LocalRepository( localRepositoryPath );
            session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

            session.setTransferListener(new ConsoleTransferListener());
            session.setRepositoryListener(new ConsoleRepositoryListener());

            // uncomment to generate dirty trees
            // session.setDependencyGraphTransformer( null );

            return session;
        }
    }

}
