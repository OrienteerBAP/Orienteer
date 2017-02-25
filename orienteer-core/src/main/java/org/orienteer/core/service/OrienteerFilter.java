package org.orienteer.core.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import org.apache.wicket.protocol.http.WicketFilter;
import org.orienteer.core.service.loader.OLoaderStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy Gonchar
 */
@Singleton
public final class OrienteerFilter implements Filter {

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

    private static Injector getInjector() {
        return Guice.createInjector(new OrienteerInitModule());
    }

    public static boolean isReload() {
        return reload;
    }

    public static void reloadOrienteer() {
        reloadOrienteer(1);
    }

    public static void reloadOrienteer(long delay) {
        OrienteerFilter orienteerFilter = injector.getInstance(OrienteerFilter.class);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.schedule(new Reload(orienteerFilter), delay, TimeUnit.SECONDS);
    }

    private static class Reload implements Runnable {

        private final OrienteerFilter orienteerFilter;

        private Reload(OrienteerFilter orienteerFilter) {
            this.orienteerFilter = orienteerFilter;
        }

        @Override
        public void run() {
            LOG.info("Start reload Orienteer.");
            reload();
            LOG.info("End reload Orienteer.");
        }

        private void reload() {
            try {
                orienteerFilter.reload();
            } catch (ServletException e) {
                LOG.error("Cannot reload application");
                if (LOG.isDebugEnabled()) e.printStackTrace();
            }
        }
    }
}
