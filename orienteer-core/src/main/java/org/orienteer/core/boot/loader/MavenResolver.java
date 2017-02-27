package org.orienteer.core.boot.loader;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.orienteer.core.boot.loader.util.InitUtils;
import org.orienteer.core.boot.loader.util.JarUtils;
import org.orienteer.core.boot.loader.util.ODependenciesNotResolvedException;
import org.orienteer.core.boot.loader.util.PomXmlUtils;
import org.orienteer.core.boot.loader.util.aether.AetherUtils;
import org.orienteer.core.boot.loader.util.metadata.OModuleMetadata;
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
public class MavenResolver {
    private final RepositorySystem system               = AetherUtils.getRepositorySystem();
    private final RepositorySystemSession session       = AetherUtils.getRepositorySystemSession(system, InitUtils.getMavenLocalRepository());
    private final List<RemoteRepository> repositories   = InitUtils.getRemoteRepositoriesProvider();
    private final Map<String, String> orienteerVersions = InitUtils.getOrienteerDependenciesVersions();
    private int idCounter = 0;

    private static final Logger LOG = LoggerFactory.getLogger(MavenResolver.class);

    private static MavenResolver resolver;

    private MavenResolver() {}

    /**
     * Getter for MavenResolver class. Singleton.
     * @return new instance of MavenResolver
     */
    public static MavenResolver get() {
        if (resolver == null) {
            resolver = new MavenResolver();
        }
        return resolver;
    }

    /**
     * @param modules paths to unresolved modules
     * @param depsFromPomXml choose mode of search dependencies
     *                       true - get dependencies from pom.xml in jar archive
     *                       false - get dependencies from maven repositories.
     * @return list with modules metadata for write in metadata.xml
     */
    public List<OModuleMetadata> getResolvedModulesMetadata(List<Path> modules, boolean depsFromPomXml) {
        List<OModuleMetadata> metadata = Lists.newArrayList();
        for (Path jarFile : modules) {
            Optional<OModuleMetadata> moduleMetadata = resolver.getModuleMetadata(jarFile, depsFromPomXml);
            if (moduleMetadata.isPresent()) {
                metadata.add(moduleMetadata.get());
            }
        }
        return metadata;
    }

    /**
     * @param file - path to Orienteer module pom.xml or jar archive.
     * @param depsFromPomXml choose mode of search dependencies
     *                       true - get dependencies from pom.xml in jar archive
     *                       false - get dependencies from maven repositories.
     * @return module metadata for write in metadata.xml
     */
    public Optional<OModuleMetadata> getModuleMetadata(Path file, boolean depsFromPomXml) {
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
                file, depsFromPomXml);
    }

    /**
     * @param metadata list with OModuleMetadata for resolved dependencies
     * @return true if all dependencies was resolved
     */
    public boolean resolveModuleMetadata(List<OModuleMetadata> metadata) {
        for (OModuleMetadata module : metadata) {
            boolean resolved = resolveModuleMetadata(module);
            if (!resolved) return false;
        }
        return true;
    }

    /**
     * @param moduleMetadata metadata with unresolved dependencies
     * @return true if all dependencies was resolved
     */
    public boolean resolveModuleMetadata(OModuleMetadata moduleMetadata) {
        boolean resolved = false;
        try {
            resolve(moduleMetadata.getMainArtifact());
            resolved = true;
        } catch (ODependenciesNotResolvedException e) {
            LOG.warn("Cannot resolve dependencies for " + moduleMetadata);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return resolved;
    }

    private Optional<OModuleMetadata> getModuleMetadata(String group, String artifact, String version,
                                                       Path pathToMainArtifact, boolean depsFromPomXml) {
        return getModuleMetadata(String.format("%s:%s:%s", group, artifact, version), pathToMainArtifact, depsFromPomXml);
    }

    private Optional<OModuleMetadata> getModuleMetadata(String groupArtifactVersion, Path pathToJar,
                                                       boolean depsFromPomXml) {
        Optional<Artifact> mainArtifact = pathToJar != null ?
                getArtifact(groupArtifactVersion, pathToJar) : resolveArtifact(groupArtifactVersion);
        if (!mainArtifact.isPresent()) return Optional.absent();
        List<Artifact> artifacts = resolveDependenciesInArtifacts(groupArtifactVersion, pathToJar, depsFromPomXml);
        Optional<String> initializer = getInitializer(pathToJar);
        if (!initializer.isPresent()) return Optional.absent();

        OModuleMetadata moduleMetadata = new OModuleMetadata();
        moduleMetadata.setLoad(true).setId(idCounter)
                .setInitializerName(initializer.get())
                .setMainArtifact(mainArtifact.get())
                .setDependencies(artifacts);
        idCounter++;
        return Optional.of(moduleMetadata);
    }

    private List<Artifact> resolveDependenciesInArtifacts(String groupArtifactVersion,
                                                         Path pathToJar, boolean depsFromJar) {
        List<ArtifactResult> results;
        List<Artifact> artifacts = null;
        try {
            if (depsFromJar) {
                Optional<Path> pomFromJar = JarUtils.getPomFromJar(pathToJar);
                results = resolveDependenciesFromPomXml(pomFromJar.get());
            } else results = resolveDependencies(groupArtifactVersion);
            artifacts = getArtifactsFromArtifactResult(results);
        } catch (ArtifactDescriptorException | DependencyCollectionException | DependencyResolutionException e) {
            e.printStackTrace();
        }
        return artifacts != null ? artifacts : Lists.<Artifact>newArrayList();
    }

    private List<ArtifactResult> resolve(Artifact mainDependency) throws ODependenciesNotResolvedException {
        List<ArtifactResult> dependencies;
        try {
            dependencies =  resolveDependencies(
                    mainDependency.getGroupId(), mainDependency.getArtifactId(), mainDependency.getVersion());
        } catch (DependencyCollectionException | DependencyResolutionException | ArtifactDescriptorException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
            throw new ODependenciesNotResolvedException(
                    "Cannot resolved dependencies by automatic downloading their from maven repositories");
        }
        return dependencies;
    }

    private List<ArtifactResult> resolveDependenciesFromPomXml(Path pomXml) {
        Set<Artifact> dependencies = PomXmlUtils.readDependencies(pomXml, orienteerVersions);
        return resolveDependencies(dependencies);
    }

    private List<ArtifactResult> resolveDependencies(String group, String artifact, String version)
            throws ArtifactDescriptorException, DependencyCollectionException, DependencyResolutionException {
        if (Strings.isNullOrEmpty(group) || Strings.isNullOrEmpty(artifact) || Strings.isNullOrEmpty(version)) {
            return Lists.newArrayList();
        }
        return resolveDependencies(String.format("%s:%s:%s", group, artifact, version));
    }

    private List<ArtifactResult> resolveDependencies(String groupArtifactVersion) throws ArtifactDescriptorException,
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

    private List<Artifact> getArtifactsFromArtifactResult(List<ArtifactResult> artifactResults) {
        List<Artifact> artifacts = Lists.newArrayList();
        for (ArtifactResult artifactResult : artifactResults) {
            artifacts.add(artifactResult.getArtifact());
        }
        return artifacts;
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
}
