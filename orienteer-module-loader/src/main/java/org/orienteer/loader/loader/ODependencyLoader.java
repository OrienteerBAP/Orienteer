package org.orienteer.loader.loader;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.orienteer.loader.loader.jar.DependencyResolver;
import org.orienteer.loader.loader.jar.JarReader;
import org.orienteer.loader.loader.xml.PomParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.exception.JclException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
public class ODependencyLoader {
    @Inject @Named("pom.xml") private Path pomFile;
    @Inject @Named("jars") private Path jarFolder;
    @Inject @Named("depJars") private Path depFolder;
    @Inject private JarClassLoader jcl;
    @Inject private JclObjectFactory factory;
    @Inject private DependencyResolver resolver;

    private static final Logger LOG = LoggerFactory.getLogger(ODependencyLoader.class);
    private final Set<Path> jars = new HashSet<>();
    private Set<Dependency> resolvedDeps = new HashSet<>();
    private Set<Dependency> unResolvedDeps = new HashSet<>();
    private Map<String, String> orienteerArtifactVersions;
    private Dependency orienteerParent;
    private boolean init;

    public Object newInstance(String fullClassName) throws JclException {
        Object object;

        if (!init) {
            createDefaultDeps();
            init = true;
            jcl.add(jarFolder.toString());
        }
        if (!JarReader.readNewJarsInFolder(jarFolder, jars).isEmpty()) {
            resolveDependencies();
            jcl.add(depFolder.toString());
        }

        object = factory.create(jcl, fullClassName);
        return object;
    }

    public Object newInstanceWithoutDownload(String fullClassName) throws JclException {
        jcl.add(jarFolder.toString());
        jcl.add(depFolder.toString());
        return factory.create(jcl, fullClassName);
    }

    private void createDefaultDeps() {
        if (pomFile != null) {
            Path pom = pomFile;
            if (!Files.exists(pom)) {
                LOG.error("Pom file does not exists: " + pom);
                return;
            }
            try {
                orienteerParent = getParentDependency(pom);
                Set<Dependency> dependencies = getParentOrienteerDependencies();
                dependencies.addAll(getValideDependencies(pom));
                resolvedDeps.addAll(dependencies);
            } catch (IOException e) {
                LOG.error("Cannot read pom file: " + pom);
                if (LOG.isDebugEnabled()) e.printStackTrace();
            }
        }
    }

