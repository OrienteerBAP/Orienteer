package org.orienteer.core.boot.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.boot.loader.util.MavenResolver;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;
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
 * @author Vitaliy Gonchar
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
        if (!isClassLoaderOn())
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

    public static void clear() {
        untrustedClassLoader = null;
        trustedClassLoader = null;
    }

    /**
     * On OrienteerClassLoader
     */
    public static void on() {
        if (!orienteerClassLoaderOn)
            orienteerClassLoaderOn = true;
    }

    public static void off() {
        if (orienteerClassLoaderOn)
            orienteerClassLoaderOn = false;
    }

    public static boolean isClassLoaderOn() {
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
        Map<Path, OModuleConfiguration> modulesConfigurations = OrienteerClassLoaderUtil.getOModulesConfigurationsMetadataInMap();
        List<Path> jars = OrienteerClassLoaderUtil.getJarsInOModulesConfigurationsFolder();

        List<OModuleConfiguration> modulesForLoad;
        if (modulesConfigurations.isEmpty()) {
            modulesForLoad = createModules(jars);
        } else {
            modulesForLoad = getUpdateModules(modulesConfigurations);
            resolver.setDependencies(modulesForLoad);
        }
        modulesForLoad = searchTrustyModules(modulesForLoad, parent);
        addModulesToClassLoaderResources(getTrustedModules(modulesForLoad));
        untrustedClassLoader = new OrienteerClassLoader(getUnTrustedModules(modulesForLoad), this);
    }

    /**
     * Constructor for  untrusted Orienteer classloader.
     * @param unTrustedModules - untrusted modules for load in untrusted classloader
     * @param parent - parent classloader
     */
    private OrienteerClassLoader(List<OModuleConfiguration> unTrustedModules, ClassLoader parent) {
	    super(new URL[0], parent);
	    addModulesToClassLoaderResources(unTrustedModules);
    }

    /**
     * Test Orienteer modules - load it together.
     * @param unTrustedModules - untrusted modules
     * @param parent - parent classloader
     * @return list with tested Orienteer modules
     */
    private List<OModuleConfiguration> searchTrustyModules(List<OModuleConfiguration> unTrustedModules,
                                                           ClassLoader parent) {
        List<OModuleConfiguration> trustyModules = Lists.newArrayList();
        OrienteerSandboxClassLoader sandboxClassLoader = new OrienteerSandboxClassLoader(parent);
        for (OModuleConfiguration module : unTrustedModules) {
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

    private void addModulesToClassLoaderResources(List<OModuleConfiguration> modules) {
        for(OModuleConfiguration metadata : modules) {
            try {
                addURL(metadata.getArtifact().getFile().toURI().toURL());
                for (OArtifactReference artifact : metadata.getDependencies()) {
                    addURL(artifact.getFile().toURI().toURL());
                }
            } catch (MalformedURLException e) {
                LOG.error("Can't load dependency", e);
            }
        }
    }
	
	private List<OModuleConfiguration> createModules(List<Path> jars) {
        List<OModuleConfiguration> modulesForLoad = resolver.getResolvedModulesConfigurations(jars);
        if (modulesForLoad.size() > 0) {
            OrienteerClassLoaderUtil.createOModulesConfigurationsMetadata(modulesForLoad);
        } else OrienteerClassLoaderUtil.deleteMetadataFile();

        return modulesForLoad;
    }
	
    private List<OModuleConfiguration> getUpdateModules(Map<Path, OModuleConfiguration> modules) {
        resolveModulesWithoutMainJar(modules);
	    List<Path> jars = OrienteerClassLoaderUtil.getJarsInOModulesConfigurationsFolder();
	    List<OModuleConfiguration> modulesForWrite = getModulesForAddToMetadata(jars, modules);

	    if (modulesForWrite.size() > 0) {
            OrienteerClassLoaderUtil.updateOModuleConfigurationInMetadata(modulesForWrite);
        }

        modules = OrienteerClassLoaderUtil.getOModulesConfigurationsMetadataForLoadInMap();

        return getModulesForLoad(modules.values());
    }
    
    private List<OModuleConfiguration> getModulesForAddToMetadata(List<Path> jars, Map<Path, OModuleConfiguration> modules) {
        List<Path> modulesForWrite = Lists.newArrayList();
        Set<Path> jarsInMetadata = modules.keySet();
        for (Path pathToJar : jars) {
            if (!jarsInMetadata.contains(pathToJar)) {
                modulesForWrite.add(pathToJar);
            }
        }
        return resolver.getResolvedModulesConfigurations(modulesForWrite);
    }

    private List<OModuleConfiguration> getModulesForLoad(Collection<OModuleConfiguration> modules) {
        List<OModuleConfiguration> modulesForLoad = Lists.newArrayList();
        for (OModuleConfiguration metadata : modules) {
            if (metadata.isLoad()) modulesForLoad.add(metadata);
        }
        return modulesForLoad;
    }

    private void resolveModulesWithoutMainJar(Map<Path, OModuleConfiguration> modules) {
        List<OModuleConfiguration> modulesWithoutMainJar = getModulesWithoutMainJar(modules.values());
        if (modulesWithoutMainJar.size() > 0) {
            resolver.downloadModules(modulesWithoutMainJar);
            List<Path> keysForDelete = Lists.newArrayList();
            for (Path key : modules.keySet()) {
                if (key.toString().contains(OrienteerClassLoaderUtil.WITHOUT_JAR)) {
                    keysForDelete.add(key);
                }
            }
            for (Path key : keysForDelete) {
                modules.remove(key);
            }
            for (OModuleConfiguration module : modulesWithoutMainJar) {
                modules.put(module.getArtifact().getFile().toPath(), module);
            }
            OrienteerClassLoaderUtil.updateOModulesConfigurationsJarsInMetadata(modulesWithoutMainJar);
        }
    }

    private List<OModuleConfiguration> getModulesWithoutMainJar(Collection<OModuleConfiguration> modules) {
        List<OModuleConfiguration> result = Lists.newArrayList();
        for (OModuleConfiguration module : modules) {
            File jar = module.getArtifact().getFile();
            if (jar == null || !jar.exists()) {
                result.add(module);
            }
        }
        return result;
    }

    private List<OModuleConfiguration> getTrustedModules(List<OModuleConfiguration> modules) {
	    List<OModuleConfiguration> trustedModules = Lists.newArrayList();
	    for (OModuleConfiguration module : modules) {
	        if (module.isTrusted()) trustedModules.add(module);
        }
	    return trustedModules;
    }

    private List<OModuleConfiguration> getUnTrustedModules(List<OModuleConfiguration> modules) {
        List<OModuleConfiguration> unTrustedModules = Lists.newArrayList();
        for (OModuleConfiguration module : modules) {
            if (!module.isTrusted()) unTrustedModules.add(module);
        }
        return unTrustedModules;
    }

    private static class OrienteerSandboxClassLoader extends URLClassLoader {

        public OrienteerSandboxClassLoader(ClassLoader parent) {
            super(new URL[]{}, parent);
        }

        public void loadResourcesInClassLoader(List<OModuleConfiguration> modules) {
            for (OModuleConfiguration module : modules) {
                loadResourceInClassLoader(module);
            }
        }

        private void loadResourceInClassLoader(OModuleConfiguration module) {
            try {
                addURL(module.getArtifact().getFile().toURI().toURL());
                for (OArtifactReference artifact : module.getDependencies()) {
                    addURL(artifact.getFile().toURI().toURL());
                }
            } catch (MalformedURLException e) {
                LOG.error("Cannot load dependency.", e);
            }
        }

        public boolean test(OModuleConfiguration module) {
            boolean trusted = false;
            try {
                loadResourceInClassLoader(module);
                Path pathToJar = module.getArtifact().getFile().toPath();
                Optional<String> className = OrienteerClassLoaderUtil.searchOrienteerInitModule(pathToJar);
                if (className.isPresent()) {
                    loadClass(className.get());
                    trusted = true;
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Cannot load init class for module: " + module);
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