package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.http.util.Args;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Utility class for work with Eclipse Aether.
 */
class AetherUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AetherUtils.class);

    private static final String JAR_EXTENSION     = "jar";
    private static final String ARTIFACT_TEMPLATE = "%s:%s:%s:%s";

    private final RepositorySystem system;
    private final RepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    AetherUtils(InitUtils initUtils) {
        this.system = getRepositorySystem();
        this.session = getRepositorySystemSession(system, initUtils.getMavenLocalRepository());
        this.repositories = initUtils.getRemoteRepositories();
    }

    /**
     * Resolve dependency
     * @param dependency {@link Dependency} for resolve its dependencies
     * @return {@link List<Artifact>} of resolved artifact
     * @throws IllegalArgumentException if dependency is null
     */
    public List<Artifact> resolveDependency(Dependency dependency) {
        Args.notNull(dependency, "dependency");
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);
        collectRequest.setRepositories(repositories);
        List<Artifact> result = Lists.newArrayList();

        DependencyNode node = null;
        try {
            node = system.collectDependencies(session, collectRequest).getRoot();
        } catch (DependencyCollectionException e) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Can't collect dependencies for: {}", dependency, e);
            }
        }
        if (node != null) {
            DependencyRequest dependencyRequest = new DependencyRequest();
            dependencyRequest.setRoot(node);
            try {
                system.resolveDependencies(session, dependencyRequest);
            } catch (DependencyResolutionException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Can't resolve dependencies for: {}", dependency, e);
                }
            }
            PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
            node.accept(nlg);
            result = nlg.getArtifacts(false);
        }
        return result;
    }

    /**
     * Resolve artifact
     * @param artifact {@link Artifact} for resolve its dependencies
     * @return {@link List<ArtifactResult>} of resolved artifact
     * @throws IllegalArgumentException if artifact is null
     */
    public List<ArtifactResult> resolveArtifact(Artifact artifact) {
        Args.notNull(artifact, "artifact");
        ArtifactDescriptorRequest descriptorRequest = createArtifactDescriptionRequest(artifact);
        ArtifactDescriptorResult descriptorResult = null;
        try {
            descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
        } catch (ArtifactDescriptorException e) {
            if (LOG.isDebugEnabled()) LOG.debug(e.getMessage(), e);
        }
        Set<ArtifactRequest> requests = createArtifactRequests(descriptorResult);
        return resolveArtifactRequests(requests);
    }


    public List<String> requestArtifactVersions(Artifact artifact) {
        List<String> versions = Lists.newArrayList();
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(artifact);
        rangeRequest.setRepositories(repositories);
        try {
            VersionRangeResult versionResult = system.resolveVersionRange(session, rangeRequest);
            if (!versionResult.getVersions().isEmpty()) {
                String highest = versionResult.getHighestVersion().toString();
                versions.add(highest);
                for (Version version : versionResult.getVersions()) {
                    if (!highest.equals(version.toString())) versions.add(version.toString());
                }
            }
        } catch (VersionRangeResolutionException e) {
            LOG.error("Can't create version request: {}", e);
        }
        return versions;
    }

    /**
     * Download artifacts
     * @param artifacts {@link Set<Artifact>} artifacts for download
     * @return {@link List<Artifact>} of resolved artifact
     * @throws IllegalArgumentException if artifacts is null
     */
    public List<Artifact> downloadArtifacts(Set<Artifact> artifacts) {
        Args.notNull(artifacts, "artifacts");
        Set<ArtifactRequest> artifactRequests = createArtifactRequests(artifacts);
        List<ArtifactResult> artifactResults = resolveArtifactRequests(artifactRequests);
        List<Artifact> result = Lists.newArrayList();
        for (ArtifactResult res : artifactResults) {
            result.add(res.getArtifact());
        }
        return result;
    }

    /**
     * Download artifacts
     * @param artifact {@link Artifact} artifact for download
     * @return {@link Artifact} of downloaded artifact or Optional.absent() if can't download artifact
     * @throws IllegalArgumentException if artifact is null
     */
    public Artifact downloadArtifact(Artifact artifact) {
        Args.notNull(artifact, "artifact");
        ArtifactRequest artifactRequest = createArtifactRequest(artifact);
        ArtifactResult result = resolveArtifactRequest(artifactRequest);
        return result != null ? result.getArtifact() : null;
    }

    /**
     * Download artifact from repository
     * @param artifact {@link Artifact} for download
     * @param repository repository for download artifact
     * @return {@link Artifact} of downloaded artifact or Optional.absent if can't download artifact
     * @throws IllegalArgumentException if artifact or repository is null.
     */
    public Artifact downloadArtifact(Artifact artifact, String repository) {
        Args.notNull(artifact, "artifact");
        Args.notEmpty(repository, "repository");
        ArtifactRequest artifactRequest = createArtifactRequest(artifact, newUserRemoteRepository(repository));
        ArtifactResult result = resolveArtifactRequest(artifactRequest);
        return result != null ? result.getArtifact() : null;
    }

    private Set<ArtifactRequest> createArtifactRequests(ArtifactDescriptorResult descriptorResult) {
        Set<Dependency> dependencies = parseDependencies(descriptorResult.getDependencies());
        return createArtifactRequests(getArtifactFromDependencies(dependencies));
    }

    private Set<ArtifactRequest> createArtifactRequests(Set<Artifact> dependencies) {
        Set<ArtifactRequest> requests = Sets.newHashSet();
        for (Artifact dependency : dependencies) {
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

    private ArtifactRequest createArtifactRequest(Artifact artifact, RemoteRepository repository) {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(Lists.newArrayList(repository));
        return artifactRequest;
    }

    private ArtifactDescriptorRequest createArtifactDescriptionRequest(Artifact artifact) {
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact(artifact);
        descriptorRequest.setRepositories(repositories);
        return descriptorRequest;
    }

    private Set<Dependency> parseDependencies(Collection<Dependency> unchagedDeps) {
        Set<Dependency> changedDeps = Sets.newHashSet();
        for (Dependency dependency : unchagedDeps) {
            Artifact artifact = dependency.getArtifact();
            String extension = artifact.getExtension();
            if (!extension.equals(JAR_EXTENSION)) {
                dependency = getChangedDependency(artifact);
                changedDeps.add(dependency);
            } else changedDeps.add(dependency);

        }
        return changedDeps;
    }

    private ArtifactResult resolveArtifactRequest(ArtifactRequest request) {
        ArtifactResult result = null;
        try {
            result = system.resolveArtifact(session, request);
        } catch (ArtifactResolutionException e) {
            LOG.warn("Cannot resolve artifact: " + request.getArtifact());
            if (LOG.isDebugEnabled()) LOG.debug(e.getMessage(), e);
        }
        return result;
    }

    private List<ArtifactResult> resolveArtifactRequests(Set<ArtifactRequest> requests) {
        List<ArtifactResult> artifactResults = Lists.newArrayList();
        for (ArtifactRequest request : requests) {
            ArtifactResult result = resolveArtifactRequest(request);
            if (result != null) artifactResults.add(result);
        }
        return artifactResults;
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
            	LOG.error("ServiceLocator failed", exception);
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    private RepositorySystemSession getRepositorySystemSession(RepositorySystem system, String  localRepositoryPath) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository(localRepositoryPath);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

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


    private ArtifactDescriptorResult getArtifactDescription(ArtifactDescriptorRequest request) {
        ArtifactDescriptorResult descriptorResult = null;
        try {
            descriptorResult = system.readArtifactDescriptor(session, request);
        } catch (ArtifactDescriptorException e) {
            LOG.warn("Can't get artifact description: {}", request);
            if (LOG.isDebugEnabled()) LOG.debug(e.getMessage(), e);
        }
        return descriptorResult;
    }

    private Set<Artifact> getArtifactFromDependencies(Collection<Dependency> dependencies) {
        Set<Artifact> result = Sets.newHashSet();
        for (Dependency dependency : dependencies) {
            result.add(dependency.getArtifact());
        }
        return result;
    }


    private RemoteRepository newUserRemoteRepository(String repository) {
        return new RemoteRepository.Builder("user-" + repositories.size() + 1, "default", repository).build();
    }

    public void addRepository(String repository) {
        repositories.add(newUserRemoteRepository(repository));
    }

}
