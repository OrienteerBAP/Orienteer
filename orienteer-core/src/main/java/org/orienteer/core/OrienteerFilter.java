package org.orienteer.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import de.agilecoders.wicket.webjars.WicketWebjars;
import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.service.OrienteerInitModule;
import org.orienteer.core.util.StartupPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main Orienteer Filter to handle all requests.
 * It allows dynamically reload Orienteer application themselves and provide different class loading context
 */
@Singleton
public final class OrienteerFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerFilter.class);

    private static final int HTTP_CODE_SERVER_UNAVAILABLE = 503;

    private static final String RELOAD_HTML = "org/orienteer/core/web/ReloadOrienteerPage.html";

    private static OrienteerFilter instance;
    private static boolean useUnTrusted = true;

    private Filter filter;
    private Injector injector;
    
    private FilterConfig filterConfig;
    private ClassLoader classLoader;
    private boolean reloading;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    	instance = this;
    	this.filterConfig = filterConfig;
    	Properties properties = StartupPropertiesLoader.retrieveProperties();
    	classLoader = initClassLoader(properties);
    	//TODO: Implement classloading here
    	Thread.currentThread().setContextClassLoader(classLoader);
        LOG.info("Start initialization: " + this.getClass().getName());
        ServletContext context = filterConfig.getServletContext();
        injector = Guice.createInjector(new OrienteerInitModule(properties));
        context.setAttribute(Injector.class.getName(), injector);
        initFilter(filterConfig);
    }

    private void initFilter(final FilterConfig filterConfig) throws ServletException {
        filter = new GuiceFilter();
        try {
            filter.init(filterConfig);
        } catch (Throwable t) {
            if (useUnTrusted) {
                LOG.warn("Cannot run Orienteer with untrusted classloader. Orienteer runs with trusted classloader.", t);
                useTrustedClassLoader();
                useUnTrusted = false;
            } else {
                LOG.warn("Cannot run Orienteer with trusted classloader. Orienteer runs with custom classloader.", t);
                useOrienteerClassLoader();
            }
            instance.reload(1000);
        }
    }
    
    private ClassLoader initClassLoader(Properties properties) {
        OrienteerClassLoader.create(OrienteerFilter.class.getClassLoader());
        OrienteerClassLoader.on();
    	return OrienteerClassLoader.getClassLoader();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (reloading) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(HTTP_CODE_SERVER_UNAVAILABLE);
            request.getRequestDispatcher(RELOAD_HTML).forward(request, response);
        } else {
            Thread.currentThread().setContextClassLoader(classLoader);
            if (filter != null) {
                filter.doFilter(request, response, chain);
            } else chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        LOG.info("Destroy doOrienteerFilter - " + this.getClass().getName());
        filter.destroy();
        filter = null;
    }

    public void reload(long wait) throws ServletException {
    	if(!reloading) {
	        LOG.info("Start reload doOrienteerFilter with doOrienteerFilter config: " + filterConfig);
	        reloading = true;
	        destroy();
	        try {
				Thread.currentThread().sleep(wait);
			} catch (InterruptedException e) {
				/*NOP*/
			}
            OrienteerClassLoaderUtil.reindex();
	        init(filterConfig);
	        WicketWebjars.reindex(OrienteerWebApplication.lookupApplication());
	        reloading = false;
    	}
    }

    private void useTrustedClassLoader() {
        OrienteerClassLoader.useTrustedClassLoader();
        classLoader = OrienteerClassLoader.getClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    private void useOrienteerClassLoader() {
        OrienteerClassLoader.useOrienteerClassLoader();
        classLoader = OrienteerClassLoader.getClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    public boolean isReloading() {
        return reloading;
    }

    public static void reloadOrienteer() {
        reloadOrienteer(3000, 5000);
    }

    public static void reloadOrienteer(long delay, final long wait) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.schedule(new Runnable() {
			
			@Override
			public void run() {
				try {
                    instance.reload(wait);
				} catch (ServletException e) {
					LOG.error("Can't reload Orienteer", e); 
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
    }
}
