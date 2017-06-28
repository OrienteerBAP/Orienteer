package org.orienteer.core.boot.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.boot.loader.util.MavenResolver;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orienteer's classloader loads modules.
 * Orienteer runs with all modules for load - if trusted and untrusted modules loads correctly
 * Orienteer runs only with trusted modules for load - if untrusted modules don't load correctly, but trusted modules loads correctly
 * Orienteer runs without modules - if trusted and untrusted modules don't loads correctly
 */
public class OrienteerClassLoader extends URLClassLoader {
	
	private final MavenResolver resolver = MavenResolver.get();

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoader.class);

    private static ClassLoader orienteerClassLoader;
    private static OrienteerClassLoader trustedClassLoader;
    private static OrienteerClassLoader untrustedClassLoader;
    private static boolean useUnTrusted = true;
    private static boolean useOrienteerClassLoader = false;
    private static boolean orienteerClassLoaderOn = false;

    /**
     * Create trusted and untrusted OrienteerClassLoader
     * @param parent - classloader for delegate loading classes
     */
    public static void create(ClassLoader parent) {
        trustedClassLoader = new OrienteerClassLoader(parent);
    }

    /**
     * Get Orienteer classloader.
     * @return - return trusted or untrusted or default Orienteer classloader (if OrienteerClassLoader is off)
     */
    public static ClassLoader getClassLoader() {
        if (!isOrienteerClassLoaderEnable())
            return OrienteerWebApplication.class.getClassLoader();
        return useUnTrusted ? untrustedClassLoader : (useOrienteerClassLoader ? orienteerClassLoader : trustedClassLoader);
    }

    /**
     * Disable using untrusted classloader and start using trusted Orienteer classloader.
     */
    public static void useTrustedClassLoader() {
        useUnTrusted = false;
        useOrienteerClassLoader = false;
    }

    /**
     * Disable using trusted classloader and use custom Orienteer classloader.
     */
    public static void useOrienteerClassLoader() {
        useOrienteerClassLoader = true;
        useUnTrusted = false;
    }

    /**
     * Use default classloader properties. Orienteer runs with untrusted classloader.
     */
    public static void useDefaultClassLoaderProperties() {
        useUnTrusted = true;
        useOrienteerClassLoader = false;
    }


    public static boolean isUseUnTrusted() {
        return useUnTrusted;
    }

    public static boolean isUseOrienteerClassLoader() {
        return useOrienteerClassLoader;
    }

    /**
     * Enable OrienteerClassLoader. It's possible to run Orienteer with modules
     */
    public static void enable() {
        if (!orienteerClassLoaderOn)
            orienteerClassLoaderOn = true;
    }

    /**
     * Disable OrienteerClassloader. It's not possible to run Orienteer with modules
     */
    public static void disable() {
        if (orienteerClassLoaderOn)
            orienteerClassLoaderOn = false;
    }

    /**
     * Get state of OrienteerClassLoader
     * @return true - OrienteerClassLoader is enable and Orienteer runs with untrusted or trusted classloader
     *         false - OrienteerClassLoader is disable and Orienteer runs with default classloader (container's classloader)
     */
    public static boolean isOrienteerClassLoaderEnable() {
        return orienteerClassLoaderOn;
    }

    /**
     * Constructor for trusted Orienteer classloader.
     * Test modules and resolve modules dependencies.
     * Create untrusted Orienteer classloader.
     * @param parent - classloader for delegate loading classes
     */
	private OrienteerClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
        orienteerClassLoader = parent;
        Map<Path, OArtifact> oArtifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataInMap();
        List<Path> jars = OrienteerClassLoaderUtil.getJarsInArtifactsFolder();

        List<OArtifact> modulesForLoad;
        if (oArtifacts.isEmpty() && jars.size() > 0) {
            modulesForLoad = createModules(jars);
        } else {
            modulesForLoad = getUpdateModules(oArtifacts);
            resolver.setDependencies(modulesForLoad);
        }
        modulesForLoad = searchTrustyModules(modulesForLoad, parent);
        addModulesToClassLoaderResources(getTrustedModules(modulesForLoad));
        if (useUnTrusted) {
            untrustedClassLoader = new OrienteerClassLoader(getUnTrustedModules(modulesForLoad), this);
        } else untrustedClassLoader = null;
    }

    /**
     * Constructor for  untrusted Orienteer classloader.
     * @param unTrustedModules - untrusted modules for load in untrusted classloader
     * @param parent - parent classloader
     */
    private OrienteerClassLoader(List<OArtifact> unTrustedModules, ClassLoader parent) {
	    super(new URL[0], parent);
	    addModulesToClassLoaderResources(unTrustedModules);
    }

    /**
     * Test Orienteer modules - load it together.
     * @param unTrustedModules - untrusted modules
     * @param parent - parent classloader
     * @return list with tested Orienteer modules
     */
    private List<OArtifact> searchTrustyModules(List<OArtifact> unTrustedModules,
                                                           ClassLoader parent) {
        List<OArtifact> trustyModules = Lists.newArrayList();
        OrienteerSandboxClassLoader sandboxClassLoader = new OrienteerSandboxClassLoader(parent);
        for (OArtifact module : unTrustedModules) {
            boolean isTrusted = sandboxClassLoader.test(module);
            if (isTrusted) {
                trustyModules.add(module);
            } else {
                sandboxClassLoader = new OrienteerSandboxClassLoader(parent);
                sandboxClassLoader.loadResourcesInClassLoader(trustyModules);
            }
        }
        return trustyModules;
    }


    /**
     * Add Orienteer modules to classloader resources.
     * @param modules - list which contains modules for add
     * @throws NullPointerException if jar file of module is null
     */
    private void addModulesToClassLoaderResources(List<OArtifact> modules) {
        for(OArtifact metadata : modules) {
            try {
                addURL(metadata.getArtifactReference().getFile().toURI().toURL());
                for (OArtifactReference artifact : metadata.getDependencies()) {
                    addURL(artifact.getFile().toURI().toURL());
                }
            } catch (MalformedURLException e) {
                LOG.error("Can't load dependency", e);
            }
        }
    }

    /**
     * Create modules list from modules folder.
     * @param jars jars in modules folder
     * @return list which contains Orienteer modules
     */
	private List<OArtifact> createModules(List<Path> jars) {
        List<OArtifact> modules = resolver.getResolvedoArtifacts(jars);
        if (modules.size() > 0) {
            OrienteerClassLoaderUtil.createOArtifactsMetadata(modules);
        } else OrienteerClassLoaderUtil.deleteMetadataFile();

        return modules;
    }

    /**
     * Search new modules and update metadata.xml if it's need.
     * @param oArtifacts -  modules which read from metadata.xml
     * @return modules for load
     */
    private List<OArtifact> getUpdateModules(Map<Path, OArtifact> oArtifacts) {
        resolveModulesWithoutMainJar(oArtifacts);
	    List<Path> jars = OrienteerClassLoaderUtil.getJarsInArtifactsFolder();
	    List<OArtifact> modulesForWrite = getModulesForAddToMetadata(jars, oArtifacts);

	    if (modulesForWrite.size() > 0) {
            OrienteerClassLoaderUtil.updateOoArtifactsInMetadata(modulesForWrite);
        }

        oArtifacts = OrienteerClassLoaderUtil.getOArtifactsMetadataForLoadInMap();

        return getModulesForLoad(oArtifacts.values());
    }

    /**
     * Search artifacts for add to metadata.xml
     * @param jars - jars in artifacts folder
     * @param oArtifacts - artifacts in metadata.xml
     * @return list with resolved artifact for add to metadata
     */
    private List<OArtifact> getModulesForAddToMetadata(List<Path> jars, Map<Path, OArtifact> oArtifacts) {
        List<Path> modulesForWrite = Lists.newArrayList();
        Set<Path> jarsInMetadata = oArtifacts.keySet();
        for (Path pathToJar : jars) {
            if (!jarsInMetadata.contains(pathToJar)) {
                modulesForWrite.add(pathToJar);
            }
        }
        return resolver.getResolvedoArtifacts(modulesForWrite);
    }

    /**
     * Search modules for load
     * @param modules collection with modules
     * @return modules fir load
     */
    private List<OArtifact> getModulesForLoad(Collection<OArtifact> modules) {
        List<OArtifact> modulesForLoad = Lists.newArrayList();
        for (OArtifact metadata : modules) {
            if (metadata.isLoad()) modulesForLoad.add(metadata);
        }
        return modulesForLoad;
    }

    /**
     * Download jars for modules. Search modules which {@link Path} contains {@value OrienteerClassLoaderUtil#WITHOUT_JAR} and
     * create new {@link OArtifact} for that modules.
     * @param modules modules which must resolved
     *                {@link Path} - path to jar file
     *                {@link OArtifact} - module for resolve
     */
    private void resolveModulesWithoutMainJar(Map<Path, OArtifact> modules) {
        List<OArtifact> modulesWithoutMainJar = getModulesWithoutMainJar(modules.values());
        if (modulesWithoutMainJar.size() > 0) {
            resolver.downloadOArtifacts(modulesWithoutMainJar);
            List<Path> keysForDelete = Lists.newArrayList();
            for (Path key : modules.keySet()) {
                if (key.toString().contains(OrienteerClassLoaderUtil.WITHOUT_JAR)) {
                    keysForDelete.add(key);
                }
            }
            for (Path key : keysForDelete) {
                modules.remove(key);
            }
            for (OArtifact module : modulesWithoutMainJar) {
                modules.put(module.getArtifactReference().getFile().toPath(), module);
            }
            OrienteerClassLoaderUtil.updateOArtifactsJarsInMetadata(modulesWithoutMainJar);
        }
    }

    /**
     * Search modules without correctly jar file.
     * @param modules - collection which contains modules
     * @return list of modules which does not contains correctly jar file
     */
    private List<OArtifact> getModulesWithoutMainJar(Collection<OArtifact> modules) {
        List<OArtifact> result = Lists.newArrayList();
        for (OArtifact module : modules) {
            File jar = module.getArtifactReference().getFile();
            if (jar == null || !jar.exists()) {
                result.add(module);
            }
        }
        return result;
    }

    /**
     * Search trusted modules in input list
     * @param modules list with trusted and untrusted modules
     * @return list with trusted modules
     */
    private List<OArtifact> getTrustedModules(List<OArtifact> modules) {
	    List<OArtifact> trustedModules = Lists.newArrayList();
	    for (OArtifact module : modules) {
	        if (module.isTrusted()) trustedModules.add(module);
        }
	    return trustedModules;
    }

    /**
     * Search untrusted modules in input list
     * @param modules list with trusted and untrusted modules
     * @return list with untrusted modules
     */
    private List<OArtifact> getUnTrustedModules(List<OArtifact> modules) {
        List<OArtifact> unTrustedModules = Lists.newArrayList();
        for (OArtifact module : modules) {
            if (!module.isTrusted()) unTrustedModules.add(module);
        }
        return unTrustedModules;
    }

    /**
     * Sandbox classloader for testing Orienteer modules before their loading
     */
    private static class OrienteerSandboxClassLoader extends URLClassLoader {

        public OrienteerSandboxClassLoader(ClassLoader parent) {
            super(new URL[]{}, parent);
        }

        /**
         * Test loading resources in classloader
         * @param modules - list of modules for load
         */
        public void loadResourcesInClassLoader(List<OArtifact> modules) {
            for (OArtifact module : modules) {
                loadResourceInClassLoader(module);
            }
        }

        /**
         * Load module and resources of module in classloader
         * @param module - {@link OArtifact} for load in classloader
         */
        private void loadResourceInClassLoader(OArtifact module) {
            try {
                addURL(module.getArtifactReference().getFile().toURI().toURL());
                for (OArtifactReference artifact : module.getDependencies()) {
                    addURL(artifact.getFile().toURI().toURL());
                }
            } catch (MalformedURLException e) {
                LOG.error("Cannot load dependency.", e);
            }
        }

        /**
         * Testing module. Search and load Wicket init class {@link org.apache.wicket.IInitializer}.
         * @param module - {@link OArtifact} for testing
         * @return true - if loading is success (loading without {@link ClassNotFoundException})
         *         false - if loading is failed (loading with {@link ClassNotFoundException})
         */
        public boolean test(OArtifact module) {
            boolean trusted = false;
            try {
                loadResourceInClassLoader(module);
                Path pathToJar = module.getArtifactReference().getFile().toPath();
                Optional<String> className = OrienteerClassLoaderUtil.searchOrienteerInitModule(pathToJar);
                if (className.isPresent()) {
                    loadClass(className.get());
                    trusted = true;
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Cannot search and load init class for module: {}", module);
                if (LOG.isDebugEnabled()) e.printStackTrace();
            }
            return trusted;
        }
    }

    @Override
    public String toString() {
        String trusted = "trustedOrienteerClassLoader";
        String unTrusted = "unTrustedOrienteerClassLoader";
        String custom = "customOrienteerClassLoader";
        return useUnTrusted ? unTrusted : (useOrienteerClassLoader ? custom : trusted);
    }
}