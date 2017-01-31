package org.orienteer.loader.loader;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.loader.loader.jar.JarReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JarClassLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
public abstract class JarModules {
    private static final Set<Path> JARS_CACHE = new HashSet<>();

    private static final Logger LOG = LoggerFactory.getLogger(JarModules.class);

    @Inject @Named("jars")
    private static Path jarFolder;
    @Inject
    private static Injector injector;

    public static boolean addModule(Path jarModule) {
        if (!jarModule.toString().endsWith(".jar")) return false;
        boolean add = JARS_CACHE.add(jarModule);
        if (add) {
            LOG.info("Add jar module " + jarModule + " to cache");
        } else LOG.info("Cannot add jar module " + jarModule + " to cache. Jar module is already exists in cache!");
        return add;
    }

//    public static void removeModule(IInitializer initializer) {
//        boolean remove = JARS_CACHE.remove(initializer);
//        if (remove) {
//            LOG.info("Remove initializer " + initializer + " from cache");
//        } else LOG.warn("Cannot remove initializer " + initializer + " from cache. Initializer does not exists in cache!");
//    }

    public static void loadModules(Application app) {
        LOG.info("Start load jar modules");
        loadJars();
        Set<String> classNames = getModulesClassNames();
//        classNames.add("org.orienteer.devutils.Initializer");
        Thread.currentThread().setContextClassLoader(injector.getInstance(JarClassLoader.class));
        for (String fullClassName : classNames) {
            IInitializer initializer;
            try {
                initializer = (IInitializer) load(fullClassName);
                if (initializer != null) initializer.init(app);
                else throw new NullPointerException();
                LOG.info(fullClassName + "     load:     " + initializer);
            } catch (Exception e) {
                LOG.error("Cannot load: " + fullClassName);
                e.printStackTrace();
            }
        }
        LOG.info("End load jar modules");
    }

    private static void loadAllClasses(Set<String> classes, String ignoredClass) {
        for (String clazz : classes) {
            if (!clazz.equals(ignoredClass)) {
                try {
                    load(clazz);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Set<String> getModulesClassNames() {
        Set<String> names = new HashSet<>();
        for (Path jar : JARS_CACHE) {
            try {
                String className = JarReader.searchOrienteerInitModule(jar);
                if (className != null) names.add(className);
            } catch (IOException ex) {
                LOG.error("Cannot load module - " + jar.toAbsolutePath());
                if (LOG.isDebugEnabled()) ex.printStackTrace();
            }
        }
        return names;
    }

    private static Object load(String fullClassName) throws Exception {
        ODependencyLoader loader = injector.getInstance(ODependencyLoader.class);
        return loader.newInstance(fullClassName);
    }

    private static void loadJars() {
        Set<Path> jarsInFolder = JarReader.readJarsInFolder(jarFolder);
        JARS_CACHE.addAll(jarsInFolder);
    }

    public static Set<Path> getJarsCache() {
        return JARS_CACHE;
    }
}
