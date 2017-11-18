package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.apache.http.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility class for work with jar contents.
 * Reads from jar file, search jars in folder
 */
class JarUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JarUtils.class);
    private final Path modulesFolder;

    JarUtils(InitUtils initUtils) {
        this.modulesFolder = initUtils.getPathToModulesFolder();
    }

    /**
     * Search pom.xml if jar file
     * @param path {@link Path} of jar file
     * @return {@link Path} of pom.xml or Optional.absent() if pom.xml is not present
     * @throws IllegalArgumentException if path is null
     */
    public Path getPomFromJar(String path) {
        Args.notNull(path, "path");
        return getPomFromJar(Paths.get(path));
    }

    /**
     * Search pom.xml if jar file
     * @param path {@link Path} of jar file
     * @return {@link Path} of pom.xml or Optional.absent() if pom.xml is not present
     * @throws IllegalArgumentException if path is null
     */
    public Path getPomFromJar(Path path) {
        Args.notNull(path, "path");
        Path result = null;
        try {
            JarFile jarFile = new JarFile(path.toFile());
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith("pom.xml")) {
                    result = unarchivePomFile(jarFile, jarEntry);
                    break;
                }
            }
            jarFile.close();
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Cannot open file to read. File: " + path.toAbsolutePath(), e);
            }
        }
        return result;
    }

    /**
     * Unarchive pom.xml from jar file
     * @param jarFile  jar file which contains pom.xml
     * @param jarEntry {@link JarEntry} of pom.xml
     * @return {@link Path} of pom.xml or Optional.absent() if pom.xml is not present
     * @throws IOException
     */
    private Path unarchivePomFile(JarFile jarFile, JarEntry jarEntry) throws IOException {
        Path result = null;

        int pointer = jarEntry.getName().lastIndexOf("/") + 1;
        String fileName = jarEntry.getName().substring(pointer);
        Path path = File.createTempFile(fileName, ".xml").toPath();

        try (BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(jarEntry));
             BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(path))){
            int readBytes;
            byte [] buff = new byte[4096];
            while ((readBytes = in.read(buff)) != -1) {
                out.write(buff, 0, readBytes);
            }
            result = path;
        } catch (IOException e) {
            Files.deleteIfExists(path);
            LOG.error("Can't unarchive " + jarEntry.getName(), e);
        }
        return result;
    }

    /**
     * Search jar files in artifacts folder
     * @return list with {@link Path} of jar files or empty list if jars don't present in artifact folder
     */
    public List<Path> searchJarsInArtifactsFolder() {
        return searchJarsInFolder(modulesFolder);
    }

    /**
     * Search new jars in folder
     * @param folder folder for search
     * @param oldJars list {@link Path} of old jars
     * @return list with {@link Path} of new jars or empty list if new jars don't present in folder
     * @throws IllegalArgumentException if folder or oldJars is null
     */
    public List<Path> searchNewJarsInFolder(String folder, List<Path> oldJars) {
        Args.notNull(folder, "folder");
        Args.notNull(oldJars, "oldJars");
        return searchNewJarsInFolder(Paths.get(folder), oldJars);
    }

    /**
     * Search jars in folder
     * @param folder {@link Path} in which will be search
     * @return list of {@link Path} with jars or empty list if jars don't present in folder
     * @throws IllegalArgumentException if folder is null
     */
    public List<Path> searchJarsInFolder(Path folder) {
        Args.notNull(folder, "folder");
        return searchJarsInFolder(folder, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
    }

    /**
     * Search jars in folder
     * @param folder folder in which will be search
     * @return list of {@link Path} with jars or empty list if jars don't present in folder
     * @throws IllegalArgumentException if folder is null
     */
    public List<Path> searchJarsInFolder(String folder) {
        Args.notNull(folder, "folder");
        return searchJarsInFolder(Paths.get(folder), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
    }

    /**
     * Search jars in folder
     * @param folder {@link Path} of folder where is search
     * @param filter {@link FilenameFilter} for search
     * @return list {@link Path} of jars or empty list if folder is not folder or jars not present in folder.
     */
    private List<Path> searchJarsInFolder(Path folder, FilenameFilter filter) {
        if (!Files.isDirectory(folder)) return Lists.newArrayList();
        List<Path> jars = Lists.newArrayList();
        File file = folder.toFile();
        File[] list = file.listFiles(filter);
        for (File f : list) {
            jars.add(f.toPath().toAbsolutePath());
        }
        return jars;
    }


    /**
     * Search new jars in folder
     * @param folder {@link Path} of folder where is search
     * @param jars list {@link Path} of old jars
     * @return list {@link Path} of jars or empty list if folder is not folder or new jars not present in folder.
     */
    public List<Path> searchNewJarsInFolder(Path folder, final List<Path> jars) {
        List<Path> jarsInFolder = searchJarsInFolder(folder, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") && !jars.contains(dir.toPath().resolve(name));
            }
        });
        if (jarsInFolder != null) jars.addAll(jarsInFolder);
        return jarsInFolder != null ? jarsInFolder : Lists.<Path>newArrayList();
    }

    /**
     * Search class name of {@link org.apache.wicket.IInitializer} in jar file
     * @param pathToJar jar file
     * @return {@link String} of class name or Optional.absent()
     *                                 if {@link org.apache.wicket.IInitializer} is not present in jar file.
     * @throws IllegalArgumentException if pathToJar is null
     */
    public String searchOrienteerInitModule(Path pathToJar) {
        Args.notNull(pathToJar, "pathToJar");
        if (!pathToJar.toString().endsWith(".jar")) return null;

        String ret = null;
        final String initModuleStart = "org.orienteer";
        final String initModuleEnd   = "Initializer.class";
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(pathToJar.toFile());

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName().replace('/', '.');
                if (!entryName.contains("org."))
                    continue;
                entryName = entryName.substring(entryName.indexOf("org."));
                if (entryName.startsWith(initModuleStart) && entryName.endsWith(initModuleEnd)) {
                    ret = entryName.substring(0, entryName.indexOf(".class"));
                    break;
                }
            }
            jarFile.close();
        } catch (IOException e) {
            LOG.error("Cannot read jar file: " + pathToJar.toAbsolutePath(), e);
        }
        return ret;
    }

}
