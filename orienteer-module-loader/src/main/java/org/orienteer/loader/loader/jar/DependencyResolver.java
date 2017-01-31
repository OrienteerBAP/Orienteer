package org.orienteer.loader.loader.jar;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.orienteer.loader.loader.Dependency;
import org.orienteer.loader.loader.html.HtmlResolver;
import org.orienteer.loader.loader.xml.PomParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public class DependencyResolver {

    @Inject @Named("depJars")
    private Path depJarsFolder;

    @Inject @Named("depPoms")
    private Path depPomsFolder;

    private static final String SNAPSHOT      = "SNAPSHOT";
    private static final String SNAPSHOT_REP  = "snapshots";
    private static final String RELEASE_REP   = "releases";
    private static final String DEP_TIMESTAMP = "timestamp";
    private static final String DEP_BUILD_NUM = "buildNumber";

    private static final String MAVEN_METADATA = "maven-metadata.xml";

    private static final Logger LOG = LoggerFactory.getLogger(DependencyResolver.class);


    private static final Map<Dependency, Path> JAR_CACHE = new HashMap<>();
    private static final Map<Dependency, Path> POM_CACHE = new HashMap<>();

    private static Set<String> repositories = new HashSet<>();
    static {
        repositories.add("https://oss.sonatype.org/content/repositories/snapshots/");
        repositories.add("https://repo1.maven.org/maven2/");
        repositories.add("http://repo.maven.apache.org/maven2/");
    }

    public void addNewRepositories(Path pomFile) {
        if (pomFile == null) return;
        String pathToPom = pomFile.toAbsolutePath().toString();
        if (!pathToPom.endsWith("pom.xml") || !pathToPom.endsWith(".pom")) return;
        initRepositories(pomFile);
    }

    private void initRepositories(Path pomFile) {
        try {
            List<Map<String, String>> elements =
                    PomParser.getBlocks(Files.newInputStream(pomFile), "repository");
            for (Map<String, String> element : elements) {
                String url = element.get("url").endsWith("/") ? element.get("url") : element.get("url") + "/";
                repositories.add(url);
            }
        } catch (IOException e) {
            LOG.debug("Cannot read pom file: " + pomFile);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    public Path getJarDependency(Dependency dependency) throws IOException {
        return getJarDependency(dependency.getGroupId(), dependency.getArtifactId(), dependency.getArtifactVersion());
    }

    public Path getJarDependency(String groupId, String artifactId, String artifactVersion) throws IOException {
        Path jarPath = JAR_CACHE.get(new Dependency(groupId, artifactId, artifactVersion));
        if (jarPath != null) return jarPath;

        URL jarUrl  = searchRemoteFile(groupId, artifactId, artifactVersion, ".jar");
        if (jarUrl == null) {
            throw new RemoteFileNotFoundException("groupId: " + groupId + " artifactId: "
                    + artifactId + " artifactVersion: " + artifactVersion);
        }
        int pointer = jarUrl.getPath().lastIndexOf("/") + 1;
        if (!Files.exists(depJarsFolder)) {
            Files.createDirectories(depJarsFolder);
        }
        String file = jarUrl.getPath().substring(pointer);
        jarPath= depJarsFolder.resolve(file);
        if (!Files.exists(jarPath)) jarPath = Downloader.download(jarPath, jarUrl);
        if (jarPath != null) JAR_CACHE.put(new Dependency(groupId, artifactId, artifactVersion), jarPath);
        return jarPath;
    }

    public Path getPomDependency(Dependency dependency) throws IOException {
        return getPomDependency(dependency.getGroupId(), dependency.getArtifactId(), dependency.getArtifactVersion());
    }

    public Path getPomDependency(String groupId, String artifactId, String artifactVersion) throws IOException {
        Path pomPath = POM_CACHE.get(new Dependency(groupId, artifactId, artifactVersion));
        if (pomPath != null) return pomPath;
        URL pomUrl = searchRemoteFile(groupId, artifactId, artifactVersion, ".pom");
        if (pomUrl == null) {
            throw new RemoteFileNotFoundException("groupId: " + groupId + " artifactId: "
                    + artifactId + " artifactVersion: " + artifactVersion);
        }
        int pointer = pomUrl.getPath().lastIndexOf("/") + 1;
        if (!Files.exists(depPomsFolder)) {
            Files.createDirectories(depPomsFolder);
        }
        String file = pomUrl.getPath().substring(pointer);
        pomPath = depPomsFolder.resolve(file);
        if (!Files.exists(pomPath)) {
            pomPath = Downloader.download(pomPath, pomUrl);
        }
        if (pomPath != null) POM_CACHE.put(new Dependency(groupId, artifactId, artifactVersion), pomPath);

        return pomPath;
    }

    private URL searchRemoteFile(String groupId, String artifactId, String artifactVersion, String fileFormat) {

        if (artifactId == null) return null;

        return isSnapshot(artifactVersion) ? searchSnapshotRemoteFile(groupId, artifactId, artifactVersion, fileFormat) :
                searchReleaseRemoteFile(groupId, artifactId, artifactVersion, fileFormat);
    }

    public URL searchReleaseRemoteFile(String groupId, String artifactId, String artifactVersion, String fileFormat) {
        resolveRepositoryUrl(SNAPSHOT_REP, RELEASE_REP);
        return getRemoteFile(
                new Dependency(groupId, artifactId, artifactVersion), false, fileFormat);
    }

    public URL searchSnapshotRemoteFile(String groupId, String artifactId, String artifactVersion, String fileFormat) {
        resolveRepositoryUrl(RELEASE_REP, SNAPSHOT_REP);
        return getRemoteFile(
                new Dependency(groupId, artifactId, artifactVersion), true, fileFormat);
    }

    private URL getRemoteFile(Dependency dependency, boolean isSnapshot, String fileFormat) {
        URL remoteFile = null;
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();
        String artifactVersion = dependency.getArtifactVersion();
        for (String repository : repositories) {
            String repUrl = getDependencyRepFolderPath(repository, groupId, artifactId, artifactVersion);
            if (isSnapshot) {
                String metadataUrl = repUrl + MAVEN_METADATA;
                URL metadata = getURL(metadataUrl);
                if (metadata != null) {
                    remoteFile = getFileURL(metadata, repUrl, dependency, isSnapshot, fileFormat);
                    break;
                }
            } else {
                URL metadata = getURL(repUrl);
                remoteFile = getFileURL(metadata, repUrl, dependency, isSnapshot, fileFormat);
                if (remoteFile != null) break;
            }
        }
        return remoteFile;
    }

    private URL getFileURL(URL metadata, String repUrl, Dependency dependency, boolean isSnapshot, String fileFormat) {
        URL fileUrl = null;
        String artifactId = dependency.getArtifactId();
        String artifactVersion = dependency.getArtifactVersion();
        String version = isSnapshot ? artifactVersion.substring(0, artifactVersion.indexOf("-")) : artifactVersion;
        try {
            String url;
            if (isSnapshot) {
                List<Map<String, String>> xmlData = PomParser.getBlocks(metadata.openStream(), "snapshot");
                Map<String, String> snapshot = xmlData.get(0);
                url = repUrl + artifactId + "-" + version + "-" + snapshot.get(DEP_TIMESTAMP)
                        + "-" + snapshot.get(DEP_BUILD_NUM) + fileFormat;
            } else url = repUrl + artifactId + "-" + version + fileFormat;
            fileUrl = getURL(url);
        }  catch (IOException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return fileUrl;
    }

    private URL getURL(String url) {
        URL remoteFile = null;
        try {
            remoteFile = new URL(url);
        } catch (MalformedURLException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }

        return remoteFile != null && HtmlResolver.isFile(remoteFile) ? remoteFile : null;
    }


    private void resolveRepositoryUrl(String stage, String targetStage) {
        Set<String> modifyReps = new HashSet<>();
        for (String rep : repositories) {
            if (rep.contains(stage)) {
                rep = rep.replace(stage, targetStage);
                modifyReps.add(rep);
            } else modifyReps.add(rep);
        }
        repositories = !modifyReps.isEmpty() ? modifyReps : repositories;
    }

    private String getDependencyRepFolderPath(String rep, String groupId, String artifactId, String artifactVersion) {
        return rep + groupId.replace('.', '/')
                + "/" + artifactId + "/" + artifactVersion + "/";
    }

    public boolean isSnapshot(Dependency dependency) {
        return isSnapshot(dependency.getArtifactVersion());
    }

    public boolean isSnapshot(String artifactVersion) {
        return artifactVersion.toUpperCase().endsWith(SNAPSHOT);
    }


}
