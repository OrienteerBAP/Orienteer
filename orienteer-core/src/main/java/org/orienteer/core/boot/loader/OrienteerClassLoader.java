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
     * Add Orienteer modules to classloader resources.
     * @param modules - list which contains modules for add
     * @throws NullPointerException if jar file of module is null
     */
    public void addOArtifactsToClassLoaderResources(List<OArtifact> modules) {
        for(OArtifact metadata : modules) {
        	addOArtifactToClassLoaderResources(metadata);
        }
    }
    
    /**
     * Add Orienteer module to classloader resources.
     * @param metadata - metadata of artifact to add
     * @throws NullPointerException if jar file of module is null
     */
    public void addOArtifactToClassLoaderResources(OArtifact metadata) {
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

    @Override
    public String toString() {
        String trusted = "trustedOrienteerClassLoader";
        String unTrusted = "unTrustedOrienteerClassLoader";
        String custom = "customOrienteerClassLoader";
        return useUnTrusted ? unTrusted : (useOrienteerClassLoader ? custom : trusted);
    }
}