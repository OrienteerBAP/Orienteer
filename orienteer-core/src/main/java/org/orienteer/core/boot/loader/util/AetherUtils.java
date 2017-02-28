package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.orienteer.core.boot.loader.util.aether.ConsoleRepositoryListener;
import org.orienteer.core.boot.loader.util.aether.ConsoleTransferListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 * Utility class for work with Eclipse Aether.
 */
class AetherUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AetherUtils.class);

    private static final String JAR_EXTENSION = "jar";
    private static final String ARTIFACT_TEMPLATE = "%s:%s:%s:%s";

    private final Set<Artifact> parentDependencies;
    private final RepositorySystem system;
    private final RepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    AetherUtils(InitUtils initUtils) {
        this.parentDependencies = initUtils.getOrienteerParentDependencies();
        this.system = getRepositorySystem();
        this.session = getRepositorySystemSession(system, initUtils.getMavenLocalRepository());
        this.repositories = initUtils.getRemoteRepositories();
    }

    public List<ArtifactResult> resolveArtifact(Artifact artifact) {
        ArtifactDescriptorRequest descriptorRequest = createArtifactDescriptionRequest(artifact);
        ArtifactDescriptorResult descriptorResult = null;
        try {
            descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
        } catch (ArtifactDescriptorException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        List<ArtifactRequest> requests = createArtifactRequests(descriptorResult);
        return resolveArtifactRequests(requests);
    }

    public List<ArtifactResult> downloadArtifacts(Set<Artifact> artifacts) {
        List<ArtifactRequest> artifactRequests = createArtifactRequests(artifacts);
        return resolveArtifactRequests(artifactRequests);
    }

    public Optional<Artifact> downloadArtifact(Artifact artifact) {
        ArtifactRequest artifactRequest = createArtifactRequest(artifact);
        return resolveArtifactRequest(artifactRequest);
    }

    private List<ArtifactRequest> createArtifactRequests(ArtifactDescriptorResult descriptorResult) {
        List<Dependency> dependencies = parseDependencies(descriptorResult.getDependencies());
        dependencies.addAll(parseDependencies(descriptorResult.getManagedDependencies()));
        return createArtifactRequests(dependencies);
    }

    private List<ArtifactRequest> createArtifactRequests(List<Dependency> dependencies) {
        List<ArtifactRequest> artifactRequests = Lists.newArrayList();
        dependencies = parseDependencies(dependencies);
        for (Dependency dependency : dependencies) {
            artifactRequests.add(createArtifactRequest(dependency.getArtifact()));
        }
        return artifactRequests;
    }

    private List<ArtifactRequest> createArtifactRequests(Set<Artifact> dependencies) {
        List<ArtifactRequest> requests = Lists.newArrayList();
        for (Artifact dependency : differenceWithCoreDependencies(dependencies)) {
            requests.add(createArtifactRequest(dependency));
        }
        return requests;
    }

    private ArtifactRequest createArtifactRequest(Artifact artifact) {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(repositories);
        return artifactRequest;
    }

    private ArtifactDescriptorRequest createArtifactDescriptionRequest(Artifact artifact) {
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact(artifact);
        descriptorRequest.setRepositories(repositories);
        return descriptorRequest;
    }

    private List<Dependency> parseDependencies(List<Dependency> unchagedDeps) {
        List<Dependency> changedDeps = Lists.newArrayList();
        for (Dependency dependency : unchagedDeps) {
            Artifact artifact = dependency.getArtifact();
            String extension = artifact.getExtension();
            if (containsInCoreDependencies(artifact))
                continue;

            if (!extension.equals(JAR_EXTENSION)) {
                dependency = getChangedDependency(artifact);
                changedDeps.add(dependency);
                LOG.info("Dependency.getArtifact: " + dependency.getArtifact());
            } else changedDeps.add(dependency);

        }
        return changedDeps;
    }

    private Optional<Artifact> resolveArtifactRequest(ArtifactRequest request) {
        Optional<Artifact> artifact = Optional.absent();
        try {
             ArtifactResult result = system.resolveArtifact(session, request);
             artifact = Optional.of(result.getArtifact());
        } catch (ArtifactResolutionException e) {
            LOG.error("Cannot resolve artifact: " + request.getArtifact());
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return artifact;
    }

    private List<ArtifactResult> resolveArtifactRequests(List<ArtifactRequest> requests) {
        List<ArtifactResult> artifactResults = Lists.newArrayList();
        try {
             artifactResults = system.resolveArtifacts(session, requests);
        } catch (ArtifactResolutionException e) {
            LOG.error("Cannot resolve artifact!");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        } finally {
            LOG.info(String.format("All dependencies - %d. Resolved dependencies - %d.",
                    requests.size(), artifactResults.size()));
        }
        return artifactResults;
    }

    private boolean containsInCoreDependencies(Artifact dependency) {
        for (Artifact d : parentDependencies) {
            if (d.getGroupId().equals(dependency.getGroupId())
                    && d.getArtifactId().equals(dependency.getArtifactId())
                    && d.getVersion().equals(dependency.getVersion())) {
                return true;
            }
        }
        return false;
    }

    private Set<Artifact> differenceWithCoreDependencies(Set<Artifact> dependencies) {
        Set<Artifact> artifacts = Sets.newHashSet();
        for (Artifact d : dependencies) {
           if (!containsInCoreDependencies(d)) {
               artifacts.add(d);
           }
        }
        return artifacts;
    }

    private RepositorySystem getRepositorySystem() {
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
        });

        return locator.getService(RepositorySystem.class);
    }

    private RepositorySystemSession getRepositorySystemSession(RepositorySystem system, String  localRepositoryPath) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository(localRepositoryPath);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        if (LOG.isDebugEnabled()) {
            session.setTransferListener(new ConsoleTransferListener());
            session.setRepositoryListener(new ConsoleRepositoryListener());
        }
        return session;
    }

    private Dependency getChangedDependency(Artifact artifact) {

        String groupId = artifact.getGroupId();
        String artifactId = artifact.getArtifactId();
        String versionId = artifact.getVersion();
        Artifact newArtifact = new DefaultArtifact(
                String.format(ARTIFACT_TEMPLATE, groupId, artifactId, JAR_EXTENSION, versionId));
        Dependency dependency = new Dependency(newArtifact, "");

        return dependency;
    }
}
