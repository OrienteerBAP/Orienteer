package org.orienteer.core.boot.loader.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.http.util.Args;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.io.IOUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.ArtifactResult;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
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
 * Utility class for OrienteerClassLoader
 */
public abstract class OrienteerClassLoaderUtil {
    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoaderUtil.class);

    public static final String WITHOUT_JAR          = "WITHOUT_JAR";

    private static PomXmlUtils pomXmlUtils   = new PomXmlUtils();
    private static InitUtils initUtils       = new InitUtils();
    private static JarUtils jarUtils         = new JarUtils(initUtils);
    private static MetadataUtil metadataUtil = new MetadataUtil(initUtils.getMetadataPath());
    private static AetherUtils aetherUtils   = new AetherUtils(initUtils);

    private static final String POM_XM       = "pom.xml";
    private static final String MODULES      = "modules.xml";


    private OrienteerClassLoaderUtil() {}

    public static void reindex() {
        aetherUtils = new AetherUtils(initUtils);
    }

    /**
     * Download Orienteer artifacts from server.
     * @return list of {@link OArtifact} from server
     * @throws IOException 
     */
    public static List<OArtifact> getOrienteerArtifactsFromServer() throws IOException  {
    	
    	URL website = new URL(initUtils.getOrienteerModulesUrl());
    	File localFile = new File(initUtils.getPathToModulesFolder().toFile(), "modules.xml");
    	FileOutputStream fos = new FileOutputStream(localFile);
    	try {
    		IOUtils.copy(website.openStream(), fos);
    	} finally {
			fos.close();
		}
    	
        OrienteerArtifactsReader reader = new OrienteerArtifactsReader(localFile.toPath());
        List<OArtifact> ooArtifacts = reader.readArtifacts();
        List<OArtifact> metadataModules = getOArtifactsMetadataAsList();
        for (OArtifact metadataModule : metadataModules) {
            for (OArtifact module : ooArtifacts) {
                OArtifactReference metadataArtifact = metadataModule.getArtifactReference();
                OArtifactReference moduleArtifact = module.getArtifactReference();
                if (metadataArtifact.getGroupId().equals(moduleArtifact.getGroupId()) &&
                        metadataArtifact.getArtifactId().equals(moduleArtifact.getArtifactId())) {
                    module.setDownloaded(true);
                }
            }
        }
        addAvailableVersions(ooArtifacts);
        return ooArtifacts;
    }

    private static void addAvailableVersions(List<OArtifact> artifacts) {
        for (OArtifact artifact : artifacts) {
            OArtifactReference reference = artifact.getArtifactReference();
            reference.addAvailableVersions(requestArtifactVersions(reference.getGroupId(), reference.getArtifactId()));
        }
    }

    /**
     * Resolve dependency (artifact). Download its dependencies
     * @param artifact - {@link Artifact} for resolve
     * @return {@link List<Dependency>} - dependencies of artifact
     * @throws IllegalArgumentException if artifact is null
     */
    public static List<Artifact> resolveAndGetArtifactDependencies(Artifact artifact) {
        Args.notNull(artifact, "artifact");
        return aetherUtils.resolveDependency(new Dependency(artifact, "compile"));
    }

    /**
     * Download artifact
     * @param artifact {@link Artifact} for download
     * @return {@link Artifact} of downloaded artifact or Optional.absent if can't download artifact
     * @throws IllegalArgumentException if artifact is null
     */
    public static Artifact downloadArtifact(Artifact artifact) {
        Args.notNull(artifact, "artifact");
        return aetherUtils.downloadArtifact(artifact);
    }

    /**
     * Download artifacts
     * @param artifacts {@link Set<Artifact>} for download
     * @return {@link List<ArtifactResult>} of downloaded artifacts
     * @throws IllegalArgumentException if artifacts is null
     */
    public static List<Artifact> downloadArtifacts(Set<Artifact> artifacts) {
        Args.notNull(artifacts, "artifacts");
        return aetherUtils.downloadArtifacts(artifacts);
    }

    /**
     * Resolve artifacts. Download its dependencies
     * @param artifacts {@link Set<Artifact>} for resolve
     * @return {@link List<ArtifactResult>} resolved dependencies of artifacts
     * @throws IllegalArgumentException if artifacts is null
     */
    public static List<Artifact> resolveArtifacts(List<Artifact> artifacts) {
        Args.notNull(artifacts, "artifacts");
        List<Artifact> result = Lists.newArrayList();
        for (Artifact artifact : artifacts) {
            result.addAll(resolveAndGetArtifactDependencies(artifact));
        }
        return result;
    }


    /**
     * Download artifact from repository
     * @param artifact {@link Artifact} for download
     * @param repository repository for download artifact
     * @return {@link Artifact} of downloaded artifact or Optional.absent if can't download artifact
     * @throws IllegalArgumentException if artifact or repository is null.
     */
    public static Artifact downloadArtifact(Artifact artifact, String repository) {
        Args.notNull(artifact, "artifact");
        Args.notNull(repository, "repository");
        return aetherUtils.downloadArtifact(artifact, repository);
    }

    /**
     * Read group, artifact, version from pom.xml
     * @param pomXml {@link Path} of pom.xml
     * @return {@link Artifact} or Optional.absent() if GAV is not present in pom.xml
     * @throws IllegalArgumentException if pomXml is null
     */
    public static Artifact readGroupArtifactVersionInPomXml(Path pomXml) {
        Args.notNull(pomXml, "pomXml");
        return pomXmlUtils.readGroupArtifactVersionInPomXml(pomXml);
    }

    public static List<String> requestArtifactVersions(String groupId, String artifactId) {
        return aetherUtils.requestArtifactVersions(new DefaultArtifact(String.format("%s:%s:(0,]", groupId, artifactId)));
    }

    /**
     * Read maven dependencies from pom.xml
     * maven dependencies are pom.xml node: <dependencies></dependencies>
     * maven dependency is pom.xml node: <dependency></dependency>
     * @param pomXml {@link Path} of pom.xml
     * @return {@link Set<Artifact>} of dependencies in pom.xml
     * @throws IllegalArgumentException if pomXml is null
     */
    public static Set<Artifact> readDependencies(Path pomXml) {
        Args.notNull(pomXml, "pomXml");
        return pomXmlUtils.readDependencies(pomXml);
    }

    /**
     * Search {@link org.apache.wicket.IInitializer} in jar file
     * @param pathToJar {@link Path} of jar file
     * @return {@link String} of class name or Optional.absent() if {@link org.apache.wicket.IInitializer} is not present in jar file
     * @throws IllegalArgumentException if pathToJar is null
     */
    public static String searchOrienteerInitModule(Path pathToJar) {
        Args.notNull(pathToJar, "pathToJar");
        return jarUtils.searchOrienteerInitModule(pathToJar);
    }

    /**
     * Search pom.xml in jar file
     * @param pathToJar {@link Path} of jar file
     * @return {@link Path} of pom.xml or Optional.absent() if pom.xml is not present in jar file
     * @throws IllegalArgumentException if pathToJar is null
     */
    public static Path getPomFromJar(Path pathToJar) {
        Args.notNull(pathToJar, "pathToJar");
        return jarUtils.getPomFromJar(pathToJar);
    }

    /**
     * Create {@link OArtifact} from jar file
     * @param pathToJar {@link Path} of jar file
     * @return {@link OArtifact} of artifact or Optional.absent() if artifact is not present in jar file
     * @throws IllegalArgumentException if pathToJar is null
     */
    public static OArtifact getOArtifactFromJar(Path pathToJar) {
        Args.notNull(pathToJar, "pathToJar");
        return MavenResolver.get().getOArtifact(pathToJar);
    }

    /**
     * Search jar files in artifact folder
     * @return {@link Set<Path>} of jar files in artifacts folder
     */
    public static Set<Path> getJarsInArtifactsFolder() {
        return Sets.newHashSet(jarUtils.searchJarsInArtifactsFolder());
    }

    /**
     * Checks if metadata.xml exists
     * @return true - if metadata.xml exists
     *         false - if metadata.xml does not exists
     */
    public static boolean metadataExists() {
    	return metadataUtil.metadataExists();
    }

    /**
     * Read artifacts in metadata.xml as map
     * @return {@link Map<Path,OArtifact>} of artifacts in metadata.xml
     */
    public static Map<Path, OArtifact> getOArtifactsMetadataInMap() {
        return metadataUtil.readOArtifactsAsMap();
    }

    /**
     * Read artifacts for load in metadata.xml as map
     * @return {@link Map<Path, OArtifact>} of artifacts for load in metadata.xml
     */
    public static Map<Path, OArtifact> getOArtifactsMetadataForLoadInMap() {
        return metadataUtil.readOArtifactsForLoadAsMap();
    }

    /**
     * Create metadata.xml with oArtifacts
     * @param oArtifacts {@link List<OArtifact>} of artifacts for write to metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public static void createOArtifactsMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.createOArtifactsMetadata(oArtifacts);
    }

    /**
     * Delete artifact from metadata.xml
     * @param oArtifact {@link OArtifact} artifact for delete from metadata.xml
     * @throws IllegalArgumentException if oArtifact is null
     */
    public static void deleteOArtifactFromMetadata(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        metadataUtil.deleteOArtifactFromMetadata(oArtifact);
    }

    /**
     * Delete artifact from metadata.xml
     * @param oArtifacts {@link List<OArtifact>} artifacts for delete from metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public static void deleteOArtifactsFromMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.deleteOArtifactsFromMetadata(oArtifacts);
    }

    /**
     * Delete artifact jar file
     * @param oArtifact {@link OArtifact} artifact which jar file will be delete
     * @throws IllegalArgumentException if oArtifact is null
     */
    public static boolean deleteOArtifactFile(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        boolean deleted = false;
        try {
            File file = oArtifact.getArtifactReference().getFile();
            if (file != null && file.exists()) {
                File artifactMavenDir = getOArtifactMavenDir(oArtifact);
                if (artifactMavenDir != null) {
                    FileUtils.deleteDirectory(artifactMavenDir);
                    deleted = true;
                    LOG.info("Delete directory in maven repository: {}", artifactMavenDir);
                } else {
                    Files.delete(file.toPath());
                    deleted = true;
                    LOG.info("Delete jar file: {}", file);
                }
            }
        } catch (IOException e) {
            LOG.error("Can't delete jar file of Orienteer module: {}", oArtifact, e);
        }
        return deleted;
    }

    /**
     * Parsing directories where oArtifact jar file located.
     * If jar file located in maven repository, path: {$random.path}/com.group/artifact/version/file.jar
     * directory with this artifact returns. (Returns {$random.path}/com.group/artifact)
     * @param oArtifact {@link OArtifact} oArtifact for search maven dir
     * @return {@link File} of maven directory with oArtifact jar file or null
     */
    private static File getOArtifactMavenDir(OArtifact oArtifact) {
        OArtifactReference reference = oArtifact.getArtifactReference();
        File versionFolder = reference.getFile().getParentFile();
        String file = versionFolder.toString().replaceAll("/", ".");
        File result = null;
        try {
            if (file.contains(reference.getGroupId()) && file.contains(reference.getArtifactId()) && file.contains(reference.getVersion())) {
                String gav = file.substring(file.indexOf(reference.getGroupId()));
                String groupId = gav.substring(gav.indexOf(reference.getGroupId()), gav.indexOf(reference.getArtifactId()) - 1);
                String artifactId = gav.substring(gav.indexOf(reference.getArtifactId()), gav.indexOf(reference.getVersion()) - 1);
                String version = gav.substring(gav.indexOf(reference.getVersion()));
                if (groupId.equals(reference.getGroupId()) && artifactId.equals(reference.getArtifactId())
                        && version.equals(reference.getVersion())) {
                    result = versionFolder.getParentFile();
                }

            }
        } catch (StringIndexOutOfBoundsException ex) {
            /* StringIndexOutOfBoundsException throws if jar file is not located in maven repository */
        }
        return result;
    }

    /**
     * Delete metadata.xml
     */
    public static void deleteMetadataFile() {
        metadataUtil.deleteMetadata();
    }

    /**
     * Update or create oArtifacts in metadata.xml
     * @param oArtifacts {@link List<OArtifact>} for update or create in metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public static void updateOArtifactsJarsInMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.updateJarsInOArtifactsMetadata(oArtifacts);
    }

    /**
     * Update or create oArtifact in metadata.xml
     * @param oArtifact {@link OArtifact} artifact for update or create in metadata.xml
     * @throws IllegalArgumentException if oArtifact is null
     */
    public static void updateOArtifactInMetadata(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        metadataUtil.updateOArtifactMetadata(oArtifact);
    }

    /**
     * Update metadata.xml: replace artifactForUpdate by newArtifact
     * @param artifactForUpdate {@link OArtifact} for replace by newArtifact
     * @param newArtifact {@link OArtifact} for replace
     * @throws IllegalArgumentException if artifactForUpdate or newArtifact is null
     */
    public static void updateOArtifactInMetadata(OArtifact artifactForUpdate, OArtifact newArtifact) {
        Args.notNull(artifactForUpdate, "artifactForUpdate");
        Args.notNull(newArtifact, "newArtifact");
        metadataUtil.updateOArtifactMetadata(artifactForUpdate, newArtifact);
    }

    /**
     * Update or create oArtifacts in metadata.xml
     * @param oArtifacts {@link List<OArtifact>} for update or create in metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public static void updateOArtifactsInMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.updateOArtifactsMetadata(oArtifacts);
    }

    /**
     * Read and get artifacts from metadata.xml
     * @return {@link List<OArtifact>} artifacts from metadata.xml
     */
    public static List<OArtifact> getOArtifactsMetadataAsList() {
        return metadataUtil.readOArtifactsAsList();
    }

    /**
     * Read and get artifacts for load from metadata.xml
     * @return {@link List<OArtifact>} artifacts for load from metadata.xml
     */
    public static List<OArtifact> getOoArtifactsMetadataForLoadAsList() {
        return metadataUtil.readOArtifactsForLoadAsList();
    }

    /**
     * @return recursively resolving dependencies property
     */
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
				try(InputStream is = url.openStream()) {
					Manifest manifest = new Manifest(is);
					Attributes attrs = manifest.getMainAttributes();
					String groupId = attrs.getValue(Name.IMPLEMENTATION_VENDOR_ID);
					String artifactId = attrs.getValue(Name.IMPLEMENTATION_TITLE);
					String version = attrs.getValue(Name.IMPLEMENTATION_VERSION);
					if(!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(artifactId) && !Strings.isNullOrEmpty(version)) {
						try {
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

    /**
     * @return {@link Path} of modules folder
     */
    static Path getArtifactsFolder() {
        return initUtils.getPathToModulesFolder();
    }


    /**
     * Add artifact jar file to temp folder
     * @param artifactName artifact name
     * @param fileUpload {@link FileUpload} of artifact's jar
     * @return {@link File} of artifact's jar file or Optional.absent() if can't add artifact's jar file to folder
     * @throws IllegalArgumentException if artifactName is null or empty. Or when fileUpload is null.
     */
    public static File createJarTempFile(String artifactName, FileUpload fileUpload) {
        Args.notEmpty(artifactName, "artifactName");
        Args.notNull(fileUpload, "fileUpload");
        String fileName = fileUpload.getClientFileName();
        try {
            File file = File.createTempFile(fileName.replaceAll("\\.jar", ""), ".jar");
            fileUpload.writeTo(file);
            return file;
        } catch (Exception e) {
            LOG.error("Cannot upload file: {}", fileName, e);
        }
        return null;
    }

    /**
     * Move file to artifact's folder
     * @param path - {@link Path} of file to move
     * @param newFileName - new name of file. If newFileName is null or empty uses {@link Path} file name.
     * @throws IllegalArgumentException if file is null
     */
    public static Path moveJarFileToArtifactsFolder(Path path, String newFileName) {
        Args.notNull(path, "file");
        Path artifactsFolder = getArtifactsFolder();
        Path newFilePath = Strings.isNullOrEmpty(newFileName) ? artifactsFolder.resolve(path.getFileName())
                : artifactsFolder.resolve(newFileName);
        try {
            return Files.move(path, newFilePath);
        } catch (IOException e) {
            LOG.error("Can't move file! Old path: {} \n new path: {}",
                    path.toAbsolutePath(), newFilePath.toAbsolutePath(), e);
        }
        return null;
    }
}