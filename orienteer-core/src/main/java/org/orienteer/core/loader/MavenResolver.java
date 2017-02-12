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
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.loader.util.ODependenciesNotResolvedException;
import org.orienteer.core.loader.util.PomXmlUtils;
import org.orienteer.core.loader.util.aether.AetherUtils;
import org.orienteer.core.loader.util.metadata.OModuleMetadata;
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

    private int idCounter = 0;

    /**
     * @param file - path to Orienteer module pom.xml or jar archive.
     * @return module metadata for write in metadata.xml
     */
    public Optional<OModuleMetadata> getModuleMetadata(Path file) {
        Optional<Path> pomXml = getPomXml(file);
        if (!pomXml.isPresent()) return Optional.absent();
        if (!file.toString().endsWith(".jar")) {
            file = null;
        }
        Optional<Artifact> dependencyOptional = PomXmlUtils.readGroupArtifactVersionInPomXml(pomXml.get());
        if (!dependencyOptional.isPresent()) return Optional.absent();
        Artifact dependency = dependencyOptional.get();
        return getModuleMetadata(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
                file);
    }

    public Optional<OModuleMetadata> getModuleMetadata(String group, String artifact, String version,
                                                       Path pathToMainArtifact) {
        return getModuleMetadata(String.format("%s:%s:%s", group, artifact, version), pathToMainArtifact);
    }

    public Optional<OModuleMetadata> getModuleMetadata(String groupArtifactVersion, Path pathToMainArtifact) {
        Optional<Artifact> mainArtifact = pathToMainArtifact != null ?
                getArtifact(groupArtifactVersion, pathToMainArtifact) : resolveArtifact(groupArtifactVersion);
        if (!mainArtifact.isPresent()) return Optional.absent();
        List<Artifact> artifacts = resolveDependenciesInArtifacts(groupArtifactVersion);
        Optional<String> initializer = getInitializer(pathToMainArtifact);
        if (!initializer.isPresent()) return Optional.absent();

        OModuleMetadata moduleMetadata = new OModuleMetadata();
        moduleMetadata.setTrusted(false)
                .setLoad(true).setId(idCounter)
                .setInitializerName(initializer.get())
                .setMainArtifact(mainArtifact.get())
                .setDependencies(artifacts);
        idCounter++;
        return Optional.of(moduleMetadata);
    }

    private Optional<Artifact> getArtifact(String groupArtifactVersion, Path pathToArtifact) {
        if (groupArtifactVersion == null || pathToArtifact == null) return Optional.absent();
        if (!pathToArtifact.toString().endsWith(".jar")) return Optional.absent();
        DefaultArtifact defaultArtifact = new DefaultArtifact(groupArtifactVersion);
        return Optional.of(defaultArtifact.setFile(pathToArtifact.toFile()));
    }

    private Optional<String> getInitializer(Path pathToJarFile) {
        return JarUtils.searchOrienteerInitModule(pathToJarFile);
    }

    public List<Artifact> resolveDependenciesInArtifacts(String groupArtifactVersion) {
        List<ArtifactResult> results;
        List<Artifact> artifacts = null;
        try {
            results = resolveDependencies(groupArtifactVersion);
            artifacts = getArtifactsFromArtifactResult(results);
        } catch (ArtifactDescriptorException | DependencyCollectionException | DependencyResolutionException e) {
            e.printStackTrace();
        }
        return artifacts != null ? artifacts : Lists.<Artifact>newArrayList();
    }

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
        List<ArtifactResult> artifactResults;
        Optional<Artifact> dependency = PomXmlUtils.readGroupArtifactVersionInPomXml(pomXml);
        if (dependency.isPresent()) {
            try {
                artifactResults = resolve(dependency.get());
            } catch (ODependenciesNotResolvedException e) {
                LOG.info("Cannot resolved dependencies by automatic downloading their from maven repositories");
                if (LOG.isDebugEnabled()) e.printStackTrace();
                artifactResults = resolveDependenciesFromPomXml(pomXml);
            }
        } else artifactResults = resolveDependenciesFromPomXml(pomXml);

        return getPathsFromArtifactResult(artifactResults);
    }

    private List<ArtifactResult> resolve(Artifact dependency) throws ODependenciesNotResolvedException {
        List<ArtifactResult> dependencies;
        try {
            dependencies = resolveDependencies(dependency);
        } catch (DependencyCollectionException | DependencyResolutionException | ArtifactDescriptorException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
            throw new ODependenciesNotResolvedException(
                    "Cannot resolved dependencies by automatic downloading their from maven repositories");
        }
        return dependencies;
    }

    public List<ArtifactResult> resolveDependenciesFromPomXml(Path pomXml) {
        Set<Artifact> dependencies = PomXmlUtils.readDependencies(pomXml, orienteerVersions);
        return resolveDependencies(dependencies);
    }

    public List<ArtifactResult> resolveDependencies(Artifact dependency)
            throws ArtifactDescriptorException, DependencyCollectionException, DependencyResolutionException {
        if (dependency == null) {
            return Lists.newArrayList();
        }
        return resolveDependencies(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
    }

    public List<ArtifactResult> resolveDependencies(String group, String artifact, String version)
            throws ArtifactDescriptorException, DependencyCollectionException, DependencyResolutionException {
        if (Strings.isNullOrEmpty(group) || Strings.isNullOrEmpty(artifact) || Strings.isNullOrEmpty(version)) {
            return Lists.newArrayList();
        }
        return resolveDependencies(String.format("%s:%s:%s", group, artifact, version));
    }

    public List<ArtifactResult> resolveDependencies(String groupArtifactVersion) throws ArtifactDescriptorException,
            DependencyCollectionException, DependencyResolutionException {
        if (Strings.isNullOrEmpty(groupArtifactVersion)) return Lists.newArrayList();

        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        ArtifactDescriptorRequest descriptorRequest =
                AetherUtils.createArtifactDescriptionRequest(artifact, repositories);
        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
        List<ArtifactRequest> requests = AetherUtils.createArtifactRequests(descriptorResult);
        return AetherUtils.resolveArtifactRequests(requests, system, session);
    }

    private List<ArtifactResult> resolveDependencies(Set<Artifact> dependencies) {
        if (dependencies == null) return Lists.newArrayList();
        List<ArtifactRequest> requests = AetherUtils.createArtifactRequests(
                Collections.unmodifiableSet(dependencies), repositories);
        return AetherUtils.resolveArtifactRequests(requests, system, session);
    }

    public Optional<Artifact> resolveArtifact(String groupId, String artifactId, String version) {
        return resolveArtifact(String.format("%s:%s:%s", groupId, artifactId, version));
    }

    private Optional<Artifact> resolveArtifact(String groupArtifactVersion) {
        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        ArtifactRequest artifactRequest = AetherUtils.createArtifactRequest(artifact, repositories);
        return AetherUtils.resolveArtifactRequest(artifactRequest, system, session);
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

    public List<Path> getPathsFromArtifactResult(List<ArtifactResult> artifactResults) {
        List<Path> paths = Lists.newArrayList();
        if (artifactResults == null) return paths;
        for (ArtifactResult result : artifactResults) {
            paths.add(result.getArtifact().getFile().toPath());
        }
        return paths;
    }

    private List<Artifact> getArtifactsFromArtifactResult(List<ArtifactResult> artifactResults) {
        List<Artifact> artifacts = Lists.newArrayList();
        for (ArtifactResult artifactResult : artifactResults) {
            artifacts.add(artifactResult.getArtifact());
        }
        return artifacts;
    }
}
