package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.orienteer.core.loader.util.AetherUtils;
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.loader.util.ODependenciesNotResolvedException;
import org.orienteer.core.loader.util.PomXmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public class MavenResolver {

    private static final Logger LOG = LoggerFactory.getLogger(MavenResolver.class);

    @Inject
    private RepositorySystem system;

    @Inject
    private RepositorySystemSession session;

    @Inject @Named("default-reps")
    private List<RemoteRepository> repositories;

    @Inject @Named("orienteer-versions")
    private Map<String, String> orienteerVersions;

    public List<Path> resolveDependencies(Path pathToJar) {
        if (pathToJar == null) {
            LOG.error("File path cannot be null!");
            return Lists.newArrayList();
        }
        Optional<Path> optionalPom = getPomXml(pathToJar);
        if (!optionalPom.isPresent()) {
            LOG.error("Path " + pathToJar + " is not jar or pom file!");
            return Lists.newArrayList();
        }
        Path pomXml = optionalPom.get();
        List<Path> dependencies;
        Optional<ODependency> dependency = PomXmlUtils.readGroupArtifactVersionInPomXml(pomXml);
        if (dependency.isPresent()) {
            try {
                dependencies = resolve(dependency.get());
            } catch (ODependenciesNotResolvedException e) {
                LOG.info("Cannot resolved dependencies by automatic downloading their from maven repositories");
                if (LOG.isDebugEnabled()) e.printStackTrace();
                dependencies = resolveDependenciesFromPomXml(pomXml);
            }
        } else dependencies = resolveDependenciesFromPomXml(pomXml);
        return dependencies;
    }

    private List<Path> resolve(ODependency dependency) throws ODependenciesNotResolvedException {
        List<Path> dependencies;
        try {
            dependencies = resolveDependencies(dependency);
        } catch (DependencyCollectionException | DependencyResolutionException | ArtifactDescriptorException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
            throw new ODependenciesNotResolvedException(
                    "Cannot resolved dependencies by automatic downloading their from maven repositories");
        }
        return dependencies;
    }

    public List<Path> resolveDependenciesFromPomXml(Path pomXml) {
        Set<ODependency> dependencies = PomXmlUtils.readDependencies(pomXml, orienteerVersions);
        return resolveDependencies(dependencies);
    }

    public List<Path> resolveDependencies(ODependency dependency)
            throws ArtifactDescriptorException, DependencyCollectionException, DependencyResolutionException {
        if (dependency == null) {
            return Lists.newArrayList();
        }
        return resolveDependencies(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getArtifactVersion());
    }

    public List<Path> resolveDependencies(String group, String artifact, String version)
            throws ArtifactDescriptorException, DependencyCollectionException, DependencyResolutionException {
        if (Strings.isNullOrEmpty(group) || Strings.isNullOrEmpty(artifact) || Strings.isNullOrEmpty(version)) {
            return Lists.newArrayList();
        }
        return resolveDependencies(String.format("%s:%s:%s", group, artifact, version));
    }

    public List<Path> resolveDependencies(String groupArtifactVersion) throws ArtifactDescriptorException,
            DependencyCollectionException, DependencyResolutionException {
        if (Strings.isNullOrEmpty(groupArtifactVersion)) return Lists.newArrayList();

        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        ArtifactDescriptorRequest descriptorRequest =
                AetherUtils.createArtifactDescriptionRequest(artifact, repositories);
        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
        List<ArtifactRequest> requests = AetherUtils.createArtifactRequests(descriptorResult);

        return AetherUtils.resolveArtifactRequests(requests, system, session);
    }

    public List<Path> resolveDependencies(Set<ODependency> dependencies) {
        if (dependencies == null) return Lists.newArrayList();
        List<ArtifactRequest> requests = AetherUtils.createArtifactRequests(
                Collections.unmodifiableSet(dependencies), repositories);

        return AetherUtils.resolveArtifactRequests(requests, system, session);
    }

    public Optional<Path> resolveArtifact(String groupArtifactVersion) {
        if (groupArtifactVersion == null) return Optional.absent();

        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        ArtifactRequest request = AetherUtils.createArtifactRequest(artifact, repositories);
        return AetherUtils.resolveArtifactRequest(request, system, session);
    }

    private Optional<Path> getPomXml(Path file) {
        Optional<Path> pomXml = Optional.absent();
        if (file.toString().endsWith(".xml")) {
            pomXml = Optional.of(file);
        } else if (file.toString().endsWith(".jar")) {
            pomXml = JarUtils.getPomFromJar(file);
        }
        return pomXml;
    }
}
