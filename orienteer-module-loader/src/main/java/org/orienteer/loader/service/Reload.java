package org.orienteer.loader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.context.JclContext;

import javax.servlet.ServletException;

/**
 * @author Vitaliy Gonchar
 */
public class Reload implements Runnable {

    private final ReloadInfoFilter reloadInfoFilter;
    private final ReloadFilter reloadFilter;

    private static final Logger LOG = LoggerFactory.getLogger(Reload.class);

    public Reload(ReloadInfoFilter reloadInfoFilter, ReloadFilter reloadFilter) {
        this.reloadInfoFilter = reloadInfoFilter;
        this.reloadFilter = reloadFilter;
    }

    @Override
    public void run() {
        LOG.debug("Start reload Wicket application");
        timeout(3000);
        reloadInfoFilter.on();
        reload();
        reloadInfoFilter.off();
        LOG.debug("End reload Wicket application");
    }

    private void reload() {
        try {
            if (JclContext.isLoaded()) JclContext.destroy();
            reloadFilter.reload(reloadInfoFilter.getFilterConfig());
        } catch (ServletException e) {
            LOG.error("Cannot reload application");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    private void timeout(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            LOG.error("InterruptedException!");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

}
