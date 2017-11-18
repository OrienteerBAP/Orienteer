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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil.resolvingDependenciesRecursively;

/**
 * Utility class for download maven dependencies.
 */
public class MavenResolver {
	
	private static final Logger LOG = LoggerFactory.getLogger(MavenResolver.class);

    private static MavenResolver resolver;
    private final boolean resolvingRecursively;

    private static final String UNKNOWN_GROUP_ID    = "UNKNOWN_GROUP_ID_";
    private static final String UNKNOWN_ARTIFACT_ID = "UNKNOWN_ARTIFACT_ID_";
    private static final String UNKNOWN_VERSION     = "UNKNOWN_VERSION_";

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
            List<Artifact> dependencies = OrienteerClassLoaderUtil.resolveAndGetArtifactDependencies(artifactReference.toAetherArtifact());
            if (dependencies.size() != 0) {
                artifact.setDependencies(toOArtifactDependencies(dependencies));
            } else resolveDependenciesFromPomXml(getPomXml(artifactReference.getFile().toPath()));
        }
        return oArtifacts;
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
        if (pomXml == null) return getNoNameOArtifact(file);

        Artifact dependency = OrienteerClassLoaderUtil.readGroupArtifactVersionInPomXml(pomXml);
        if (dependency == null) return getNoNameOArtifact(file);

        return getOArtifact(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
                file);
    }

    private OArtifact getNoNameOArtifact(Path file) {
        if (!Files.exists(file))
            return null;
        OArtifactReference artifactReference = OArtifactReference.getEmptyOArtifactReference();
        artifactReference.setGroupId(UNKNOWN_GROUP_ID + file.getFileName());
        artifactReference.setArtifactId(UNKNOWN_ARTIFACT_ID + file.getFileName());
        artifactReference.setVersion(UNKNOWN_VERSION + file.getFileName());
        artifactReference.setFile(file.toFile());
        return new OArtifact(artifactReference).setLoad(true);
    }

    private OArtifact getOArtifact(String group, String artifact, String version,
                                                                  Path pathToMainArtifact) {

        return getOArtifact(String.format("%s:%s:%s", group, artifact, version), pathToMainArtifact);
    }

    private OArtifact getOArtifact(String groupArtifactVersion, Path pathToJar) {
        OArtifactReference mainArtifact = pathToJar != null ?
                getArtifactReference(groupArtifactVersion, pathToJar) : resolveAndGetArtifactReference(groupArtifactVersion);
        if (mainArtifact == null) return null;
        List<Artifact> artifacts = resolveDependenciesInArtifacts(groupArtifactVersion);

        OArtifact moduleMetadata = new OArtifact();
        moduleMetadata.setLoad(true)
                .setArtifactReference(mainArtifact)
                .setDependencies(toOArtifactDependencies(artifacts));
        return moduleMetadata;
    }

    private List<Artifact> resolveDependenciesInArtifacts(String groupArtifactVersion) {
        List<Artifact> results = null;
        try {
            results = resolveDependencies(groupArtifactVersion);
        } catch (ArtifactDescriptorException | DependencyCollectionException | DependencyResolutionException e) {
        	LOG.debug(e.getMessage(), e);
        }
        return results != null ? results : Lists.<Artifact>newArrayList();
    }

    private List<Artifact> resolveDependenciesFromPomXml(Path pomXml) {
        Set<Artifact> dependencies = OrienteerClassLoaderUtil.readDependencies(pomXml);
        return resolveDependencies(dependencies);
    }

    private List<Artifact> resolveDependencies(String groupArtifactVersion) throws ArtifactDescriptorException,
            DependencyCollectionException, DependencyResolutionException {
        if (Strings.isNullOrEmpty(groupArtifactVersion)) return Lists.newArrayList();

        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        return OrienteerClassLoaderUtil.resolveAndGetArtifactDependencies(artifact);
    }

    private List<Artifact> resolveDependencies(Set<Artifact> dependencies) {
        if (dependencies == null) return Lists.newArrayList();
        List<Artifact> results = Lists.newArrayList();
        results.addAll(OrienteerClassLoaderUtil.downloadArtifacts(dependencies));
        if (resolvingRecursively)
            results.addAll(OrienteerClassLoaderUtil.resolveArtifacts(results));
        return results;
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
