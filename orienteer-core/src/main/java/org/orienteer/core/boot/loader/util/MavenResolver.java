package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
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
     * @param oArtifacts - modules configurations without dependencies
     * @return modules with dependencies
     */
    public List<OArtifact> setDependencies(List<OArtifact> oArtifacts) {
        for (OArtifact artifact : oArtifacts) {
            OArtifactReference artifactReference = artifact.getArtifactReference();
            List<Artifact> dependencies = OrienteerClassLoaderUtil.getResolvedDependency(
                    new Dependency(artifactReference.toAetherArtifact(), "compile"));
            artifact.setDependencies(toOArtifactDependencies(dependencies));
        }
        return oArtifacts;
    }

    /**
     * @param jars paths to unresolved modules in jar files
     * @return list with modules for write in metadata.xml
     */
    public List<OArtifact> getResolvedoArtifacts(List<Path> jars) {
        List<OArtifact> metadata = Lists.newArrayList();
        for (Path jarFile : jars) {
            Optional<OArtifact> moduleMetadata = getOArtifact(jarFile);
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
    public Optional<OArtifact> getOArtifact(Path file) {
        Optional<Path> pomXml = getPomXml(file);
        if (!pomXml.isPresent()) return Optional.absent();

        Optional<Artifact> dependencyOptional = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(pomXml.get());
        if (!dependencyOptional.isPresent()) return Optional.absent();
        Artifact artifact = dependencyOptional.get();
        List<Artifact> dependencies = OrienteerClassLoaderUtil.getResolvedDependency(new Dependency(artifact, "compile"));
        OArtifactReference artifactReference = OArtifactReference.valueOf(artifact.setFile(file.toFile()));
        OArtifact oArtifact = new OArtifact(artifactReference);
        Iterator<Artifact> iterator = dependencies.iterator();
        while (iterator.hasNext()){
            Artifact art = iterator.next();
            if (art.getGroupId().equals(artifact.getGroupId())
                    && art.getArtifactId().equals(artifact.getArtifactId())
                    && art.getVersion().equals(artifact.getVersion())) {
                iterator.remove();
                break;
            }
        }
        oArtifact.setDependencies(toOArtifactDependencies(dependencies));
        return Optional.of(oArtifact);
    }

    /**
     * Download Orienteer artifacts to target folder.
     * @param oArtifacts - artifacts for download modules
     */
    public void downloadOArtifacts(List<OArtifact> oArtifacts) {
        for (OArtifact module : oArtifacts) {
            File jar = module.getArtifactReference().getFile();
            if (jar == null || !jar.exists()) {
                Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(module.getArtifactReference().toAetherArtifact());
                if (artifactOptional.isPresent()) {
                    module.setArtifactReference(OArtifactReference.valueOf(artifactOptional.get()));
                } else {
                    module.setLoad(false);
                }
            }
        }
    }


//
//    private Optional<OArtifact> getOArtifact(String groupArtifactVersion, Path pathToJar) {
//        Optional<OArtifactReference> mainArtifact = pathToJar != null ?
//                getArtifactReference(groupArtifactVersion, pathToJar) : resolveAndGetArtifactReference(groupArtifactVersion);
//        if (!mainArtifact.isPresent()) return Optional.absent();
//        List<Artifact> artifacts = resolveDependenciesInArtifacts(groupArtifactVersion);
//
//        OArtifact moduleMetadata = new OArtifact();
//        moduleMetadata.setLoad(false)
//                .setArtifactReference(mainArtifact.get())
//                .setDependencies(toOArtifactDependencies(artifacts));
//        return Optional.of(moduleMetadata);
//    }

//    private List<Artifact> resolveDependenciesInArtifacts(String groupArtifactVersion) {
//        List<ArtifactResult> results;
//        List<Artifact> artifacts = null;
//        try {
//            results = resolveDependencies(groupArtifactVersion);
//            artifacts = getArtifactsFromArtifactResult(results);
//        } catch (ArtifactDescriptorException | DependencyCollectionException | DependencyResolutionException e) {
//            e.printStackTrace();
//        }
//        return artifacts != null ? artifacts : Lists.<Artifact>newArrayList();
//    }

//    private List<ArtifactResult> resolveDependenciesFromPomXml(Path pomXml) {
//        Set<Artifact> dependencies = OrienteerClassLoaderUtil.readDependencies(pomXml);
//        return resolveDependencies(dependencies);
//    }

    private List<Artifact> resolveDependencies(String groupArtifactVersion) throws ArtifactDescriptorException,
            DependencyCollectionException, DependencyResolutionException {
        if (Strings.isNullOrEmpty(groupArtifactVersion)) return Lists.newArrayList();

        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        return OrienteerClassLoaderUtil.getResolvedDependency(new Dependency(artifact, "compile"));
    }

//    private List<Artifact> resolveDependencies(Set<Artifact> dependencies) {
//        if (dependencies == null) return Lists.newArrayList();
//        List<Artifact> results = Lists.newArrayList();
//        results.addAll(getArtifacts(OrienteerClassLoaderUtil.downloadArtifacts(dependencies)));
//        if (resolvingRecursively)
//            results.addAll(OrienteerClassLoaderUtil.resolveArtifacts(results));
//        return results;
//    }

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
        if (!pathToArtifact.toString().endsWith(".jar"))
            return Optional.absent();
        DefaultArtifact defaultArtifact = new DefaultArtifact(groupArtifactVersion);
        return Optional.of(OArtifactReference.valueOf(defaultArtifact.setFile(pathToArtifact.toFile())));
    }

    private List<OArtifactReference> toOArtifactDependencies(List<Artifact> dependencies) {
        List<OArtifactReference> oArtifactReferenceDependencies = Lists.newArrayList();
        for (Artifact artifact : dependencies) {
            oArtifactReferenceDependencies.add(OArtifactReference.valueOf(artifact));
        }
        return oArtifactReferenceDependencies;
    }
}
