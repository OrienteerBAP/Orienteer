package org.orienteer.loader.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.rest.InterceptContentFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 */
public class ReloadOrienteerInitModule extends OrienteerInitModule {

    private static final Logger LOG = LoggerFactory.getLogger(ReloadOrienteerInitModule.class);

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

        // filter init
        bind(WicketFilter.class).in(Singleton.class);
        filter("/*").through(ReloadFilter.class);
        bind(ReloadFilter.class).in(Singleton.class);
        filter("/*").through(WicketFilter.class);
        bind(FilterConfigProvider.class).in(Singleton.class);
        bind(FilterConfig.class).toProvider(FilterConfigProvider.class);

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
