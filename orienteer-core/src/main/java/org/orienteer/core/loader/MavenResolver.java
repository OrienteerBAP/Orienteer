package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.version.Version;
import org.orienteer.core.loader.util.JarReader;
import org.orienteer.core.loader.util.PomXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public class MavenResolver {

    private static final Logger LOG = LoggerFactory.getLogger(MavenResolver.class);

    @Inject
    private RepositorySystem system;

    @Inject
    private RepositorySystemSession session;

    @Inject @Named("default-reps")
    private List<RemoteRepository> repositories;

    @Inject @Named("orienteer-versions")
    private Map<String, String> orienteerVersions;

    private Path parentPom;

    @Inject @Named("orienteer-default-dependencies")
    private Set<Dependency> coreDependencies;

    @Inject
    public void setParentPom(@Named("parent-pom") String parentPom) {
        this.parentPom = Paths.get(parentPom);
    }

    private Optional<Path> getPomXml(Path file) {
        Optional<Path> pomXml = Optional.absent();
        if (file.toString().endsWith(".xml")) {
            pomXml = Optional.of(file);
        } else if (file.toString().endsWith(".jar")) {
            pomXml = JarReader.getPomFromJar(file);
        }
        return pomXml;
    }

    public List<Path> resolveDependencies(String path) {
        return resolveDependencies(Paths.get(path));
    }

    public List<Path> resolveDependencies(Path file) {
        if (file == null) {
            LOG.error("File path cannot be null!");
            return Lists.newArrayList();
        }
        Optional<Path> optionalPom = getPomXml(file);
        if (!optionalPom.isPresent()) {
            LOG.error("Path " + file + " is not jar or pom file!");
            return Lists.newArrayList();
        }
        List<Path> jarDependencies = Lists.newArrayList();
        Path pomXml = optionalPom.get();

        Set<Dependency> dependencies = PomXmlParser.readDependencies(pomXml, orienteerVersions);
        for (Dependency dependency : dependencies) {
            if (!coreDependencies.contains(dependency)) {
                Optional<Path> pathOptional = resolveArtifact(dependency);
                if (pathOptional.isPresent()) jarDependencies.add(pathOptional.get());
            }
        }

        return jarDependencies;
    }

    public Optional<Path> resolveArtifact(Dependency dependency) {
        return resolveArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getArtifactVersion());
    }

    public Optional<Path> resolveArtifact(String group, String artifact, String version) {
        return resolveArtifact(String.format("%s:%s:%s", group, artifact, version));
    }

    public Optional<Path> resolveArtifact(String groupArtifatVersion) {
        Optional<Path> optionalPath = Optional.absent();
        Artifact artifact = new DefaultArtifact(groupArtifatVersion);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setRepositories(repositories);
        artifactRequest.setArtifact(artifact);
        try {
            ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
            LOG.info("artifact result: " + artifactResult);
            optionalPath = Optional.of(artifactResult.getArtifact().getFile().toPath());
        } catch (ArtifactResolutionException e) {
            LOG.error("Cannot create request: " + artifactRequest);
            if (LOG.isDebugEnabled())
                e.printStackTrace();
        }
        return optionalPath;
    }

    private Optional<String> getSnapshotVersion(String group, String artifact) {

        VersionRangeRequest request = new VersionRangeRequest();
        request.setArtifact(new DefaultArtifact(group, artifact, "jar", "1.3-SNAPSHOT"));
        request.setRepositories(repositories);
        Optional<String> version = Optional.absent();
        try {
            VersionRangeResult result = system.resolveVersionRange(session, request);
            Version highestVersion = result.getHighestVersion();
            LOG.info("highestVersion: " + highestVersion);
            if (highestVersion != null) version = Optional.of(highestVersion.toString());
        } catch (VersionRangeResolutionException e) {
            LOG.error("Cannot get snapshot version for: " + group + ":" + artifact);
            if (LOG.isDebugEnabled())
                e.printStackTrace();
        }
        return version;
    }
//
//    public static void main(String[] args) throws Exception {
////        load();
//        URL url = new URL("https://jitpack.io/");
//        List<Path> certificate = getCertificate(url);
//        for (Path path : certificate) {
//            LOG.info("certificate: " + path);
//        }
//    }
//
//    private static void load() {
//        Injector injector = Guice.createInjector(new OModuleExecutorInitModule());
//        MavenResolver resolver = injector.getInstance(MavenResolver.class);
//
////        String gav = "org.orienteer:orienteer-core:1.3-SNAPSHOT";
//        String gav = "com.github.rubenlagus:TelegramBots:2.4.0";
//        Optional<Path> file = resolver.resolveArtifact(gav);
//        LOG.info("file present: " + file.isPresent());
//        LOG.info("file: " + file.orNull());
//    }
//
//    private static KeyStore trustCertificates(List<Path> certificates) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
//        //Put everything after here in your function.
//        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//        trustStore.load(null);//Make an empty store
//        for (Path path : certificates) {
//            InputStream fis = Files.newInputStream(path);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            while (bis.available() > 0) {
//                Certificate cert = cf.generateCertificate(bis);
//                trustStore.setCertificateEntry("fiddler" + bis.available(), cert);
//            }
//        }
//
//        return trustStore;
//    }
//
//    private static List<Path> getCertificate(URL url) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateEncodingException {
//        List<Path> certificates = Lists.newArrayList();
//        Path certificate = Paths.get(System.getProperty("user.dir") + "/orienteer-core/tmp/certificates/");
//        if (!Files.exists(certificate)) Files.createDirectories(certificate);
//        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//        connection.connect();
//        Certificate[] serverCertificates = connection.getServerCertificates();
//        int i = 1;
//        for (Certificate cer : serverCertificates) {
//            if (cer instanceof X509Certificate) {
//                ( (X509Certificate) cer).checkValidity();
//                Path path = certificate.resolve("certificate_" + i + ".crt");
//                FileOutputStream os = new FileOutputStream(path.toFile());
//                i++;
//                os.write(cer.getEncoded());
//                certificates.add(path);
//            }
//        }
//        return certificates;
//    }

}
