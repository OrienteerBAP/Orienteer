package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    static final String MODULES                     = "modules.xml";

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoaderUtil.class);

    private OrienteerClassLoaderUtil() {}

    public static List<OModule> getOrienteerModulesFromServer() {
        HttpDownloader downloader = new HttpDownloader(INIT_UTILS.getOrienteerModulesUrl(), INIT_UTILS.getPathToModulesFolder());
        Path download = downloader.download(MODULES);
        if (download == null) return Lists.newArrayList();
        OrienteerArtifactsReader reader = new OrienteerArtifactsReader(download);
        List<OModule> oModules = reader.readModules();
        List<OModule> metadataModules = getMetadataModules();
        for (OModule metadataModule : metadataModules) {
            for (OModule module : oModules) {
                OArtifact metadataArtifact = metadataModule.getArtifact();
                OArtifact moduleArtifact = module.getArtifact();
                if (metadataArtifact.equals(moduleArtifact)) {
                    module.setDownloaded(true);
                }
            }
        }
        return oModules;
    }

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

    public static Optional<Artifact> downloadArtifact(Artifact artifact, String repository) {
        return AETHER_UTILS.downloadArtifact(artifact, repository);
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

    public static Optional<OModule> getOModuleFromJar(Path pathToJar) {
        return MavenResolver.get().getModuleMetadata(pathToJar);
    }

    public static List<Path> getJarsInModulesFolder() {
        return JAR_UTILS.readJarsInModulesFolder();
    }

    public static Map<Path, OModule> getMetadataModulesInMap() {
        return METADATA_UTIL.readModulesAsMap();
    }

    public static Map<Path, OModule> getMetadataModulesForLoadInMap() {
        return METADATA_UTIL.readModulesForLoadAsMap();
    }

    public static void createMetadata(List<OModule> modules) {
        METADATA_UTIL.createMetadata(modules);
    }

    public static void deleteModuleFromMetadata(OModule module) {
        METADATA_UTIL.deleteModuleFromMetadata(module);
    }

    public static void deleteModulesFromMetadata(List<OModule> modules) {
        METADATA_UTIL.deleteModulesFromMetadata(modules);
    }

    public static void deleteModuleArtifact(OModule module) {
        try {
            Files.deleteIfExists(module.getArtifact().getFile().toPath());
        } catch (IOException e) {
            LOG.error("Cannot delete artifact of module: " + module, e);
        }
    }

    public static void deleteMetadataFile() {
        METADATA_UTIL.deleteMetadata();
    }

    public static void updateModulesJarsInMetadata(List<OModule> modules) {
        METADATA_UTIL.updateJarsInMetadata(modules);
    }

    public static void updateModulesInMetadata(OModule module) {
        METADATA_UTIL.updateMetadata(module);
    }

    public static void updateModulesInMetadata(OModule moduleForUpdate, OModule newModule) {
        METADATA_UTIL.updateMetadata(moduleForUpdate, newModule);
    }

    public static void updateModulesInMetadata(List<OModule> modules) {
        METADATA_UTIL.updateMetadata(modules);
    }

    public static List<OModule> getMetadataModules() {
        return METADATA_UTIL.readMetadata();
    }

    public static List<OModule> getMetadataModulesForLoad() {
        return METADATA_UTIL.readMetadataForLoad();
    }

    static boolean resolvingDependenciesRecursively() {
        return INIT_UTILS.resolvingDependenciesRecursively();
    }


    public static Artifact getMainArtifact() {
        Path pathToPomXml = Paths.get(POM_XML);
        Optional<Artifact> artifactOptional = POM_XML_UTILS.readParentGAVInPomXml(pathToPomXml);
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

    public static Optional<File> addModuleToModulesFolder(String moduleName, FileUpload fileUpload) {
        File file = getModulesFolder().resolve(moduleName).toFile();
        try {
            fileUpload.writeTo(file);
            return Optional.of(file);
        } catch (Exception e) {
            LOG.error("Cannot upload file: " + file.getAbsolutePath(), e);
        }
        return Optional.absent();
    }
}