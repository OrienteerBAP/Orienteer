package org.orienteer.core.loader.util.aether;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
public abstract class AetherUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AetherUtils.class);

    private static final String JAR_EXTENSION = "jar";
    private static final String ARTIFACT_TEMPLATE = "%s:%s:%s:%s";

    @Inject @Named("orienteer-default-dependencies")
    private static Set<Artifact> coreDependencies;

    private static Dependency getChangedDependency(Artifact artifact) {

        String groupId = artifact.getGroupId();
        String artifactId = artifact.getArtifactId();
        String versionId = artifact.getVersion();
        Artifact newArtifact = new DefaultArtifact(
                String.format(ARTIFACT_TEMPLATE, groupId, artifactId, JAR_EXTENSION, versionId));
        Dependency dependency = new Dependency(newArtifact, "");

        return dependency;
    }

    public static List<ArtifactRequest> createArtifactRequests(ArtifactDescriptorResult descriptorResult) {
        List<Dependency> dependencies = parseDependencies(descriptorResult.getDependencies());
        dependencies.addAll(parseDependencies(descriptorResult.getManagedDependencies()));
        return createArtifactRequests(dependencies, descriptorResult.getRepositories());
    }

    public static List<ArtifactRequest> createArtifactRequests(List<Dependency> dependencies,
                                                               List<RemoteRepository> repositories) {
        List<ArtifactRequest> artifactRequests = Lists.newArrayList();
        dependencies = parseDependencies(dependencies);
        for (Dependency dependency : dependencies) {
            artifactRequests.add(createArtifactRequest(dependency.getArtifact(), repositories));
        }
        return artifactRequests;
    }

    public static List<ArtifactRequest> createArtifactRequests(Set<Artifact> dependencies,
                                                               List<RemoteRepository> repositories) {
        List<ArtifactRequest> requests = Lists.newArrayList();
        for (Artifact dependency : differenceWithCoreDependencies(dependencies)) {
            requests.add(createArtifactRequest(dependency, repositories));
        }
        return requests;
    }

    public static ArtifactRequest createArtifactRequest(Artifact artifact, List<RemoteRepository> repositories) {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(repositories);
        return artifactRequest;
    }

    public static ArtifactDescriptorRequest createArtifactDescriptionRequest(Artifact artifact,
                                                                             List<RemoteRepository> repositories) {
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact(artifact);
        descriptorRequest.setRepositories(repositories);
        return descriptorRequest;
    }

    private static List<Dependency> parseDependencies(List<Dependency> unchagedDeps) {
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

    public static Optional<Artifact> resolveArtifactRequest(ArtifactRequest request,
                                                        RepositorySystem system,
                                                        RepositorySystemSession session) {
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

    public static List<ArtifactResult> resolveArtifactRequests(List<ArtifactRequest> requests,
                                                     RepositorySystem system,
                                                     RepositorySystemSession session) {
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

    private static boolean containsInCoreDependencies(Artifact dependency) {
        for (Artifact d : coreDependencies) {
            if (d.getGroupId().equals(dependency.getGroupId())
                    && d.getArtifactId().equals(dependency.getArtifactId())
                    && d.getVersion().equals(dependency.getVersion())) {
                return true;
            }
        }
        return false;
    }

    private static Set<Artifact> differenceWithCoreDependencies(Set<Artifact> dependencies) {
        Set<Artifact> artifacts = Sets.newHashSet();
        for (Artifact d : dependencies) {
           if (!containsInCoreDependencies(d)) {
               artifacts.add(d);
           }
        }
        return artifacts;
    }
}
