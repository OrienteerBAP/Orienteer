package org.orienteer.core.loader;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.eclipse.aether.artifact.Artifact;
import org.kevoree.kcl.api.FlexyClassLoader;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.loader.util.metadata.OModuleMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public class OrienteerOutsideModulesManager {
    private static final Logger LOG = LoggerFactory.getLogger(OrienteerOutsideModulesManager.class);
    private static final Map<FlexyClassLoader, Class<? extends IInitializer>> INIT_CLASSES = Maps.newConcurrentMap();
    private static final String INIT_METHOD = "init";
    private static final String DESTROY_METHOD = "destroy";

    @Inject
    private OrienteerWebApplication application;

    public synchronized boolean registerModule(OModuleMetadata metadata) {
        return registerModule(metadata, false);
    }

    public synchronized boolean registerModule(OModuleMetadata metadata, boolean trustyClassLoader) {
        if (application == null) {
            LOG.error("Application cannot be null");
            return false;
        }
        FlexyClassLoader classLoader = getClassLoader(metadata, trustyClassLoader);
        boolean loadModule = false;
        try {
            Class<? extends IInitializer> loadClass = (Class<? extends IInitializer>)
                    classLoader.loadClass(metadata.getInitializerName());
            invoke(loadClass, INIT_METHOD);
            loadModule = true;
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return loadModule;
    }

    public synchronized void registerModules(List<OModuleMetadata> modules) {
        registerModules(modules, false);
    }

    public synchronized void registerModules(List<OModuleMetadata> modules, boolean trustyClassLoader) {
        for (OModuleMetadata metadata : modules) {
            registerModule(metadata, trustyClassLoader);
        }
    }

    private void invoke(Class<? extends IInitializer> loadClass, String methodName)
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException, InstantiationException {

        Object initializer = loadClass.newInstance();
        Method method = loadClass.getMethod(methodName, Application.class);
        method.invoke(initializer, application);
    }

    private FlexyClassLoader getClassLoader(OModuleMetadata metadata, boolean trustyClassLoader) {
        FlexyClassLoader classLoader = trustyClassLoader ? OLoaderStorage.getTrustyModuleLoader(false) :
                OLoaderStorage.getSandboxModuleLoader(false);
        try {
            classLoader.load(metadata.getMainArtifact().getFile().toURI().toURL());
            for (Artifact dependency : metadata.getDependencies()) {
                classLoader.load(dependency.getFile().toURI().toURL());
            }
        } catch (MalformedURLException e) {
            LOG.error("Cannot create URl from " + metadata.getMainArtifact().getFile());
            if (LOG.isDebugEnabled()) e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Cannot open file " + metadata.getMainArtifact().getFile());
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return classLoader;
    }

}
