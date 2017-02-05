package org.orienteer.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

/**
 * @author Vitaliy Gonchar
 */
public class Reload extends Thread {

    private final OrienteerFilter orienteerFilter;

    private static final Logger LOG = LoggerFactory.getLogger(Reload.class);

    public Reload(OrienteerFilter orienteerFilter) {
        this.orienteerFilter = orienteerFilter;
    }

    @Override
    public void start() {
        LOG.info("Start reload Orienteer.");
        timeout(2000);
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

    private void timeout(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            LOG.error("InterruptedException!");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

}
