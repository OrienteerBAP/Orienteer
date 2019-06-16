package org.orienteer.logger.server.service.dispatcher;

import org.orienteer.logger.server.model.OLoggerEventFilteredDispatcherModel;
import org.orienteer.logger.server.repository.OLoggerRepository;

/**
 * Filtered event dispatcher
 */
public class OLoggerEventFilteredDispatcher extends OLoggerEventDispatcher {

    private final String alias;

    public OLoggerEventFilteredDispatcher(String alias) {
        this.alias = alias;
    }

    @Override
    protected boolean needsToBeLogged(Throwable event) {
        OLoggerEventFilteredDispatcherModel dispatcher = OLoggerRepository.getOLoggerEventFilteredDispatcher(alias)
                .orElseThrow(() -> new IllegalStateException("There is no filtered dispatcher with alias: " + alias));

        return super.needsToBeLogged(event) && dispatcher.getExceptions().contains(event.getClass().getName());
    }
}
