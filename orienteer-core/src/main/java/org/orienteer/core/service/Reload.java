package org.orienteer.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

/**
 * @author Vitaliy Gonchar
 */
public class Reload implements Runnable {

    private final OrienteerFilter orienteerFilter;

    private static final Logger LOG = LoggerFactory.getLogger(Reload.class);

    public Reload(OrienteerFilter orienteerFilter) {
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