    private Set<Dependency> getParentOrienteerDependencies() {
        if (orienteerParent == null) return new HashSet<>();

        resolver.addNewRepositories(pomFile);
        try {
            Path parentPom = resolver.getPomDependency(orienteerParent);
            orienteerArtifactVersions = getOrienteerArtifactVersions(parentPom);
            return getValideDependencies(parentPom);
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return new HashSet<>();
    }

    private void resolveDependencies() {
        for (Path jarFile : jars) {
            resolveDependencies(jarFile);
        }
    }


    private void resolveDependencies(Path file) {
        try {
            Path pomFromJar = file.toString().endsWith(".pom") ? file : JarReader.getPomFromJar(file);
            if (pomFromJar == null)
                return;
            Set<Dependency> dependencies = getValideDependencies(file);
            if (dependencies == null || dependencies.isEmpty())
                return ;
            resolver.addNewRepositories(pomFromJar);
            for (Dependency dependency : dependencies) {
                Path dep = getPathFromDependency(dependency, depFolder);
                if (!Files.exists(dep)) {
                    dep = resolver.getJarDependency(dependency);
                }
                if (dep != null) {
                    LOG.info("Resolve: " + dependency);
                    resolvedDeps.add(dependency);
                    if (unResolvedDeps.contains(dependency))
                        unResolvedDeps.remove(dependency);
                }
            }
        } catch (IOException e) {
             e.printStackTrace();
        }
    }


    private Set<Dependency> getValideDependencies(Path file) throws IOException {
        Path pom = (file.toString().endsWith(".pom") || file.toString().endsWith("pom.xml")) ? file : JarReader.getPomFromJar(file);
        return pom != null ? getDependencies(pom) : new HashSet<Dependency>();
//        Dependency parentDependency = getParentDependency(pom);
//
//        if (parentDependency == null)
//            return dependencies;
//
//        DependencyResolver downloader = new DependencyResolver(pom, depFolder);
//        Path parentPom = downloader.getPomDependency(parentDependency);
//
//        if (parentPom == null)
//            return dependencies;
//
//        dependencies.addAll(getValideDependencies(parentPom));

//        return dependencies;
    }

    private Path getPathFromDependency(Dependency dependency, Path folder) {
        String artifactId = dependency.getArtifactId();
        String artifactVersion = dependency.getArtifactVersion();
        return folder.resolve(artifactId + "-" + artifactVersion + ".jar");
    }

    private Set<Dependency> getDependencies(Path pomFile) throws IOException {
        Set<Dependency> deps = PomParser.readDependencies(Files.newInputStream(pomFile));
        return sort(deps);
    }

    private Dependency getParentDependency(Path pomFile) throws IOException {
        return PomParser.getParentDependency(Files.newInputStream(pomFile));
    }

    public Map<String, String> getOrienteerArtifactVersions(Path pomFile) throws IOException {
        return PomParser.getArtifactVersions(Files.newInputStream(pomFile));
    }

    private Set<Dependency> sort(Set<Dependency> dependencies) {
        if (dependencies == null)
            return new HashSet<>();
        Set<Dependency> sorted = new HashSet<>();
        Iterator<Dependency> iterator = dependencies.iterator();
        while (iterator.hasNext()) {
            Dependency dependency = iterator.next();
            if (!Strings.isNullOrEmpty(dependency.getArtifactId())) {
                String artifactVersion = dependency.getArtifactVersion();
                String group = dependency.getGroupId();
                if (artifactVersion != null
                        && group != null
                        && group.equals("org.orienteer")) {
                    dependency.setArtifactVersion(orienteerParent.getArtifactVersion());
                    sorted.add(dependency);
                } else if (PomParser.isLinkToVersion(artifactVersion)) {
                    artifactVersion = orienteerArtifactVersions.get(artifactVersion);
                    if (artifactVersion != null) dependency.setArtifactVersion(artifactVersion);
                }
                if (artifactVersion != null && !artifactVersion.equals(PomParser.WITHOUT_VERSION)
                        && !PomParser.isLinkToVersion(artifactVersion)) {
                    sorted.add(dependency);
                } else unResolvedDeps.add(dependency);
            }
        }
        if (!resolvedDeps.isEmpty()) {
            sorted = Sets.difference(sorted, resolvedDeps);
//            unResolvedDeps = Sets.difference(unResolvedDeps, resolvedDeps);
        }
        return sorted;
    }

    public Set<Dependency> getResolvedDeps() {
        return resolvedDeps;
    }

    public Set<Dependency> getUnResolvedDeps() {
        return unResolvedDeps;
    }

    //
//    private void createSystemDeps() {
//        Path system = Paths.get(systemRepository);
//        if (!Files.isDirectory(system)) {
//            LOG.error("Is not folder: " + system);
//            return;
//        }
//        try {
//            readDirs(system);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void readDirs(Path dir) throws IOException {
//        if (!Files.isDirectory(dir)) {
//            Dependency Dependency = getDependencyFromPath(dir.toString());
//            defaultDeps.add(Dependency);
//            return;
//        }
//        DirectoryStream<Path> paths = Files.newDirectoryStream(dir);
//        for (Path path : paths) {
//            readDirs(path);
//        }
//    }
//
//    private Dependency getDependencyFromPath(String path) {
//        path = path.substring(systemRepository.length());
//        String file = path.substring(path.lastIndexOf("/"));
//        path = path.substring(0, path.indexOf(file));
//        String artifactVersion = path.substring(path.lastIndexOf("/"));
//        path = path.substring(0, path.indexOf(artifactVersion));
//        String artifactId = path.substring(path.lastIndexOf("/"));
//        path = path.substring(0, path.indexOf(artifactId));
//        String groupId = path.replace('/', '.');
//
//        artifactVersion = artifactVersion.substring(1);
//        artifactId = artifactId.substring(1);
//        return new Dependency(groupId, artifactId, artifactVersion);
//    }
}
