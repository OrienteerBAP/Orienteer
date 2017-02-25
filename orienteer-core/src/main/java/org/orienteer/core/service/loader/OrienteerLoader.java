package org.orienteer.core.service.loader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import org.kevoree.kcl.api.FlexyClassLoader;
import org.kevoree.kcl.impl.FlexyClassLoaderImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 */
public class OrienteerLoader extends FlexyClassLoaderImpl {

    private final ClassLoader parent;

    private OrienteerLoader(ClassLoader parent) {
        super();
        this.parent = parent;
    }

    public static OrienteerLoader get(ClassLoader parent) {
        return new OrienteerLoader(parent);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> resources = getListFromEnumeration(super.getResources(name));
        resources.addAll(getListFromEnumeration(parent.getResources(name)));

        return Collections.enumeration(resources);
    }

    private List<URL> getListFromEnumeration(Enumeration<URL> resources) {
        List<URL> list = Lists.newArrayList();
        while (resources.hasMoreElements()) {
            list.add(resources.nextElement());
        }
        return list;
    }

    public ImmutableSet<Class<?>> getAllClassesInPackage(String packageName) {
        ImmutableSet.Builder<Class<?>> builder = ImmutableSet.builder();
        String packagePrefix = packageName + '.';
        ImmutableMap<File, ClassLoader> entries = getClassPathEntries(this);
        return builder.build();
    }


    private ImmutableMap<File, ClassLoader> getClassPathEntries(ClassLoader classloader) {
        LinkedHashMap<File, ClassLoader> entries = Maps.newLinkedHashMap();
        // Search parent first, since it's the order ClassLoader#loadClass() uses.
        ClassLoader parent = classloader.getParent();
        if (parent != null) {
            entries.putAll(getClassPathEntries(parent));
        }
        if (classloader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classloader;
            for (URL entry : urlClassLoader.getURLs()) {
                if (entry.getProtocol().equals("file")) {
                    File file = new File(entry.getFile());
                    if (!entries.containsKey(file)) {
                        entries.put(file, classloader);
                    }
                }
            }
        } else if (classloader instanceof FlexyClassLoader) {
            for (URL entry : getAllResources()) {
                if (entry.getProtocol().equals("file")) {
                    File file = new File(entry.getFile());
                    if (!entries.containsKey(file)) {
                        entries.put(file, classloader);
                    }
                }
            }
        }
        return ImmutableMap.copyOf(entries);
    }

    private class ClassInfo {
        private final String name;
        private final ClassLoader classLoader;

        public ClassInfo(String name, ClassLoader classLoader) {
            this.name = name;
            this.classLoader = classLoader;
        }

        public String getName() {
            return name;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }
    }
}
