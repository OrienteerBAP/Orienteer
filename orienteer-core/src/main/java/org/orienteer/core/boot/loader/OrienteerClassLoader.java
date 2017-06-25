package org.orienteer.core.boot.loader;

import com.google.common.base.Optional;import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.boot.loader.util.MavenResolver;
import static org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil.*;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	
    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoader.class);

    private static ClassLoader parentClassLoader;
    private static OrienteerClassLoader trustedOrienteerClassLoader;
    private static OrienteerClassLoader untrustedOrienteerClassLoader;
    private static boolean useUnTrusted = true;
    private static boolean useOrienteerClassLoader = false;
    private static boolean orienteerClassLoaderOn = false;

    /**
     * Create trusted and untrusted OrienteerClassLoader
     * @param parent - classloader for delegate loading classes
     */
    public static void initOrienteerClassLoaders(ClassLoader parent) {
    	parentClassLoader = parent;
        Map<Path, OArtifact> oArtifacts = getOArtifactsMetadataInMap();
        List<Path> jars = getJarsInArtifactsFolder();

        List<OArtifact> modulesForLoad = new ArrayList<>();
        
        modulesForLoad.addAll(updateMetadataFromJars(jars));
        modulesForLoad.addAll(oArtifacts.values());
        MavenResolver.get().setDependencies(modulesForLoad);
        
        trustedOrienteerClassLoader = new OrienteerClassLoader(parent);
        untrustedOrienteerClassLoader = new OrienteerClassLoader(trustedOrienteerClassLoader);
        for (OArtifact oArtifact : modulesForLoad) {
			if(oArtifact.isTrusted()) trustedOrienteerClassLoader.addOArtifactToClassLoaderResources(oArtifact);
			else untrustedOrienteerClassLoader.addOArtifactToClassLoaderResources(oArtifact);
		}
    }

    /**
     * Get Orienteer classloader.
     * @return - return trusted or untrusted or default Orienteer classloader (if OrienteerClassLoader is off)
     */
    public static ClassLoader getClassLoader() {
        if (!isOrienteerClassLoaderEnable())
            return OrienteerWebApplication.class.getClassLoader();
        return useUnTrusted ? untrustedOrienteerClassLoader : (useOrienteerClassLoader ? parentClassLoader : trustedOrienteerClassLoader);
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
     * Add Orienteer modules to classloader resources.
     * @param modules - list which contains modules for add
     * @throws NullPointerException if jar file of module is null
     */
    private void addModulesToClassLoaderResources(List<OArtifact> modules) {
        for(OArtifact metadata : modules) {
        	addOArtifactToClassLoaderResources(metadata);
        }
    }
    
    /**
     * Add Orienteer module to classloader resources.
     * @param metadata - metadata of artifact to add
     * @throws NullPointerException if jar file of module is null
     */
    private void addOArtifactToClassLoaderResources(OArtifact metadata) {
        try {
            addURL(metadata.getArtifactReference().getFile().toURI().toURL());
            for (OArtifactReference artifact : metadata.getDependencies()) {
                addURL(artifact.getFile().toURI().toURL());
            }
        } catch (MalformedURLException e) {
            LOG.error("Can't load dependency", e);
        }
    }

    /**
     * Update modules list from modules folder.
     * @param jars jars in modules folder
     * @return list which contains Orienteer modules
     */
	private static List<OArtifact> updateMetadataFromJars(List<Path> jars) {
		if(jars!=null && !jars.isEmpty()) {
	        List<OArtifact> modules = MavenResolver.get().getResolvedOArtifacts(jars);
	        if (modules.size() > 0) {
	            updateOArtifactsInMetadata(modules);
	        } else deleteMetadataFile();
	
	        return modules;
		} else return new ArrayList<>();
    }

    /**
     * Search new modules and update metadata.xml if it's need.
     * @param oArtifacts -  modules which read from metadata.xml
     * @return modules for load
     */
    private List<OArtifact> getUpdateModules(Map<Path, OArtifact> oArtifacts) {
        resolveModulesWithoutMainJar(oArtifacts);
	    List<Path> jars = getJarsInArtifactsFolder();
	    List<OArtifact> modulesForWrite = getModulesForAddToMetadata(jars, oArtifacts);

	    if (modulesForWrite.size() > 0) {
            updateOArtifactsInMetadata(modulesForWrite);
        }

        oArtifacts = getOArtifactsMetadataForLoadInMap();

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
        return MavenResolver.get().getResolvedOArtifacts(modulesForWrite);
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
        	MavenResolver.get().downloadOArtifacts(modulesWithoutMainJar);
            List<Path> keysForDelete = Lists.newArrayList();
            for (Path key : modules.keySet()) {
                if (key.toString().contains(WITHOUT_JAR)) {
                    keysForDelete.add(key);
                }
            }
            for (Path key : keysForDelete) {
                modules.remove(key);
            }
            for (OArtifact module : modulesWithoutMainJar) {
                modules.put(module.getArtifactReference().getFile().toPath(), module);
            }
            updateOArtifactsJarsInMetadata(modulesWithoutMainJar);
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


    @Override
    public String toString() {
        String trusted = "trustedOrienteerClassLoader";
        String unTrusted = "unTrustedOrienteerClassLoader";
        String custom = "customOrienteerClassLoader";
        return useUnTrusted ? unTrusted : (useOrienteerClassLoader ? custom : trusted);
    }
}