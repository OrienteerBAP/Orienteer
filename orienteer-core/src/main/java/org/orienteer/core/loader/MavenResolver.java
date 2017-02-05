package org.orienteer.core.loader;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.orienteer.core.loader.util.ConsoleDependencyGraphDumper;
import org.orienteer.core.loader.util.JarReader;
import org.orienteer.core.loader.util.PomXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
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
    private Injector injector;

    @Inject
    private RepositorySystem system;

    @Inject @Named("default-reps")
    private List<RemoteRepository> repositories;

    @Inject @Named("orienteer-versions")
    private Map<String, String> orienteerVersions;

    @Inject @Named("orienteer-default-dependencies")
    private Set<Dependency> coreDependencies;

    private Optional<Path> getPomXml(Path file) {
        Optional<Path> pomXml = Optional.absent();
        if (file.toString().endsWith(".xml")) {
            pomXml = Optional.of(file);
        } else if (file.toString().endsWith(".jar")) {
            pomXml = JarReader.getPomFromJar(file);
        }
        return pomXml;
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
        Path pomXml = optionalPom.get();
        List<Path> jarDependencies = Lists.newArrayList();
        Set<Dependency> dependencies = PomXmlParser.readDependencies(pomXml, orienteerVersions);
        for (Dependency dependency : dependencies) {
            if (!coreDependencies.contains(dependency)) {
                Optional<Path> pathOptional = resolveArtifact(dependency);
                if (pathOptional.isPresent()) jarDependencies.add(pathOptional.get());
            }
        }

        return jarDependencies;
    }

    public List<Path> resolveDependencies(Dependency dependency)
            throws ArtifactDescriptorException, DependencyCollectionException {
        if (dependency == null) {
            return Lists.newArrayList();
        }
        return resolveDependencies(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getArtifactVersion());
    }

    public List<Path> resolveDependencies(String group, String artifact, String version)
            throws ArtifactDescriptorException, DependencyCollectionException {
        if (Strings.isNullOrEmpty(group) || Strings.isNullOrEmpty(artifact) || Strings.isNullOrEmpty(version)) {
            return Lists.newArrayList();
        }
        return resolveDependencies(String.format("%s:%s:%s", group, artifact, version));
    }

    public List<Path> resolveDependencies(String groupArtifactVersion) throws ArtifactDescriptorException,
            DependencyCollectionException {
        if (Strings.isNullOrEmpty(groupArtifactVersion)) return Lists.newArrayList();

        List<Path> dependencies = Lists.newArrayList();
        RepositorySystemSession session = getSessionfForResolvingDependencies();
        Artifact artifact = new DefaultArtifact(groupArtifactVersion);
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact( artifact );
        descriptorRequest.setRepositories(repositories);
        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor( session, descriptorRequest );

        CollectRequest collectRequest = getCollectRequest(descriptorRequest, descriptorResult);
        CollectResult collectResult = system.collectDependencies( session, collectRequest );
        if (LOG.isDebugEnabled()) {
            LOG.info("Resolved dependencies for " + groupArtifactVersion);
            collectResult.getRoot().accept(new ConsoleDependencyGraphDumper());
        }

        return dependencies;
    }

    public Optional<Path> resolveArtifact(Dependency dependency) {
        return resolveArtifact(
                dependency.getGroupId(), dependency.getArtifactId(), dependency.getArtifactVersion());
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
            RepositorySystemSession session = getSessionForResolvingArtifact();
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

    private RepositorySystemSession getSessionForResolvingArtifact() {
        return injector.getInstance(DefaultRepositorySystemSession.class);
    }

    private RepositorySystemSession getSessionfForResolvingDependencies() {
        DefaultRepositorySystemSession session = injector.getInstance(DefaultRepositorySystemSession.class);
        session.setConfigProperty( ConflictResolver.CONFIG_PROP_VERBOSE, true );
        session.setConfigProperty( DependencyManagerUtils.CONFIG_PROP_VERBOSE, true );
        return session;
    }

    private CollectRequest getCollectRequest(ArtifactDescriptorRequest descriptorRequest,
                                             ArtifactDescriptorResult descriptorResult) {
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRootArtifact( descriptorResult.getArtifact() );
        collectRequest.setDependencies( descriptorResult.getDependencies() );
        collectRequest.setManagedDependencies( descriptorResult.getManagedDependencies() );
        collectRequest.setRepositories( descriptorRequest.getRepositories() );
        return collectRequest;
    }

//    public static void main(String[] args) throws ArtifactDescriptorException, DependencyCollectionException {
//        Injector injector = Guice.createInjector(new OModuleExecutorInitModule());
//        MavenResolver resolver = injector.getInstance(MavenResolver.class);
//        resolver.resolveDependencies("org.orienteer:devutils:1.3-SNAPSHOT");
//    }

//    public static void main(String[] args) throws Exception {
//        URL url = new URL("https://jitpack.io/");
//        List<Path> certificate = getCertificates(url);
//        trustCertificates(certificate);
//        Injector injector = Guice.createInjector(new OModuleExecutorInitModule());
//        MavenResolver resolver = injector.getInstance(MavenResolver.class);
//        String gav = "com.github.rubenlagus:TelegramBots:2.4.0";
//        Optional<Path> file = resolver.resolveArtifact(gav);
//        LOG.info("file present: " + file.isPresent());
//        LOG.info("file: " + file.orNull());
//
//    }
//
//    private void load() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
//        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//        final Path keyStore = Paths.get(System.getProperty("user.dir") + "/orienteer-core/tmp/certificates/keystore");
//        FileInputStream fin = new FileInputStream(keyStore.toFile());
//        trustStore.load(fin, "password".toCharArray());
//    }
//
//    private static void trustCertificates(List<Path> certificates) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
//        //Put everything after here in your function.
//        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//        final Path keyStore = Paths.get(System.getProperty("user.dir") + "/orienteer-core/tmp/certificates/keystore");
//        trustStore.load(null);//Make an empty store
//        int i = 0;
//        for (Path path : certificates) {
//            InputStream fis = Files.newInputStream(path);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            while (bis.available() > 0) {
//                Certificate cert = cf.generateCertificate(bis);
//                trustStore.setCertificateEntry("cert_" + i, cert);
//            }
//            i++;
//        }
//        FileOutputStream fos = new FileOutputStream(keyStore.toFile());
//        trustStore.store(fos, "password".toCharArray());
//        fos.close();
//        System.setProperty("javax.net.ssl.trustStore", keyStore.toAbsolutePath().toString());
//    }
//
//    private static List<Path> getCertificates(URL url) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateEncodingException {
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
