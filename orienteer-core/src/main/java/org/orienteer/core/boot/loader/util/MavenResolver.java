package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil.getModulesFolder;
import static org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil.resolvingDependenciesRecursively;

/**
 * @author Vitaliy Gonchar
 */
public class MavenResolver {
    private static final Logger LOG = LoggerFactory.getLogger(MavenResolver.class);

    private static MavenResolver resolver;
    private final Path modulesFolder;
    private final boolean resolvingRecursively;

    private MavenResolver(Path modulesFolder, boolean resolvingRecursively) {
        this.modulesFolder = modulesFolder;
        this.resolvingRecursively = resolvingRecursively;
    }

    /**
     * Getter for MavenResolver class. Singleton.
     * @return new instance of MavenResolver
     */
    public static MavenResolver get() {
        if (resolver == null) {
            resolver = new MavenResolver(getModulesFolder(), resolvingDependenciesRecursively());
        }
        return resolver;
    }

    /**
     * Set dependency fot Orienteer modules
     * @param modulesConfigs - modules configurations without dependencies
     * @return modules with dependencies
     */
    public List<OArtifact> setDependencies(List<OArtifact> modulesConfigs) {
        for (OArtifact module : modulesConfigs) {
            OArtifactReference artifact = module.getArtifact();
            File moduleJar = artifact.getFile();
            Optional<Path> pomFromJar = OrienteerClassLoaderUtil.getPomFromJar(moduleJar.toPath());
            if (pomFromJar.isPresent()) {
                List<Artifact> dependencies = getArtifactsFromArtifactResult(resolveDependenciesFromPomXml(pomFromJar.get()));
                module.setDependencies(toOrienteerArtifactDependencies(dependencies));
            }
        }
        return modulesConfigs;
    }

    /**
     * @param jars paths to unresolved modules in jar files
     * @return list with modules metadata for write in metadata.xml
     */
    public List<OArtifact> getResolvedModulesConfigurations(List<Path> jars) {
        List<OArtifact> metadata = Lists.newArrayList();
        for (Path jarFile : jars) {
            Optional<OArtifact> moduleMetadata = getModuleConfiguration(jarFile);
            if (moduleMetadata.isPresent()) {
                metadata.add(moduleMetadata.get());
            }
        }
        return metadata;
    }

    /**
     * @param file - path to Orienteer module pom.xml or jar archive.
     * @return module metadata for write in metadata.xml
     */
    public Optional<OArtifact> getModuleConfiguration(Path file) {
        Optional<Path> pomXml = getPomXml(file);
        if (!pomXml.isPresent()) return Optional.absent();
        if (!file.toString().endsWith(".jar")) {
            file = null;
        }

        Optional<Artifact> dependencyOptional = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(pomXml.get());
        if (!dependencyOptional.isPresent()) return Optional.absent();
        Artifact dependency = dependencyOptional.get();

        return getModuleConfiguration(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
                file);
    }

    /**
     * Download Orienteer modules to target folder.
     * @param modulesConfigurations - modules configurations for download modules
     */
    public void downloadModules(List<OArtifact> modulesConfigurations) {
        for (OArtifact module : modulesConfigurations) {
            File jar = module.getArtifact().getFile();
            if (jar == null || !jar.exists()) {
                Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(module.getArtifact().toAetherArtifact());
                if (artifactOptional.isPresent()) {
                    module.setArtifact(OArtifactReference.valueOf(artifactOptional.get()));
                } else {
                    module.setLoad(false);
                }
            }
        }
    }

    /**
     * Move Orienteer module to modules folder
     * @param pathToJar - path to Orienteer module
     * @return path to module in modules folder
     */
    private File moveToModulesFolder(Path pathToJar) {
        Path result = modulesFolder.resolve(pathToJar.getFileName());
        try {
            if (!Files.exists(result)) Files.move(pathToJar, result);
        } catch (IOException e) {
            LOG.warn("Cannot move modules jar to modules folder!", e);
        }
        return result.toFile();
    }

