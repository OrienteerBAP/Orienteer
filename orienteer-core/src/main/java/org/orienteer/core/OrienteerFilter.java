package org.orienteer.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import org.apache.wicket.protocol.http.WicketFilter;
import org.orienteer.core.service.OrienteerFilterInitModule;
import org.orienteer.core.service.OrienteerInitModule;
import org.orienteer.core.service.OrienteerFilterInitModule.FilterConfigProvider;
import org.orienteer.core.service.loader.OClassLoaderStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main Orienteer Filter to handle all requests.
 * It allows dynamically reload Orienteer application themselves and provide different class loading context
 */
@Singleton
public final class OrienteerFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerFilter.class);

    private static volatile WeakReference<ServletContext> servletContext = new WeakReference<>(null);
    private static WicketFilter filter;
    private static Injector injector;
    private ClassLoader classLoader;
    private boolean reloading;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    	ClassLoader classLoader = null;
    	//TODO: Implement classloading here
    	Thread.currentThread().setContextClassLoader(classLoader);
        LOG.info("Start initialization: " + this.getClass().getName());
        ServletContext context = filterConfig.getServletContext();
        injector = Guice.createInjector(new OrienteerInitModule());
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
        if (reloading) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(503);
            LOG.info("Reload application. Send 503 code");
        } else {
        	Thread.currentThread().setContextClassLoader(classLoader);
        	if (filter != null) {
        		filter.doFilter(request, response, chain);
        	} else chain.doFilter(request, response);
        }
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

    public void reload(FilterConfig filterConfig, long wait) throws ServletException {
    	if(!reloading) {
    		
	        LOG.info("Start reload filter with filter config: " + filterConfig);
	        reloading = true;
	        destroy();
	        try {
				Thread.currentThread().sleep(wait);
			} catch (InterruptedException e) {
				/*NOP*/
			}
	        init(filterConfig);
	        reloading = false;
    	}
    }

    public void reload(long wait) throws ServletException {
        if (filter == null) return ;
        reload(filter.getFilterConfig(), wait);
    }

    public boolean isReloading() {
        return reloading;
    }

    public static void reloadOrienteer() {
        reloadOrienteer(3000, 5000);
    }

    public static void reloadOrienteer(long delay, long wait) {
        OrienteerFilter orienteerFilter = injector.getInstance(OrienteerFilter.class);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.schedule(new Reload(orienteerFilter), delay, TimeUnit.MILLISECONDS);
    }

    private static class Reload implements Runnable {

        private final OrienteerFilter orienteerFilter;
        private long wait = 0;

        private Reload(OrienteerFilter orienteerFilter) {
            this.orienteerFilter = orienteerFilter;
        }

        @Override
        public void run() {
            LOG.info("Start reload Orienteer.");
            try {
            	orienteerFilter.reload(wait);
            } catch (ServletException e) {
            	LOG.error("Cannot reload application");
            	if (LOG.isDebugEnabled()) e.printStackTrace();
            }
            LOG.info("End reload Orienteer.");
        }
    }
}
