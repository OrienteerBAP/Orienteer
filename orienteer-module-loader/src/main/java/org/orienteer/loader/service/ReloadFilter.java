package org.orienteer.loader.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.apache.wicket.guice.GuiceWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.orienteer.core.service.OrienteerInitModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public class ReloadFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ReloadFilter.class);

    private static volatile WeakReference<ServletContext> servletContext = new WeakReference<>(null);
    private static WicketFilter filter;
    private static Injector injector;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        LOG.debug("Init filter - " + this.getClass().getName());
        ServletContext context = filterConfig.getServletContext();
        injector = getInjector();
        context.setAttribute(Injector.class.getName(), injector);
        servletContext = new WeakReference<>(context);
        filter = injector.getInstance(ReloadInfoFilter.class);
        final Map<String, String> initParams = new HashMap<>();
        initParams.put(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        initParams.put("applicationFactoryClassName", GuiceWebApplicationFactory.class.getName());
        initParams.put("injectorContextAttribute", Injector.class.getName());
        filter.init(new FilterConfig() {
            @Override
            public String getFilterName() {
                return filterConfig.getFilterName();
            }

            @Override
            public ServletContext getServletContext() {
                return filterConfig.getServletContext();
            }

            @Override
            public String getInitParameter(String name) {
                return initParams.get(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.enumeration(initParams.keySet());
            }
        });
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (filter != null) {
            filter.doFilter(request, response, chain);
        } else chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.debug("Destroy filter - " + this.getClass().getName());
        servletContext.clear();
        filter.destroy();
        filter = null;
    }

    public void reload(FilterConfig filterConfig) throws ServletException {
        LOG.debug("Start reload filter");
        destroy();
        init(filterConfig);
    }

    protected Injector getInjector() {
        return Guice.createInjector(new DefaultInitReloadModule(), new OrienteerInitModule());
    }
}
