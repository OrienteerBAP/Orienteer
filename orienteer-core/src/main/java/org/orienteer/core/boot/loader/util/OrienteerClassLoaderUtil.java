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

    private static PomXmlUtils pomXmlUtils   = new PomXmlUtils();
    private static InitUtils initUtils       = new InitUtils();
    private static JarUtils jarUtils         = new JarUtils(initUtils);
    private static AetherUtils aetherUtils   = new AetherUtils(initUtils);
    private static MetadataUtil metadataUtil = new MetadataUtil(initUtils.getMetadataPath(), initUtils.getPathToModulesFolder());

    static final String POM_XML                     = "pom.xml";
    static final String MODULES                     = "modules.xml";

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoaderUtil.class);

    private OrienteerClassLoaderUtil() {}

    public static void reindex() {
        aetherUtils = new AetherUtils(initUtils);
    }

    public static List<OModule> getOrienteerModulesFromServer() {
        HttpDownloader downloader = new HttpDownloader(initUtils.getOrienteerModulesUrl(), initUtils.getPathToModulesFolder());
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
        return aetherUtils.resolveArtifact(artifact);
    }

    public static List<ArtifactResult> downloadArtifacts(Set<Artifact> artifacts) {
        return aetherUtils.downloadArtifacts(artifacts);
    }

    public static List<ArtifactResult> resolveArtifacts(Set<Artifact> artifacts) {
        return aetherUtils.resolveArtifacts(artifacts);
    }

    public static Optional<Artifact> downloadArtifact(Artifact artifact) {
        return aetherUtils.downloadArtifact(artifact);
    }

    public static Optional<Artifact> downloadArtifact(Artifact artifact, String repository) {
        return aetherUtils.downloadArtifact(artifact, repository);
    }

    public static Optional<Artifact> readGroupArtifactVersionInPomXml(Path pomXml) {
        return pomXmlUtils.readGroupArtifactVersionInPomXml(pomXml);
    }

    public static Set<Artifact> readDependencies(Path pomXml) {
        return pomXmlUtils.readDependencies(pomXml);
    }

    public static Optional<String> searchOrienteerInitModule(Path pathToJar) {
        return jarUtils.searchOrienteerInitModule(pathToJar);
    }

    public static Optional<Path> getPomFromJar(Path path) {
        return jarUtils.getPomFromJar(path);
    }

    public static Optional<OModule> getOModuleFromJar(Path pathToJar) {
        return MavenResolver.get().getModuleMetadata(pathToJar);
    }

    public static List<Path> getJarsInModulesFolder() {
        return jarUtils.readJarsInModulesFolder();
    }

    public static Map<Path, OModule> getMetadataModulesInMap() {
        return metadataUtil.readModulesAsMap();
    }

    public static Map<Path, OModule> getMetadataModulesForLoadInMap() {
        return metadataUtil.readModulesForLoadAsMap();
    }

    public static void createMetadata(List<OModule> modules) {
        metadataUtil.createMetadata(modules);
    }

    public static void deleteModuleFromMetadata(OModule module) {
        metadataUtil.deleteModuleFromMetadata(module);
    }

    public static void deleteModulesFromMetadata(List<OModule> modules) {
        metadataUtil.deleteModulesFromMetadata(modules);
    }

    public static void deleteModuleArtifact(OModule module) {
        try {
            Files.deleteIfExists(module.getArtifact().getFile().toPath());
        } catch (IOException e) {
            LOG.error("Cannot delete artifact of module: " + module, e);
        }
    }

    public static void deleteMetadataFile() {
        metadataUtil.deleteMetadata();
    }

    public static void updateModulesJarsInMetadata(List<OModule> modules) {
        metadataUtil.updateJarsInMetadata(modules);
    }

    public static void updateModulesInMetadata(OModule module) {
        metadataUtil.updateMetadata(module);
    }

    public static void updateModulesInMetadata(OModule moduleForUpdate, OModule newModule) {
        metadataUtil.updateMetadata(moduleForUpdate, newModule);
    }

    public static void updateModulesInMetadata(List<OModule> modules) {
        metadataUtil.updateMetadata(modules);
    }

    public static List<OModule> getMetadataModules() {
        return metadataUtil.readMetadata();
    }

    public static List<OModule> getMetadataModulesForLoad() {
        return metadataUtil.readMetadataForLoad();
    }

    static boolean resolvingDependenciesRecursively() {
        return initUtils.resolvingDependenciesRecursively();
    }


    public static Artifact getMainArtifact() {
        Path pathToPomXml = Paths.get(POM_XML);
        Optional<Artifact> artifactOptional = pomXmlUtils.readParentGAVInPomXml(pathToPomXml);
        if (!artifactOptional.isPresent())
            throw new IllegalStateException("Cannot read main artifact in pom.xml (" + pathToPomXml.toAbsolutePath() + ")");
        return artifactOptional.get();
    }

    static void addOrienteerVersions(Path pathToPomXml) {
        pomXmlUtils.addOrienteerVersions(pathToPomXml);
    }

    static Path getModulesFolder() {
        return initUtils.getPathToModulesFolder();
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