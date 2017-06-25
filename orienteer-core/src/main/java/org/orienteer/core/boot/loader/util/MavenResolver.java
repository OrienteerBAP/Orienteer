package org.orienteer.core.boot.loader.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil.resolvingDependenciesRecursively;

/**
 * Utility class for download maven dependencies.
 */
public class MavenResolver {

    private static MavenResolver resolver;
    private final boolean resolvingRecursively;

    private MavenResolver(boolean resolvingRecursively) {
        this.resolvingRecursively = resolvingRecursively;
    }

    /**
     * Getter for MavenResolver class. Singleton.
     * @return new instance of MavenResolver
     */
    public static MavenResolver get() {
        if (resolver == null) {
            resolver = new MavenResolver(resolvingDependenciesRecursively());
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
            OArtifactReference artifact = module.getArtifactReference();
            File moduleJar = artifact.getFile();
            Path pomFromJar = OrienteerClassLoaderUtil.getPomFromJar(moduleJar.toPath());
            if (pomFromJar != null) {
                List<Artifact> dependencies = getArtifactsFromArtifactResult(resolveDependenciesFromPomXml(pomFromJar));
                module.setDependencies(toOArtifactDependencies(dependencies));
            }
        }
        return modulesConfigs;
    }

    /**
     * @param jars paths to unresolved modules in jar files
     * @return list with modules for write in metadata.xml
     */
    public List<OArtifact> getResolvedOArtifacts(List<Path> jars) {
        List<OArtifact> metadata = Lists.newArrayList();
        for (Path jarFile : jars) {
            OArtifact moduleMetadata = getOArtifact(jarFile);
            if (moduleMetadata != null) {
                metadata.add(moduleMetadata);
            }
        }
        return metadata;
    }

    /**
     * @param file - path to Orienteer module pom.xml or jar archive.
     * @return module metadata for write in metadata.xml
     */
    public OArtifact getOArtifact(Path file) {
        Path pomXml = getPomXml(file);
        if (pomXml == null) return null;

        Artifact dependency = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(pomXml);
        if (dependency == null) return null;

        return getOArtifact(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
                file);
    }

    /**
     * Download Orienteer artifacts to target folder.
     * @param oArtifacts - artifacts for download modules
     */
    public void downloadOArtifacts(List<OArtifact> oArtifacts) {
        for (OArtifact module : oArtifacts) {
            File jar = module.getArtifactReference().getFile();
            if (jar == null || !jar.exists()) {
                Artifact artifact = OrienteerClassLoaderUtil.downloadArtifact(module.getArtifactReference().toAetherArtifact());
                if (artifact != null) {
                    module.setArtifactReference(OArtifactReference.valueOf(artifact));
                } else {
                    module.setLoad(false);
                }
            }
        }
    }

    private OArtifact getOArtifact(String group, String artifact, String version,
                                                                  Path pathToMainArtifact) {
        return getOArtifact(String.format("%s:%s:%s", group, artifact, version), pathToMainArtifact);
    }

    private OArtifact getOArtifact(String groupArtifactVersion, Path pathToJar) {
        OArtifactReference mainArtifact = pathToJar != null ?
                getArtifactReference(groupArtifactVersion, pathToJar) : resolveAndGetArtifactReference(groupArtifactVersion);
        if (mainArtifact == null) return null;;
        List<Artifact> artifacts = resolveDependenciesInArtifacts(groupArtifactVersion);

        OArtifact moduleMetadata = new OArtifact();
        moduleMetadata.setLoad(false)
                .setArtifactReference(mainArtifact)
                .setDependencies(toOArtifactDependencies(artifacts));
        return moduleMetadata;
    }

    private List<Artifact> resolveDependenciesInArtifacts(String groupArtifactVersion) {
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

    private List<ArtifactResult> resolveDependenciesFromPomXml(Path pomXml) {
        Set<Artifact> dependencies = OrienteerClassLoaderUtil.readDependencies(pomXml);
        return resolveDependencies(dependencies);
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

    private OArtifactReference resolveAndGetArtifactReference(String groupArtifactVersion) {
        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        Artifact downloadedArtifact = OrienteerClassLoaderUtil.downloadArtifact(artifact);
        return downloadedArtifact!=null ? OArtifactReference.valueOf(downloadedArtifact) : null;
    }

    private Path getPomXml(Path file) {
        Path pomXml = null;
        if (file.toString().endsWith(".xml")) {
            pomXml = file;
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

    private OArtifactReference getArtifactReference(String groupArtifactVersion, Path pathToArtifact) {
        if (groupArtifactVersion == null || pathToArtifact == null) return null;
        if (!pathToArtifact.toString().endsWith(".jar")) return null;
        DefaultArtifact defaultArtifact = new DefaultArtifact(groupArtifactVersion);
        return OArtifactReference.valueOf(defaultArtifact.setFile(pathToArtifact.toFile()));
    }

    private List<OArtifactReference> toOArtifactDependencies(List<Artifact> dependencies) {
        List<OArtifactReference> oArtifactReferenceDependencies = Lists.newArrayList();
        for (Artifact artifact : dependencies) {
            oArtifactReferenceDependencies.add(OArtifactReference.valueOf(artifact));
        }
        return oArtifactReferenceDependencies;
    }
}
