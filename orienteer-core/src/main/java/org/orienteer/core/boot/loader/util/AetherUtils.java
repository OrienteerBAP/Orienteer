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

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private final Set<Artifact> parentDependencies = Sets.newHashSet();
    private final RepositorySystem system;
    private final RepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    AetherUtils(InitUtils initUtils) {
        this.system = getRepositorySystem();
        this.session = getRepositorySystemSession(system, initUtils.getMavenLocalRepository());
        this.repositories = initUtils.getRemoteRepositories();
        Path localPomXml = FileSystems.getDefault().getPath("pom.xml");
        Collection<Artifact> availableArtifacts = null;
        String orienteerVersion = initUtils.getOrienteerVersion();
        if(Files.exists(localPomXml)) {
        	// We run as mvn jetty:run
        	Optional<Artifact> thisArtifact = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(localPomXml);
        	if(thisArtifact.isPresent()) {
        		availableArtifacts = OrienteerClassLoaderUtil.readDependencies(localPomXml);
        	}
        } else {
        	//we run in WAR mode
        	availableArtifacts = OrienteerClassLoaderUtil.getAvailableArtifacts(getClass().getClassLoader());
        }
        if(availableArtifacts!=null) {
	        for (Artifact artifact : availableArtifacts) {
				addOrienteerMainDependencies(artifact);
			}
        }
        if (orienteerVersion != null) {
            addOrienteerMainDependencies(getOrienteerArtifact(ORIENTEER_PARENT, orienteerVersion));
            addOrienteerMainDependencies(getOrienteerArtifact(ORIENTEER_CORE, orienteerVersion));
        }
        LOG.info("Orienteer default dependencies: " + parentDependencies.size());
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
            LOG.warn("Cannot resolve artifact: {}", request.getArtifact(), e);
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
    
    private ArtifactDescriptorResult getArtifactDescription(Artifact artifact) {
    	return getArtifactDescription(new ArtifactDescriptorRequest().setArtifact(artifact));
    }

    private ArtifactDescriptorResult getArtifactDescription(ArtifactDescriptorRequest request) {
        ArtifactDescriptorResult descriptorResult = null;
        try {
            descriptorResult = system.readArtifactDescriptor(session, request);
        } catch (ArtifactDescriptorException e) {
            LOG.warn("Can't get artifact description: {}", request, e);
        }
        return descriptorResult;
    }

    private void addOrienteerMainDependencies(Artifact artifactToDownload) {
        Optional<Artifact> artifactOptional = downloadArtifact(artifactToDownload);
        if (artifactOptional.isPresent()) {
            Artifact artifact = artifactOptional.get();
            parentDependencies.add(artifact);
            ArtifactDescriptorResult artifactDescriptor = getArtifactDescription(artifact);
            if(artifactDescriptor!=null) {
	            List<Dependency> dependencies = artifactDescriptor.getDependencies();
	            for (Dependency dependency : dependencies) {
	            	if("compile".equals(dependency.getScope())) 
	            			parentDependencies.add(dependency.getArtifact());
				}
            }
        } else LOG.warn("Can't download Orienteer artifact pom.xml! artifact: {}", artifactToDownload);
    }

    private Set<Artifact> getArtifactFromDependencies(Collection<Dependency> dependencies) {
        Set<Artifact> result = Sets.newHashSet();
        for (Dependency dependency : dependencies) {
            result.add(dependency.getArtifact());
        }
        return result;
    }

    private Artifact getOrienteerArtifact(String artifact, String version) {
        return new DefaultArtifact(
                String.format(ARTIFACT_TEMPLATE, ORIENTEER_GROUP, artifact, POM_EXTENSION, version));
    }

    private RemoteRepository newUserRemoteRepository(String repository) {
        return new RemoteRepository.Builder("user-" + repositories.size() + 1, "default", repository).build();
    }

    public void addRepository(String repository) {
        repositories.add(newUserRemoteRepository(repository));
    }
}
