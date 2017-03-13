package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.ArtifactResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 * Utility class for OrienteerClassLoader
 */
public abstract class OrienteerClassLoaderUtil {

    public static final String WITHOUT_JAR          = "WITHOUT_JAR";

    private static final PomXmlUtils POM_XML_UTILS  = new PomXmlUtils();
    private static final InitUtils INIT_UTILS       = new InitUtils();
    private static final JarUtils JAR_UTILS         = new JarUtils(INIT_UTILS);
    private static final AetherUtils AETHER_UTILS   = new AetherUtils(INIT_UTILS);
    private static final MetadataUtil METADATA_UTIL = new MetadataUtil(INIT_UTILS.getMetadataPath(), INIT_UTILS.getPathToModulesFolder());

    static final String POM_XML                     = "pom.xml";

    private OrienteerClassLoaderUtil() {}


    public static List<ArtifactResult> getResolvedArtifact(Artifact artifact) {
        return AETHER_UTILS.resolveArtifact(artifact);
    }

    public static List<ArtifactResult> downloadArtifacts(Set<Artifact> artifacts) {
        return AETHER_UTILS.downloadArtifacts(artifacts);
    }

    public static List<ArtifactResult> resolveArtifacts(Set<Artifact> artifacts) {
        return AETHER_UTILS.resolveArtifacts(artifacts);
    }

    public static Optional<Artifact> downloadArtifact(Artifact artifact) {
        return AETHER_UTILS.downloadArtifact(artifact);
    }

    public static Optional<Artifact> readGroupArtifactVersionInPomXml(Path pomXml) {
        return POM_XML_UTILS.readGroupArtifactVersionInPomXml(pomXml);
    }

    public static Set<Artifact> readDependencies(Path pomXml) {
        return POM_XML_UTILS.readDependencies(pomXml);
    }

    public static Optional<String> searchOrienteerInitModule(Path pathToJar) {
        return JAR_UTILS.searchOrienteerInitModule(pathToJar);
    }

    public static Optional<Path> getPomFromJar(Path path) {
        return JAR_UTILS.getPomFromJar(path);
    }

    public static List<Path> getJarsInModulesFolder() {
        return JAR_UTILS.readJarsInModulesFolder();
    }

    public static Map<Path, OModuleMetadata> getMetadataModulesInMap() {
        return METADATA_UTIL.readModulesAsMap();
    }

    public static Map<Path, OModuleMetadata> getMetadataModulesForLoadInMap() {
        return METADATA_UTIL.readModulesForLoadAsMap();
    }

    public static void createMetadata(List<OModuleMetadata> modules) {
        METADATA_UTIL.createMetadata(modules);
    }

    public static void deleteModuleFromMetadata(OModuleMetadata module) {
        METADATA_UTIL.deleteModuleFromMetadata(module);
    }

    public static void deleteModuleFromMetadata(List<OModuleMetadata> modules) {
        METADATA_UTIL.deleteModulesFromMetadata(modules);
    }

    public static void deleteMetadataFile() {
        METADATA_UTIL.deleteMetadata();
    }

    public static void updateModulesJarsInMetadata(List<OModuleMetadata> modules) {
        METADATA_UTIL.updateJarsInMetadata(modules);
    }

    public static void updateModulesInMetadata(OModuleMetadata module) {
        METADATA_UTIL.updateMetadata(module);
    }

    public static void updateModulesInMetadata(List<OModuleMetadata> modules) {
        METADATA_UTIL.updateMetadata(modules);
    }

    public static List<OModuleMetadata> getMetadataModules() {
        return METADATA_UTIL.readMetadata();
    }

    public static List<OModuleMetadata> getMetadataModulesForLoad() {
        return METADATA_UTIL.readMetadataForLoad();
    }

    static boolean resolvingDependenciesRecursively() {
        return INIT_UTILS.resolvingDependenciesRecursively();
    }

    static boolean needLoadDependenciesFromPomXml() {
        return INIT_UTILS.isDependenciesResolveFromPomXml();
    }

    static Artifact getMainArtifact() {
        Path pathToPomXml = Paths.get(POM_XML);
        Optional<Artifact> artifactOptional = POM_XML_UTILS.readMainGAVInPomXml(pathToPomXml);
        if (!artifactOptional.isPresent())
            throw new IllegalStateException("Cannot read main artifact in pom.xml (" + pathToPomXml.toAbsolutePath() + ")");
        return artifactOptional.get();
    }

    static void addOrienteerVersions(Path pathToPomXml) {
        POM_XML_UTILS.addOrienteerVersions(pathToPomXml);
    }

    static Path getModulesFolder() {
        return INIT_UTILS.getPathToModulesFolder();
    }
}