package org.orienteer.logger.server.service;

import java.util.Set;

/**
 * Filtered event dispatcher
 */
public class OLoggerEventFilteredDispatcher extends OLoggerEventDispatcher {

    private final Set<String> exceptions;

    public OLoggerEventFilteredDispatcher(Set<String> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    protected boolean needsToBeLogged(Throwable event) {
        return super.needsToBeLogged(event) && exceptions.contains(event.getClass().getName());
    }
}
