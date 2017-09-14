package org.orienteer.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import de.agilecoders.wicket.webjars.WicketWebjars;
import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.component.OModulesLoadFailedPanel;
import org.orienteer.core.service.OrienteerInitModule;
import org.orienteer.core.util.StartupPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    private static final String RELOAD_HTML = "org/orienteer/core/web/OrienteerReloadPage.html";

    private static OrienteerFilter instance;

    private Filter filter;

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
        Injector injector = Guice.createInjector(new OrienteerInitModule(properties));
        context.setAttribute(Injector.class.getName(), injector);
        initFilter(filterConfig);
    }

    private void initFilter(final FilterConfig filterConfig) throws ServletException {
        filter = new GuiceFilter();
        try {
            filter.init(filterConfig);
            if (OrienteerClassLoader.isUseUnTrusted())
                OrienteerClassLoader.clearDisabledModules();
            OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
            if (app != null) {
                OModulesLoadFailedPanel.clearInfoAboutUsers();
                app.setLoadInSafeMode(!OrienteerClassLoader.isUseUnTrusted() || OrienteerClassLoader.isUseOrienteerClassLoader());
                app.setLoadWithoutModules(OrienteerClassLoader.isUseOrienteerClassLoader());
            }
        } catch (Throwable t) {
            if (OrienteerClassLoader.isUseUnTrusted()) {
                LOG.warn("Can't run Orienteer with untrusted classloader. Orienteer runs with trusted classloader.", t);
                useTrustedClassLoader();
            } else {
                LOG.warn("Can't run Orienteer with trusted classloader. Orienteer runs with custom classloader.", t);
                useOrienteerClassLoader();
            }
            reloading = false;
            instance.reload(1000);
        }
    }
    
    private ClassLoader initClassLoader(Properties properties) {
        OrienteerClassLoader.initOrienteerClassLoaders(OrienteerFilter.class.getClassLoader());
        OrienteerClassLoader.enable();
    	return OrienteerClassLoader.getClassLoader();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (reloading) {
            HttpServletResponse res = (HttpServletResponse) response;
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL html = classLoader.getResource(RELOAD_HTML);
            if (html != null) {
                ServletOutputStream out = response.getOutputStream();
                InputStream in = html.openStream();
                int bytes;
                byte [] buff = new byte[4096];
                while ((bytes = in.read(buff)) != -1) {
                    out.write(buff, 0, bytes);
                }
            }
            res.setStatus(HTTP_CODE_SERVER_UNAVAILABLE);
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

    /**
     * Reload Orienteer with fixed delay=3s and wait=5s
     */
    public static void reloadOrienteer() {
        reloadOrienteer(3000, 5000);
    }

    /**
     * Reload Orienteer with delay and wait
     * @param delay - delay in ms. After delay Orienteer will be reload
     * @param wait - wait in ms. Wait before {@link OrienteerFilter} starts reload
     */
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
