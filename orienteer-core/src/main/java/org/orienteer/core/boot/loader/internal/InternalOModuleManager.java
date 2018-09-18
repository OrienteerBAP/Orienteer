package org.orienteer.core.boot.loader.internal;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import org.apache.commons.io.FileUtils;
import org.apache.http.util.Args;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.internal.service.OModulesStaticInjector;
import org.orienteer.core.boot.loader.service.IOrienteerModulesResolver;
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
import java.util.stream.Collectors;

/**
 * Utility class for OrienteerClassLoader
 */
public class InternalOModuleManager implements IReindexSupport {
    private static final Logger LOG = LoggerFactory.getLogger(InternalOModuleManager.class);

    public static final String WITHOUT_JAR          = "WITHOUT_JAR";

    private final PomXmlHandler pomXmlUtils;
    private final OJarsManager jarsManager;
    private final MetadataUtil metadataUtil;
    private final AetherUtils aetherUtils;
    private final MavenResolver mavenResolver;


    public static InternalOModuleManager get() {
        Injector injector = OModulesStaticInjector.getInjector();
        return injector.getInstance(InternalOModuleManager.class);
    }

    public InternalOModuleManager(OModulesMicroFrameworkConfig config) {
        pomXmlUtils = new PomXmlHandler();
        jarsManager = new OJarsManager(config.getOrCreateModulesFolder());
        metadataUtil = new MetadataUtil(config.getMetadataPath());
        aetherUtils = new AetherUtils(config.getMavenLocalRepository(), config.getRemoteRepositories());
        mavenResolver = new MavenResolver(config.isResolvingDependenciesRecursively());
    }


    @Override
    public final void reindex(OModulesMicroFrameworkConfig config) {
        jarsManager.reindex(config);
        metadataUtil.reindex(config);
        aetherUtils.reindex(config);
        mavenResolver.reindex(config);
    }

    public OJarsManager getJarsManager() {
        return jarsManager;
    }

    public MavenResolver getMavenResolver() {
        return mavenResolver;
    }

    /**
     * Download Orienteer artifacts from server.
     * @return list of {@link OArtifact} from server
     * @throws IOException 
     */
    public List<OArtifact> getOrienteerModules() {
        OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
        if (app == null) {
            throw new IllegalStateException("Can't retrieve Orienteer modules if Orienteer application doesn't exists!");
        }
        IOrienteerModulesResolver resolver = app.getServiceInstance(IOrienteerModulesResolver.class);
        return resolver.resolveOrienteerModules();
    }

    public Set<OArtifact> getOrienteerModulesAsSet() {
        return new LinkedHashSet<>(getOrienteerModules());
    }

    public Set<OArtifact> getNonOrienteerArtifacts(Set<OArtifact> artifacts) throws IllegalStateException {
        Set<OArtifact> orienteerArtifacts = getOrienteerModulesAsSet();
        if (orienteerArtifacts.isEmpty()) {
            throw new IllegalStateException("Can't retrieve Orienteer artifacts from server.");
        }
        return Sets.difference(artifacts, orienteerArtifacts);
    }