    private Optional<OArtifact> getModuleConfiguration(String group, String artifact, String version,
                                                                  Path pathToMainArtifact) {
        return getModuleConfiguration(String.format("%s:%s:%s", group, artifact, version), pathToMainArtifact);
    }

    private Optional<OArtifact> getModuleConfiguration(String groupArtifactVersion, Path pathToJar) {
        Optional<OArtifactReference> mainArtifact = pathToJar != null ?
                getArtifactReference(groupArtifactVersion, pathToJar) : resolveAndGetArtifactReference(groupArtifactVersion);
        if (!mainArtifact.isPresent()) return Optional.absent();
        List<Artifact> artifacts = resolveDependenciesInArtifacts(groupArtifactVersion, pathToJar);

        OArtifact moduleMetadata = new OArtifact();
        moduleMetadata.setLoad(false)
                .setArtifact(mainArtifact.get())
                .setDependencies(toOrienteerArtifactDependencies(artifacts));
        return Optional.of(moduleMetadata);
    }

    private List<Artifact> resolveDependenciesInArtifacts(String groupArtifactVersion, Path pathToJar) {
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
        Set<Artifact> dependencies = OrienteerClassLoaderUtil.readDependencies(pomXml);
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
        return OrienteerClassLoaderUtil.getResolvedArtifact(artifact);
    }

    private List<ArtifactResult> resolveDependencies(Set<Artifact> dependencies) {
        if (dependencies == null) return Lists.newArrayList();
        List<ArtifactResult> results = Lists.newArrayList();
        results.addAll(OrienteerClassLoaderUtil.downloadArtifacts(dependencies));
        if (resolvingRecursively)
            results.addAll(OrienteerClassLoaderUtil.resolveArtifacts(getArtifacts(results)));
        return results;
    }

    private Set<Artifact> getArtifacts(List<ArtifactResult> results) {
        Set<Artifact> artifacts = Sets.newHashSet();
        for (ArtifactResult result : results) {
            artifacts.add(result.getArtifact());
        }
        return artifacts;
    }

    private Optional<OArtifactReference> resolveAndGetArtifactReference(String groupArtifactVersion) {
        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        return artifactOptional.isPresent() ? Optional.of(OArtifactReference.valueOf(artifactOptional.orNull())) : Optional.<OArtifactReference>absent();
    }

    private Optional<Path> getPomXml(Path file) {
        Optional<Path> pomXml = Optional.absent();
        if (file.toString().endsWith(".xml")) {
            pomXml = Optional.of(file);
        } else if (file.toString().endsWith(".jar")) {
            pomXml = OrienteerClassLoaderUtil.getPomFromJar(file);
        }
        return pomXml;
    }

    private List<Artifact> getArtifactsFromArtifactResult(List<ArtifactResult> artifactResults) {
        List<Artifact> artifacts = Lists.newArrayList();
        for (ArtifactResult artifactResult : artifactResults) {
            if (artifactResult != null) artifacts.add(artifactResult.getArtifact());
        }
        return artifacts;
    }

    private Optional<OArtifactReference> getArtifactReference(String groupArtifactVersion, Path pathToArtifact) {
        if (groupArtifactVersion == null || pathToArtifact == null) return Optional.absent();
        if (!pathToArtifact.toString().endsWith(".jar")) return Optional.absent();
        DefaultArtifact defaultArtifact = new DefaultArtifact(groupArtifactVersion);
        return Optional.of(OArtifactReference.valueOf(defaultArtifact.setFile(pathToArtifact.toFile())));
    }

    private List<OArtifactReference> toOrienteerArtifactDependencies(List<Artifact> dependencies) {
        List<OArtifactReference> oArtifactReferenceDependencies = Lists.newArrayList();
        for (Artifact artifact : dependencies) {
            oArtifactReferenceDependencies.add(OArtifactReference.valueOf(artifact));
        }
        return oArtifactReferenceDependencies;
    }
}
