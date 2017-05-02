package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.http.util.Args;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.string.Strings;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

/**
 * @author Vitaliy Gonchar
 * Utility class for OrienteerClassLoader
 */
public abstract class OrienteerClassLoaderUtil {
    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoaderUtil.class);

    public static final String WITHOUT_JAR          = "WITHOUT_JAR";

    private static PomXmlUtils pomXmlUtils   = new PomXmlUtils();
    private static InitUtils initUtils       = new InitUtils();
    private static JarUtils jarUtils         = new JarUtils(initUtils);
    private static MetadataUtil metadataUtil = new MetadataUtil(initUtils.getMetadataPath(), initUtils.getPathToModulesFolder());
    private static AetherUtils aetherUtils   = new AetherUtils(initUtils);

    private static final String POM_XML             = "pom.xml";
    static final String MODULES                     = "modules.xml";


    private OrienteerClassLoaderUtil() {}

    public static void reindex() {
        aetherUtils = new AetherUtils(initUtils);
    }

    public static List<OArtifact> getOrienteerArtifactsFromServer() {
        HttpDownloader downloader = new HttpDownloader(initUtils.getOrienteerModulesUrl(), initUtils.getPathToModulesFolder());
        Path download = downloader.download(MODULES);
        if (download == null) return Lists.newArrayList();
        OrienteerArtifactsReader reader = new OrienteerArtifactsReader(download);
        List<OArtifact> ooArtifacts = reader.readModules();
        List<OArtifact> metadataModules = getOoArtifactsMetadataAsList();
        for (OArtifact metadataModule : metadataModules) {
            for (OArtifact module : ooArtifacts) {
                OArtifactReference metadataArtifact = metadataModule.getArtifactReference();
                OArtifactReference moduleArtifact = module.getArtifactReference();
                if (metadataArtifact.equals(moduleArtifact)) {
                    module.setDownloaded(true);
                }
            }
        }
        return ooArtifacts;
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

    public static Optional<OArtifact> getOArtifactFromJar(Path pathToJar) {
        return MavenResolver.get().getOArtifact(pathToJar);
    }

    public static List<Path> getJarsInArtifactsFolder() {
        return jarUtils.readJarsInArtifactsFolder();
    }

    public static Map<Path, OArtifact> getOArtifactsMetadataInMap() {
        return metadataUtil.readOArtifactsAsMap();
    }

    public static Map<Path, OArtifact> getOArtifactsMetadataForLoadInMap() {
        return metadataUtil.readOArtifactsForLoadAsMap();
    }

    public static void createOArtifactsMetadata(List<OArtifact> oArtifacts) {
        metadataUtil.createOArtifactsMetadata(oArtifacts);
    }

    public static void deleteOArtifactFromMetadata(OArtifact oArtifact) {
        metadataUtil.deleteOArtifactFromMetadata(oArtifact);
    }

    public static void deleteOArtifactsFromMetadata(List<OArtifact> oArtifacts) {
        metadataUtil.deleteOArtifactsFromMetadata(oArtifacts);
    }

    public static void deleteOArtifactArtifactFile(OArtifact oArtifact) {
        try {
            Files.deleteIfExists(oArtifact.getArtifactReference().getFile().toPath());
        } catch (IOException e) {
            LOG.error("Cannot delete artifact of module: " + oArtifact, e);
        }
    }

    public static void deleteMetadataFile() {
        metadataUtil.deleteMetadata();
    }

    public static void updateOArtifactsJarsInMetadata(List<OArtifact> oArtifacts) {
        metadataUtil.updateJarsInOoArtifactsMetadata(oArtifacts);
    }

    public static void updateOArtifactInMetadata(OArtifact oArtifact) {
        metadataUtil.updateOoArtifactsMetadata(oArtifact);
    }

    public static void updateOoArtifactInMetadata(OArtifact moduleConfigForUpdate,
                                                            OArtifact newModuleConfig) {
        metadataUtil.updateOoArtifactsMetadata(moduleConfigForUpdate, newModuleConfig);
    }

    public static void updateOoArtifactInMetadata(List<OArtifact> oArtifacts) {
        metadataUtil.updateOoArtifactsMetadata(oArtifacts);
    }

    public static List<OArtifact> getOoArtifactsMetadataAsList() {
        return metadataUtil.readOoArtifactsAsList();
    }

    public static List<OArtifact> getOoArtifactsMetadataForLoadAsList() {
        return metadataUtil.readOoArtifactsForLoadAsList();
    }

    static boolean resolvingDependenciesRecursively() {
        return initUtils.resolvingDependenciesRecursively();
    }
    
    @SuppressWarnings("deprecation")
    public static Collection<Artifact> getAvailableArtifacts(ClassLoader classLoader) {
    	Collection<Artifact> ret = new HashSet<>();
    	try {
			Enumeration<URL> urls = classLoader.getResources("META-INF/MANIFEST.MF");
			while (urls.hasMoreElements()) {
				URL url = (URL) urls.nextElement();
				LOG.info("URL: "+url);
				try(InputStream is = url.openStream()) {
					Manifest manifest = new Manifest(is);
					Attributes attrs = manifest.getMainAttributes();
					String groupId = attrs.getValue(Name.IMPLEMENTATION_VENDOR_ID);
					String artifactId = attrs.getValue(Name.IMPLEMENTATION_TITLE);
					String version = attrs.getValue(Name.IMPLEMENTATION_VERSION);
					if(!Strings.isEmpty(groupId) && !Strings.isEmpty(artifactId) && !Strings.isEmpty(version)) {
						try {
							LOG.info("AVAILABLE ARTIFACT: "+String.format("%s:%s:pom:%s", groupId, artifactId, version));
							ret.add(new DefaultArtifact(String.format("%s:%s:pom:%s", groupId, artifactId, version)));
						} catch (IllegalArgumentException e) { /*NOP*/
						}
					}
				} catch (IOException e) {
					LOG.error("Can't load manifest from "+url, e);
				}
			}
		} catch (IOException e) {
			LOG.error("Can't list available artifacts", e);
		}
    	return ret;
    }

    static Path getModulesFolder() {
        return initUtils.getPathToModulesFolder();
    }

    static void addOrienteerVersions(Path pomXml) {
        Args.notNull(pomXml, "pomXml");
        if (pomXml.toString().endsWith(".jar")) {
            Optional<Path> pom = jarUtils.getPomFromJar(pomXml);
            if (pom.isPresent()) pomXmlUtils.addOrienteerVersions(pom.get());
        } else pomXmlUtils.addOrienteerVersions(pomXml);
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