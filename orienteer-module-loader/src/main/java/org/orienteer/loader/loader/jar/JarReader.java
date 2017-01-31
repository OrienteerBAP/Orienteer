package org.orienteer.loader.loader.jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Vitaliy Gonchar
 */
public abstract class JarReader {
    private static final Logger LOG = LoggerFactory.getLogger(JarReader.class);

    public static Path getPomFromJar(String path) {
        return getPomFromJar(Paths.get(path));
    }

    public static Path getPomFromJar(Path path) {
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
                LOG.error("Cannot open file to read. File: " + path.toAbsolutePath());
                e.printStackTrace();
            }
        }
        return result;
    }

    private static Path unarchivePomFile(JarFile jarFile, JarEntry jarEntry) throws IOException {
        int pointer = jarEntry.getName().lastIndexOf("/") + 1;
        String fileName = jarEntry.getName().substring(pointer);
        pointer = jarFile.getName().lastIndexOf("/") + 1;
        String folder = jarFile.getName().substring(0, pointer);
        Path pomFolder = Paths.get(folder + "pom/");
        if (!Files.exists(pomFolder))
            Files.createDirectory(pomFolder);
        Path result = pomFolder.resolve(jarFile.getName().substring(pointer).replace("jar", fileName));

        try (BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(jarEntry));
             BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(result))){
            int readBytes;
            byte [] buff = new byte[4096];
            while ((readBytes = in.read(buff)) != -1) {
                out.write(buff, 0, readBytes);
            }
        } catch (IOException e) {
            Files.deleteIfExists(result);
            result = null;
            if (LOG.isDebugEnabled()) {
                LOG.error("Cannot unarchive " + jarEntry.getName());
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Set<Path> readNewJarsInFolder(String folder, Set<Path> oldJars) {
        return readNewJarsInFolder(Paths.get(folder), oldJars);
    }

    public static Set<Path> readJarsInFolder(Path folder) {
        return readJarsInFolder(folder, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
    }

    public static Set<Path> readJarsInFolder(String folder) {
        return readJarsInFolder(Paths.get(folder), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
    }

    private static Set<Path> readJarsInFolder(Path folder, FilenameFilter filter) {
        if (!Files.isDirectory(folder)) return Collections.emptySet();
        Set<Path> jars = new HashSet<>();
        File file = folder.toFile();
        File[] list = file.listFiles(filter);
        for (File f : list) {
            jars.add(f.toPath());
        }
        return jars;
    }


    public static Set<Path> readNewJarsInFolder(Path folder, final Set<Path> jars) {
        Set<Path> jarsInFolder = readJarsInFolder(folder, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") && !jars.contains(dir.toPath().resolve(name));
            }
        });
        if (jarsInFolder != null) jars.addAll(jarsInFolder);
        return jarsInFolder != null ? jarsInFolder : Collections.<Path>emptySet();
    }

    public static String searchOrienteerInitModule(Path pathToJar) throws IOException {
        if (!pathToJar.toString().endsWith(".jar")) return null;

        String fullClassName = null;
        final String initModuleStart = "org.orienteer";
        final String initModuleEnd   = "Initializer.class";
        JarFile jarFile = new JarFile(pathToJar.toFile());
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName().replace('/', '.');
            if (!entryName.contains("org."))
                continue;
            entryName = entryName.substring(entryName.indexOf("org."));
            if (entryName.startsWith(initModuleStart) && entryName.endsWith(initModuleEnd)) {
                fullClassName = entryName.substring(0, entryName.indexOf(".class"));
                break;
            }
        }
        jarFile.close();
        return fullClassName;
    }

}
