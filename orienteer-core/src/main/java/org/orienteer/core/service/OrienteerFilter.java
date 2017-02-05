package org.orienteer.core.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import org.apache.wicket.protocol.http.WicketFilter;
import org.orienteer.core.loader.OLoaderStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public class OrienteerFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerFilter.class);

    private static volatile WeakReference<ServletContext> servletContext = new WeakReference<>(null);
    private static WicketFilter filter;
    private static Injector injector;
    private static boolean reload;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        OLoaderStorage.createNewRootLoader();
        LOG.info("Start initialization: " + this.getClass().getName());
        ServletContext context = filterConfig.getServletContext();
        injector = getInjector();
        context.setAttribute(Injector.class.getName(), injector);
        servletContext = new WeakReference<>(context);
        injector.getInstance(OrienteerFilterInitModule.FilterConfigProvider.class)
                .setServletContext(filterConfig.getServletContext())
                .setFilterName(filterConfig.getFilterName());
        filter = injector.getInstance(WicketFilter.class);
        filter.init(injector.getInstance(FilterConfig.class));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (reload) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(503);
            LOG.info("Reload application. Send 503 code");
        } else if (filter != null) {
            filter.doFilter(request, response, chain);
        } else chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.info("Destroy filter - " + this.getClass().getName());
        servletContext.clear();
        GuiceFilter guiceFilter = injector.getInstance(GuiceFilter.class);
        if (guiceFilter != null) guiceFilter.destroy();
        filter.destroy();
        filter = null;
    }

    public void reload(FilterConfig filterConfig) throws ServletException {
        LOG.info("Start reload filter with filter config: " + filterConfig);
        reload = true;
        destroy();
        init(filterConfig);
        reload = false;
    }

    public void reload() throws ServletException {
        if (filter == null) return ;
        reload(filter.getFilterConfig());
    }

    protected static Injector getInjector() {
//        return  injector != null ? injector : Guice.createInjector(new ReloadOrienteerInitModule());
        return Guice.createInjector(new OrienteerInitModule());
    }

    public static boolean isReload() {
        return reload;
    }
}