    public Set<OArtifact> getOrienteerArtifacts(Set<OArtifact> artifacts) throws IllegalStateException {
        Set<OArtifact> orienteerArtifacts = getOrienteerModulesAsSet();
        if (orienteerArtifacts.isEmpty()) {
            throw new IllegalStateException("Can't retrieve Orienteer artifacts from server.");
        }
        return artifacts.stream()
                .filter(artifact -> isOrienteerArtifact(orienteerArtifacts, artifact))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public boolean isOrienteerArtifact(Set<OArtifact> orietneerArtifacts, OArtifact testedArtifact) {
        OArtifactReference testedRef = testedArtifact.getArtifactReference();

        for (OArtifact artifact : orietneerArtifacts) {
            OArtifactReference orienteerRef = artifact.getArtifactReference();
            boolean groupEq = orienteerRef.getGroupId().equals(testedRef.getGroupId());
            boolean artifactEq = orienteerRef.getArtifactId().equals(testedRef.getArtifactId());
            boolean isOrienteerVersion = orienteerRef.getAvailableVersions().contains(testedRef.getVersion());

            if (groupEq && artifactEq && isOrienteerVersion) {
                return true;
            }
        }

        return false;
    }

    /**
     * Resolve dependency (artifact). Download its dependencies
     * @param artifact - {@link Artifact} for resolve
     * @return {@link List} - dependencies of artifact
     * @throws IllegalArgumentException if artifact is null
     */
    public List<Artifact> resolveAndGetArtifactDependencies(Artifact artifact) {
        Args.notNull(artifact, "artifact");
        return aetherUtils.resolveDependency(new Dependency(artifact, "compile"));
    }

    /**
     * Download artifact
     * @param artifact {@link Artifact} for download
     * @return {@link Artifact} of downloaded artifact or Optional.absent if can't download artifact
     * @throws IllegalArgumentException if artifact is null
     */
    public Artifact downloadArtifact(Artifact artifact) {
        Args.notNull(artifact, "artifact");
        return aetherUtils.downloadArtifact(artifact);
    }

    /**
     * Download artifacts
     * @param artifacts {@link Set} for download
     * @return {@link List} of downloaded artifacts
     * @throws IllegalArgumentException if artifacts is null
     */
    public List<Artifact> downloadArtifacts(Set<Artifact> artifacts) {
        Args.notNull(artifacts, "artifacts");
        return aetherUtils.downloadArtifacts(artifacts);
    }

    /**
     * Resolve artifacts. Download its dependencies
     * @param artifacts {@link Set} for resolve
     * @return {@link List} resolved dependencies of artifacts
     * @throws IllegalArgumentException if artifacts is null
     */
    public List<Artifact> resolveArtifacts(List<Artifact> artifacts) {
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
    public Artifact downloadArtifact(Artifact artifact, String repository) {
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
    public Artifact readGroupArtifactVersionInPomXml(Path pomXml) {
        Args.notNull(pomXml, "pomXml");
        return pomXmlUtils.readGroupArtifactVersionInPomXml(pomXml);
    }

    public List<String> requestArtifactVersions(String groupId, String artifactId) {
        return aetherUtils.requestArtifactVersions(new DefaultArtifact(String.format("%s:%s:(0,]", groupId, artifactId)));
    }

    /**
     * Read maven dependencies from pom.xml
     * maven dependencies are pom.xml node
     * @param pomXml {@link Path} of pom.xml
     * @return {@link Set} of dependencies in pom.xml
     * @throws IllegalArgumentException if pomXml is null
     */
    public Set<Artifact> readDependencies(Path pomXml) {
        Args.notNull(pomXml, "pomXml");
        return pomXmlUtils.readDependencies(pomXml);
    }

    /**
     * Search {@link org.apache.wicket.IInitializer} in jar file
     * @param pathToJar {@link Path} of jar file
     * @return {@link String} of class name or Optional.absent() if {@link org.apache.wicket.IInitializer} is not present in jar file
     * @throws IllegalArgumentException if pathToJar is null
     */
    public String searchOrienteerInitModule(Path pathToJar) {
        Args.notNull(pathToJar, "pathToJar");
        return jarsManager.searchOrienteerInitModule(pathToJar);
    }

    /**
     * Search pom.xml in jar file
     * @param pathToJar {@link Path} of jar file
     * @return {@link Path} of pom.xml or Optional.absent() if pom.xml is not present in jar file
     * @throws IllegalArgumentException if pathToJar is null
     */
    public Path getPomFromJar(Path pathToJar) {
        Args.notNull(pathToJar, "pathToJar");
        return jarsManager.getPomFromJar(pathToJar);
    }

    /**
     * Create {@link OArtifact} from jar file
     * @param pathToJar {@link Path} of jar file
     * @return {@link OArtifact} of artifact or Optional.absent() if artifact is not present in jar file
     * @throws IllegalArgumentException if pathToJar is null
     */
    public OArtifact getOArtifactFromJar(Path pathToJar) {
        Args.notNull(pathToJar, "pathToJar");
        return mavenResolver.getOArtifact(pathToJar);
    }

    /**
     * Search jar files in artifact folder
     * @return {@link Set} of jar files {@link Path}s in artifacts folder
     */
    public Set<Path> getJarsInArtifactsFolder() {
        return Sets.newHashSet(jarsManager.searchJarsInArtifactsFolder());
    }

    /**
     * Checks if metadata.xml exists
     * @return true - if metadata.xml exists
     *         false - if metadata.xml does not exists
     */
    public boolean metadataExists() {
    	return metadataUtil.isMetadataExists();
    }

    /**
     * Read artifacts in metadata.xml as map
     * @return {@link Map} of artifacts in metadata.xml
     */
    public Map<Path, OArtifact> getOArtifactsMetadataInMap() {
        return metadataUtil.readOArtifactsAsMap();
    }

    /**
     * Read artifacts for load in metadata.xml as map
     * @return {@link Map} of artifacts for load in metadata.xml
     */
    public Map<Path, OArtifact> getOArtifactsMetadataForLoadInMap() {
        return metadataUtil.readOArtifactsForLoadAsMap();
    }

    /**
     * Create metadata.xml with oArtifacts
     * @param oArtifacts {@link List} of artifacts for write to metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public void createOArtifactsMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.createOArtifactsMetadata(oArtifacts);
    }

    /**
     * Delete artifact from metadata.xml
     * @param oArtifact {@link OArtifact} artifact for delete from metadata.xml
     * @throws IllegalArgumentException if oArtifact is null
     */
    public void deleteOArtifactFromMetadata(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        metadataUtil.deleteOArtifactFromMetadata(oArtifact);
    }

    /**
     * Delete artifact from metadata.xml
     * @param oArtifacts {@link List} artifacts for delete from metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public void deleteOArtifactsFromMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.deleteOArtifactsFromMetadata(oArtifacts);
    }

    public void deleteOArtifactsFromMetadata(Set<OArtifact> artifacts) {
        deleteOArtifactsFromMetadata(new LinkedList<>(artifacts));
    }

    public void deleteOArtifactFiles(Set<OArtifact> artifacts) {
        deleteOArtifactFiles(new LinkedList<>(artifacts));
    }

    public void deleteOArtifactFiles(List<OArtifact> artifacts) {
        artifacts.forEach(this::deleteOArtifactFile);
    }

    /**
     * Delete artifact jar file
     * @param oArtifact {@link OArtifact} artifact which jar file will be delete
     * @throws IllegalArgumentException if oArtifact is null
     */
    public boolean deleteOArtifactFile(OArtifact oArtifact) {
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
    private File getOArtifactMavenDir(OArtifact oArtifact) {
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
    public void deleteMetadataFile() {
        metadataUtil.deleteMetadata();
    }

    /**
     * Update or create oArtifacts in metadata.xml
     * @param oArtifacts {@link List} for update or create in metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public void updateOArtifactsJarsInMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.updateJarsInOArtifactsMetadata(oArtifacts);
    }

    /**
     * Update or create oArtifact in metadata.xml
     * @param oArtifact {@link OArtifact} artifact for update or create in metadata.xml
     * @throws IllegalArgumentException if oArtifact is null
     */
    public void updateOArtifactInMetadata(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        metadataUtil.updateOArtifactMetadata(oArtifact);
    }

    /**
     * Update metadata.xml: replace artifactForUpdate by newArtifact
     * @param artifactForUpdate {@link OArtifact} for replace by newArtifact
     * @param newArtifact {@link OArtifact} for replace
     * @throws IllegalArgumentException if artifactForUpdate or newArtifact is null
     */
    public void updateOArtifactInMetadata(OArtifact artifactForUpdate, OArtifact newArtifact) {
        Args.notNull(artifactForUpdate, "artifactForUpdate");
        Args.notNull(newArtifact, "newArtifact");
        metadataUtil.updateOArtifactMetadata(artifactForUpdate, newArtifact);
    }

    /**
     * Update or create oArtifacts in metadata.xml
     * @param oArtifacts {@link List} for update or create in metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    public void updateOArtifactsInMetadata(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        metadataUtil.updateOArtifactsMetadata(oArtifacts);
    }

    public void updateOArtifactsInMetadata(Set<OArtifact> artifacts) {
        Args.notNull(artifacts, "artifacts");
        updateOArtifactsInMetadata(new LinkedList<>(artifacts));
    }

    /**
     * Read and get artifacts from metadata.xml
     * @return {@link List} artifacts from metadata.xml
     */
    public List<OArtifact> getOArtifactsMetadataAsList() {
        return metadataUtil.readOArtifactsAsList();
    }

    public Set<OArtifact> getOArtifactsMetadataAsSet() {
        return metadataUtil.readOArtifactsAsSet();
    }

    /**
     * Read and get artifacts for load from metadata.xml
     * @return {@link List} artifacts for load from metadata.xml
     */
    public List<OArtifact> getOoArtifactsMetadataForLoadAsList() {
        return metadataUtil.readOArtifactsForLoadAsList();
    }
    
    @SuppressWarnings("deprecation")
    public Collection<Artifact> getAvailableArtifacts(ClassLoader classLoader) {
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
}