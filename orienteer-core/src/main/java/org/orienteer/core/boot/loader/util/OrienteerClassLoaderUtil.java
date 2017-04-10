package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
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

    public static List<OArtifact> getOrienteerModulesConfigurationsFromServer() {
        HttpDownloader downloader = new HttpDownloader(initUtils.getOrienteerModulesUrl(), initUtils.getPathToModulesFolder());
        Path download = downloader.download(MODULES);
        if (download == null) return Lists.newArrayList();
        OrienteerArtifactsReader reader = new OrienteerArtifactsReader(download);
        List<OArtifact> oModuleConfigurations = reader.readModules();
        List<OArtifact> metadataModules = getOModulesConfigurationsMetadataAsList();
        for (OArtifact metadataModule : metadataModules) {
            for (OArtifact module : oModuleConfigurations) {
                OArtifactReference metadataArtifact = metadataModule.getArtifact();
                OArtifactReference moduleArtifact = module.getArtifact();
                if (metadataArtifact.equals(moduleArtifact)) {
                    module.setDownloaded(true);
                }
            }
        }
        return oModuleConfigurations;
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

    public static Optional<OArtifact> getOModuleConfigurationFromJar(Path pathToJar) {
        return MavenResolver.get().getModuleConfiguration(pathToJar);
    }

    public static List<Path> getJarsInOModulesConfigurationsFolder() {
        return jarUtils.readJarsInModulesFolder();
    }

    public static Map<Path, OArtifact> getOModulesConfigurationsMetadataInMap() {
        return metadataUtil.readOModulesConfigurationsAsMap();
    }

    public static Map<Path, OArtifact> getOModulesConfigurationsMetadataForLoadInMap() {
        return metadataUtil.readOModulesConfigurationsForLoadAsMap();
    }

    public static void createOModulesConfigurationsMetadata(List<OArtifact> modulesConfigurations) {
        metadataUtil.createOModulesConfigurationsMetadata(modulesConfigurations);
    }

    public static void deleteOModuleConfigurationFromMetadata(OArtifact moduleConfiguration) {
        metadataUtil.deleteOModuleConfigurationFromMetadata(moduleConfiguration);
    }

    public static void deleteOModulesConfigurationsFromMetadata(List<OArtifact> modulesConfigurations) {
        metadataUtil.deleteOModulesConfigurationsFromMetadata(modulesConfigurations);
    }

    public static void deleteOModuleConfigurationArtifactFile(OArtifact moduleConfiguration) {
        try {
            Files.deleteIfExists(moduleConfiguration.getArtifact().getFile().toPath());
        } catch (IOException e) {
            LOG.error("Cannot delete artifact of module: " + moduleConfiguration, e);
        }
    }

    public static void deleteMetadataFile() {
        metadataUtil.deleteMetadata();
    }

    public static void updateOModulesConfigurationsJarsInMetadata(List<OArtifact> modulesConfigurations) {
        metadataUtil.updateJarsInOModulesConfigurationsMetadata(modulesConfigurations);
    }

    public static void updateOModuleConfigurationInMetadata(OArtifact moduleConfiguration) {
        metadataUtil.updateOModulesConfigurationsMetadata(moduleConfiguration);
    }

    public static void updateOModuleConfigurationInMetadata(OArtifact moduleConfigForUpdate,
                                                            OArtifact newModuleConfig) {
        metadataUtil.updateOModulesConfigurationsMetadata(moduleConfigForUpdate, newModuleConfig);
    }

    public static void updateOModuleConfigurationInMetadata(List<OArtifact> modulesConfigurations) {
        metadataUtil.updateOModulesConfigurationsMetadata(modulesConfigurations);
    }

    public static List<OArtifact> getOModulesConfigurationsMetadataAsList() {
        return metadataUtil.readOModulesConfigurationsAsList();
    }

    public static List<OArtifact> getOModulesConfigurationsMetadataForLoadAsList() {
        return metadataUtil.readOModulesConfigurationsForLoadAsList();
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