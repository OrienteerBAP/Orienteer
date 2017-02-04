package org.orienteer.core.service;

import com.google.inject.Provider;
import com.google.inject.name.Names;
import org.apache.wicket.protocol.http.WebApplication;
import org.orienteer.core.OrienteerWebApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.rest.InterceptContentFilter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Vitaliy Gonchar
 */
public class ReloadOrienteerInitModule extends OrienteerInitModule {

    private static final Logger LOG = LoggerFactory.getLogger(ReloadOrienteerInitModule.class);
    private final Map<String, Path> paths = new HashMap<>();
    private final String properties         = "orienteer.properties";
//    private final String propertiesDefault  = "src/main/resources/loader-default.properties";



    @Override
    protected void configureServlets() {

        bind(InterceptContentFilter.class).asEagerSingleton();
        filter("/orientdb/*").through(InterceptContentFilter.class);
        initResources();
        initClassLoader();
        initFilter();
        initApplication();
    }

    private void initClassLoader() {
//        requestStaticInjection(OrienteerOutsideModules.class);
//        ProxyProviderFactory.setDefaultProxyProvider(new CglibProxyProvider());
//        bind(ClassLoader.class).to(JarClassLoader.class);
//        bind(JarClassLoader.class).toProvider(new Provider<JarClassLoader>() {
//            @Override
//            public JarClassLoader get() {
//                return OLoaderStorage.getCurrentModuleLoader();
//            }
//        });
//        bind(JclObjectFactory.class).toProvider(new Provider<JclObjectFactory>() {
//            @Override
//            public JclObjectFactory get() {
//                return JclObjectFactory.getInstance(true);
//            }
//        });
    }

    private void initFilter() {

    }

    private void initApplication() {
        Properties properties = retrieveProperties();
        Names.bindProperties(binder(), properties);
        bindOrientDbProperties(properties);
        String applicationClass = properties.getProperty("orienteer.application");
        Class<? extends OrienteerWebApplication> appClass = OrienteerWebApplication.class;
        if (applicationClass != null) {
            try {
                Class<?> customAppClass = Class.forName(applicationClass);

                if (OrienteerWebApplication.class.isAssignableFrom(appClass)) {
                    appClass = (Class<? extends OrienteerWebApplication>) customAppClass;
                } else {
                    LOG.error("Orienteer application class '" + applicationClass + "' is not child class of '" + OrienteerWebApplication.class + "'. Using default.");
                }
            } catch (ClassNotFoundException e) {
                LOG.error("Orienteer application class '" + applicationClass + "' was not found. Using default.");
            }
        }
        // bind non-singleton application
        bind(appClass);

        Provider<? extends OrienteerWebApplication> appProvider = binder().getProvider(appClass);
        if (!OrienteerWebApplication.class.equals(appClass)) {
            bind(OrienteerWebApplication.class).toProvider(appProvider);
        }
        bind(OrientDbWebApplication.class).toProvider(appProvider);
        bind(WebApplication.class).toProvider(appProvider);

        bind(Properties.class).annotatedWith(Orienteer.class).toInstance(properties);

        install(loadFromClasspath(new OrienteerModule()));
    }

    private void initResources() {
        loadProperties();
        bind(Path.class).annotatedWith(Names.named("jars"))
                .toInstance(paths.get("orienteer.loader.jar.folder"));
        bind(Path.class).annotatedWith(Names.named("pom.xml"))
                .toInstance(paths.get("orienteer.loader.pom.file"));
        bind(Path.class).annotatedWith(Names.named("depJars"))
                .toInstance(paths.get("orienteer.loader.dependency.jar.folder"));
        bind(Path.class).annotatedWith(Names.named("depPoms"))
                .toInstance(paths.get("orienteer.loader.dependency.pom.folder"));
//        bind(DependencyResolver.class).in(Singleton.class);
//        bind(ODependencyLoader.class).in(Singleton.class);

    }

    private void loadProperties() {
        try {
            loadJarsFolderPath(properties);
        } catch (IOException e) {
            LOG.error("Cannot load properties");
            if (LOG.isDebugEnabled()) e.printStackTrace();
//            try {
//                loadJarsFolderPath(propertiesDefault);
//            } catch (IOException ex) {
//                LOG.error("Cannot load user and default loader properties.");
//                ex.printStackTrace();
//            }
        }
    }

    private void loadJarsFolderPath(String propertiesPath) throws IOException {

        Properties properties = new Properties();
        Path path = Paths.get(propertiesPath);
        LOG.info("orienteer-module-loader properties path: " + path.toAbsolutePath());
        InputStream in = Files.newInputStream(path);
        properties.load(in);
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            paths.put(key, Paths.get(properties.getProperty(key)));
        }
    }


}
