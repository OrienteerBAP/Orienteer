package org.orienteer.loader.service;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.apache.wicket.guice.GuiceWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.Orienteer;
import org.orienteer.core.service.OrienteerInitModule;
import org.orienteer.core.service.OrienteerModule;
import org.orienteer.loader.loader.JarModules;
import org.orienteer.loader.loader.ODependencyLoader;
import org.orienteer.loader.loader.jar.DependencyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.proxy.CglibProxyProvider;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.rest.InterceptContentFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 */
public class ReloadOrienteerInitModule extends OrienteerInitModule {

    private static final Logger LOG = LoggerFactory.getLogger(ReloadOrienteerInitModule.class);
    private final Map<String, Path> paths = new HashMap<>();
    private final String properties         = "loader.properties";
    private final String propertiesDefault  = "src/main/resources/loader-default.properties";

    private static final Map<String, String> INIT_PARAMS = new HashMap<>();
    static {
        INIT_PARAMS.put(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        INIT_PARAMS.put("applicationFactoryClassName", GuiceWebApplicationFactory.class.getName());
        INIT_PARAMS.put("injectorContextAttribute", Injector.class.getName());
    }

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
        requestStaticInjection(JarModules.class);
        ProxyProviderFactory.setDefaultProxyProvider(new CglibProxyProvider());
        bind(ClassLoader.class).to(JarClassLoader.class);
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
    }

    private void initFilter() {
        bind(WicketFilterProvider.class).in(Singleton.class);
        bind(WicketFilter.class).toProvider(WicketFilterProvider.class).in(Singleton.class);
        bind(ReloadFilter.class).in(Singleton.class);
        filter("/*").through(ReloadFilter.class);
        filter("/*").through(WicketFilter.class);
        bind(FilterConfigProvider.class).in(Singleton.class);
        bind(FilterConfig.class).toProvider(FilterConfigProvider.class);
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
                .toInstance(paths.get("loader.jar.folder"));
        bind(Path.class).annotatedWith(Names.named("pom.xml"))
                .toInstance(paths.get("loader.pom.file"));
        bind(Path.class).annotatedWith(Names.named("depJars"))
                .toInstance(paths.get("loader.dependency.jar.folder"));
        bind(Path.class).annotatedWith(Names.named("depPoms"))
                .toInstance(paths.get("loader.dependency.pom.folder"));
        bind(DependencyResolver.class).in(Singleton.class);
        bind(ODependencyLoader.class).in(Singleton.class);

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

    /**
     * Provide WicketFilter
     */
    @Singleton
    private static class WicketFilterProvider implements Provider<WicketFilter> {

        @Inject
        private JarClassLoader jcl;

        @Override
        public WicketFilter get() {
            return new WicketFilter() {
                @Override
                protected ClassLoader getClassLoader() {
                    return jcl;
                }
            };
        }
    }

    /**
     * Provide FilterConfig
     */
    @Singleton
    public static class FilterConfigProvider implements Provider<FilterConfig> {
        private String filterName;
        private ServletContext servletContext;

        public FilterConfigProvider setFilterName(String filterName) {
            this.filterName = filterName;
            return this;
        }

        public FilterConfigProvider setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
            return this;
        }

        @Override
        public FilterConfig get() {
            return new FilterConfig() {
                @Override
                public String getFilterName() {
                    return filterName;
                }

                @Override
                public ServletContext getServletContext() {
                    return servletContext;
                }

                @Override
                public String getInitParameter(String name) {
                    return INIT_PARAMS.get(name);
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return Collections.enumeration(INIT_PARAMS.keySet());
                }
            };
        }
    }

}
