package org.orienteer.loader.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.orienteer.loader.loader.JarModules;
import org.orienteer.loader.loader.ODependencyLoader;
import org.orienteer.loader.loader.jar.DependencyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.proxy.CglibProxyProvider;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;

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
public class DependencyLoaderInitModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyLoaderInitModule.class);

    public final Map<String, Path> paths = new HashMap<>();

    private final String properties         = "loader.properties";
    private final String propertiesDefault  = "src/main/resources/loader-default.properties";

    @Override
    protected void configure() {
        loadProperties();
        bind(Path.class).annotatedWith(Names.named("jars"))
                .toInstance(paths.get("loader.jar.folder"));
        bind(Path.class).annotatedWith(Names.named("pom.xml"))
                .toInstance(paths.get("loader.pom.file"));
        bind(Path.class).annotatedWith(Names.named("depJars"))
                .toInstance(paths.get("loader.dependency.jar.folder"));
        bind(Path.class).annotatedWith(Names.named("depPoms"))
                .toInstance(paths.get("loader.dependency.pom.folder"));
        bind(DependencyResolver.class).in(Singleton.class);
        ProxyProviderFactory.setDefaultProxyProvider(new CglibProxyProvider());
        bind(JarClassLoader.class).toProvider(new Provider<JarClassLoader>() {
            @Override
            public JarClassLoader get() {
                return new JarClassLoader();
            }
        }).in(Singleton.class);
        bind(JclObjectFactory.class).toProvider(new Provider<JclObjectFactory>() {
            @Override
            public JclObjectFactory get() {
                return JclObjectFactory.getInstance(true);
            }
        }).in(Singleton.class);
        bind(ODependencyLoader.class).in(Singleton.class);
        requestStaticInjection(JarModules.class);
    }

    private void loadProperties() {
        try {
            loadJarsFolderPath(properties);
        } catch (IOException e) {
            try {
                loadJarsFolderPath(propertiesDefault);
            } catch (IOException ex) {
                LOG.error("Cannot load user and default loader properties.");
                ex.printStackTrace();
            }
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
