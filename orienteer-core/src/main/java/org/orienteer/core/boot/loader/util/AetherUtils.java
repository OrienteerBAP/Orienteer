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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 * Utility class for work with Eclipse Aether.
 */
class AetherUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AetherUtils.class);

    private static final String JAR_EXTENSION     = "jar";
    private static final String POM_EXTENSION     = "pom";
    private static final String ARTIFACT_TEMPLATE = "%s:%s:%s:%s";
    private static final String ORIENTEER_GROUP   = "org.orienteer";
    private static final String ORIENTEER_PARENT  = "orienteer-parent";
    private static final String ORIENTEER_CORE    = "orienteer-core";
    private final Set<Artifact> parentDependencies;
    private final RepositorySystem system;
    private final RepositorySystemSession session;
    private final List<RemoteRepository> repositories;
    private Artifact parent;

    AetherUtils(InitUtils initUtils) {
        this.system = getRepositorySystem();
        this.session = getRepositorySystemSession(system, initUtils.getMavenLocalRepository());
        this.repositories = initUtils.getRemoteRepositories();
        this.parent = getOrienteerParent(OrienteerClassLoaderUtil.getMainArtifact());
        this.parentDependencies = getOrienteerMainDependencies();
    }

    public List<ArtifactResult> resolveArtifact(Artifact artifact) {
        ArtifactDescriptorRequest descriptorRequest = createArtifactDescriptionRequest(artifact);
        ArtifactDescriptorResult descriptorResult = null;
        try {
            descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
        } catch (ArtifactDescriptorException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        Set<ArtifactRequest> requests = createArtifactRequests(descriptorResult);
        return resolveArtifactRequests(requests);
    }

    public List<ArtifactResult> resolveArtifacts(Set<Artifact> artifacts) {
        List<ArtifactResult> results = Lists.newArrayList();
        for (Artifact artifact : artifacts) {
            results.addAll(resolveArtifact(artifact));
        }
        return results;
    }

    public List<ArtifactResult> downloadArtifacts(Set<Artifact> artifacts) {
        Set<ArtifactRequest> artifactRequests = createArtifactRequests(differenceWithCoreDependencies(artifacts));
        return resolveArtifactRequests(artifactRequests);
    }

    public Optional<Artifact> downloadArtifact(Artifact artifact) {
        if (containsIn(parentDependencies, artifact)) {
            return getArtifactFromSet(parentDependencies, artifact);
        }
        ArtifactRequest artifactRequest = createArtifactRequest(artifact);
        ArtifactResult result = resolveArtifactRequest(artifactRequest);
        return result != null ? Optional.of(result.getArtifact()) : Optional.<Artifact>absent();
    }

    public Optional<Artifact> downloadArtifact(Artifact artifact, String repository) {
        if (containsIn(parentDependencies, artifact)) {
            return getArtifactFromSet(parentDependencies, artifact);
        }
        ArtifactRequest artifactRequest = createArtifactRequest(artifact, newUserRemoteRepository(repository));
        ArtifactResult result = resolveArtifactRequest(artifactRequest);
        return result != null ? Optional.of(result.getArtifact()) : Optional.<Artifact>absent();
    }

    private Set<ArtifactRequest> createArtifactRequests(ArtifactDescriptorResult descriptorResult) {
        Set<Dependency> dependencies = parseDependencies(descriptorResult.getDependencies());
//        dependencies.addAll(parseDependencies(descriptorResult.getManagedDependencies()));
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
            if (containsIn(parentDependencies, artifact))
                continue;

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
            LOG.error("Cannot resolve artifact: " + request.getArtifact());
            if (LOG.isDebugEnabled()) e.printStackTrace();
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

    private boolean containsIn(Collection<Artifact> dependencies, Artifact dependency) {
        if (dependencies == null) return false;
        for (Artifact d : dependencies) {
            boolean contains = artifactsEquals(d, dependency);
            if (contains) return contains;
        }
        return false;
    }

    private boolean artifactsEquals(Artifact artifact1, Artifact artifact2) {
        if (artifact1.getGroupId().equals(artifact2.getGroupId())
                && artifact1.getArtifactId().equals(artifact2.getArtifactId())
                && artifact1.getVersion().equals(artifact2.getVersion())) {
            return true;
        } else if (artifact1.getGroupId().equals(artifact2.getGroupId())
                && artifact1.getArtifactId().equals(artifact2.getArtifactId())) {
            String versionInCoreDeps = artifact2.getVersion();
            String versionInDependency = artifact2.getVersion();
            return versionInCoreDeps.compareTo(versionInDependency) > 0 || versionInCoreDeps.compareTo(versionInCoreDeps) == 0;
        }
        return false;
    }

    private Optional<Artifact> getArtifactFromSet(Set<Artifact> set, Artifact artifact) {
        Iterator<Artifact> iterator = set.iterator();
        while (iterator.hasNext()) {
            Artifact next = iterator.next();
            if (artifactsEquals(next, artifact)) return Optional.of(next);
        }
        return Optional.absent();
    }

    private Set<Artifact> differenceWithCoreDependencies(Set<Artifact> dependencies) {
        Set<Artifact> artifacts = Sets.newHashSet();
        for (Artifact d : dependencies) {
           if (!containsIn(parentDependencies, d)) {
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

    private ArtifactDescriptorResult getArtifactDescription(ArtifactDescriptorRequest request) {
        ArtifactDescriptorResult descriptorResult = null;
        try {
            descriptorResult = system.readArtifactDescriptor(session, request);
        } catch (ArtifactDescriptorException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return descriptorResult;
    }

    private Set<Artifact> getOrienteerMainDependencies() {
//        ArtifactDescriptorRequest artifactDescriptionRequest = createArtifactDescriptionRequest(parent);
//        ArtifactDescriptorResult parentResult = getArtifactDescription(artifactDescriptionRequest);
        Optional<Artifact> parentOptional = downloadArtifact(parent);
        if (!parentOptional.isPresent()) throw new IllegalStateException("Cannot download Orienteer parent pom.xml");
        Artifact coreArtifact = getOrienteerCore(parent.getVersion());
        parent = parentOptional.get();
        Optional<Artifact> coreOptional = downloadArtifact(coreArtifact);
        if (!coreOptional.isPresent()) throw new IllegalStateException("Cannot download Orienteer core pom.xml");
        Artifact coreResult = coreOptional.get();
        Set<Artifact> parentDeps = OrienteerClassLoaderUtil.readDependencies(parent.getFile().toPath());
        OrienteerClassLoaderUtil.addOrienteerVersions(parent.getFile().toPath());
        Set<Artifact> coreDeps = OrienteerClassLoaderUtil.readDependencies(coreResult.getFile().toPath());
        parentDeps.addAll(coreDeps);
        OrienteerClassLoaderUtil.addOrienteerVersions(coreResult.getFile().toPath());
        return parentDeps;
    }

    private Set<Artifact> getArtifactFromDependencies(Collection<Dependency> dependencies) {
        Set<Artifact> result = Sets.newHashSet();
        for (Dependency dependency : dependencies) {
            result.add(dependency.getArtifact());
        }
        return result;
    }

    private Artifact getOrienteerCore(String version) {
        return new DefaultArtifact(
                String.format(ARTIFACT_TEMPLATE, ORIENTEER_GROUP, ORIENTEER_CORE, POM_EXTENSION, version));
    }

    private Artifact getOrienteerParent(Artifact parent) {

        if (!parent.getGroupId().equals(ORIENTEER_GROUP)
                || !parent.getArtifactId().equals(ORIENTEER_PARENT)
                || !parent.getExtension().equals(POM_EXTENSION)) {
            Artifact newParent = new DefaultArtifact(
                    String.format(ARTIFACT_TEMPLATE, parent.getGroupId(), parent.getArtifactId(), POM_EXTENSION, parent.getVersion()));
            return newParent;
        }
        return parent;
    }

    private RemoteRepository newUserRemoteRepository(String repository) {
        return new RemoteRepository.Builder("user-" + repositories.size() + 1, "default", repository).build();
    }

    public void addRepository(String repository) {
        repositories.add(newUserRemoteRepository(repository));
    }
}
