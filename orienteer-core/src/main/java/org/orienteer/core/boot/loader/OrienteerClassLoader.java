package org.orienteer.core.boot.loader;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.SetModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.boot.loader.util.MavenResolver;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil.*;

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
     * Contains information about disabled modules when Orienteer reloads
     */
    private static IModel<Set<OArtifact>> disabledModules = new SetModel<>(Sets.<OArtifact>newHashSet());

    /**
     * Create trusted and untrusted OrienteerClassLoader
     * @param parent - classloader for delegate loading classes
     */
    public static void initOrienteerClassLoaders(ClassLoader parent) {
    	parentClassLoader = parent;
        Map<Path, OArtifact> oArtifacts = getOArtifactsMetadataInMap();
        Set<Path> jars = getJarsInArtifactsFolder();

        List<OArtifact> modulesForLoad = Lists.newArrayList();
        Set<Path> paths = oArtifacts.keySet();
        modulesForLoad.addAll(updateMetadataFromJars(Lists.newArrayList(Sets.difference(jars, paths))));
        modulesForLoad.addAll(oArtifacts.values());
        modulesForLoad = filterModules(modulesForLoad);

        if (modulesForLoad.size() != 0)
            LOG.info("Resolving dependencies for {} module(s). Please wait...", modulesForLoad.size());
        MavenResolver.get().setDependencies(modulesForLoad);
        
        trustedOrienteerClassLoader = new OrienteerClassLoader(parent);
        if (useUnTrusted) {
            untrustedOrienteerClassLoader = new OrienteerClassLoader(trustedOrienteerClassLoader);
        } else {
            untrustedOrienteerClassLoader = null;
        }

        for (OArtifact oArtifact : modulesForLoad) {
			if(oArtifact.isTrusted())
			    trustedOrienteerClassLoader.addOArtifactToClassLoaderResources(oArtifact);
			else if (useUnTrusted)
			    untrustedOrienteerClassLoader.addOArtifactToClassLoaderResources(oArtifact);
		}
    }

    /**
     * Filter modules and disable untrusted modules if {@link OrienteerClassLoader#useUnTrusted} is false.
     * @param modules {@link List<OArtifact>} trusted and untrusted modules.
     * @return filtered modules. If {@link OrienteerClassLoader#useUnTrusted} is true - return all modules.
     * If {@link OrienteerClassLoader#useUnTrusted} is false return only trusted modules and
     * untrusted modules disable in metadata.xml
     */
    private static List<OArtifact> filterModules(List<OArtifact> modules) {
        List<OArtifact> result = Lists.newArrayList();
        Set<OArtifact> disabled = disabledModules.getObject();
        for (OArtifact module : modules) {
            if (module.isLoad() && (useUnTrusted || module.isTrusted()) && !useOrienteerClassLoader) {
                result.add(module);
            } else if (module.isLoad() && (!useUnTrusted || useOrienteerClassLoader)) {
                module.setLoad(false);
                disabled.add(module);
                updateOArtifactInMetadata(module);
            }
        }
        return result;
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
     * @return true if use untrusted classloader false in otherwise.
     */
    public static boolean isUseUnTrusted() {
        return useUnTrusted;
    }

    /**
     * @return true if use custom Orienteer (container) classloader.
     */
    public static boolean isUseOrienteerClassLoader() {
        return useOrienteerClassLoader;
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
	        }
	
	        return modules;
		} else return new ArrayList<>();
    }


    public static IModel<Set<OArtifact>> getDisabledModules() {
	    return disabledModules;
    }

    public static void clearDisabledModules() {
	    if (disabledModules != null && disabledModules.getObject() != null) {
	        disabledModules.getObject().clear();
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