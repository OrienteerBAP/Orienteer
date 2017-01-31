package org.orienteer.loader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

/**
 * @author Vitaliy Gonchar
 */
public class Reload implements Runnable {

    private final ReloadFilter reloadFilter;

    private static final Logger LOG = LoggerFactory.getLogger(Reload.class);

    public Reload(ReloadFilter reloadFilter) {
        this.reloadFilter = reloadFilter;
    }

    @Override
    public void run() {
        LOG.debug("Start reload Wicket application");
        timeout(3000);
        reload();
        LOG.debug("End reload Wicket application");
    }

    private void reload() {
        try {
            reloadFilter.reload();
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
