package org.orienteer.core.loader.util;

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
import org.orienteer.core.loader.ODependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
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
    private static Set<ODependency> coreDependencies;

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

    public static List<ArtifactRequest> createArtifactRequests(Set<ODependency> dependencies,
                                                               List<RemoteRepository> repositories) {
        List<ArtifactRequest> requests = Lists.newArrayList();
        for (ODependency dependency : Sets.difference(coreDependencies, dependencies)) {
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();
            String version = dependency.getArtifactVersion();
            Artifact artifact = new DefaultArtifact(
                    String.format(ARTIFACT_TEMPLATE, groupId, artifactId, JAR_EXTENSION, version));
            requests.add(createArtifactRequest(artifact, repositories));
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
            if (coreDependencies.contains(getODependency(artifact)))
                continue;

            if (!extension.equals(JAR_EXTENSION)) {
                dependency = getChangedDependency(artifact);
                changedDeps.add(dependency);
                LOG.info("Dependency.getArtifact: " + dependency.getArtifact());
            } else changedDeps.add(dependency);

        }
        return changedDeps;
    }

    private static ODependency getODependency(Artifact artifact) {
        return new ODependency(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    public static Optional<Path> resolveArtifactRequest(ArtifactRequest request,
                                                        RepositorySystem system,
                                                        RepositorySystemSession session) {
        Optional<Path> path = Optional.absent();
        try {
             ArtifactResult result = system.resolveArtifact(session, request);
             path = Optional.of(result.getArtifact().getFile().toPath());
        } catch (ArtifactResolutionException e) {
            LOG.error("Cannot resolve artifact: " + request.getArtifact());
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return path;
    }

    public static List<Path> resolveArtifactRequests(List<ArtifactRequest> requests,
                                                     RepositorySystem system,
                                                     RepositorySystemSession session) {
        List<Path> artifactPaths = Lists.newArrayList();
        try {
            List<ArtifactResult> artifactResults = system.resolveArtifacts(session, requests);
            for (ArtifactResult result : artifactResults) {
                artifactPaths.add(result.getArtifact().getFile().toPath());
            }
        } catch (ArtifactResolutionException e) {
            LOG.error("Cannot resolve artifact!");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        } finally {
            LOG.info(String.format("All dependencies - %d. Resolved dependencies - %d.",
                    requests.size(), artifactPaths.size()));
        }
        return artifactPaths;
    }
}
