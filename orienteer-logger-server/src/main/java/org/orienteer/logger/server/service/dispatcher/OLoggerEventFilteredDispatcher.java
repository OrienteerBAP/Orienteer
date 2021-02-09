package org.orienteer.logger.server.service.dispatcher;

import org.orienteer.logger.server.model.IOLoggerDAO;
import org.orienteer.logger.server.model.IOLoggerEventFilteredDispatcherModel;

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
        IOLoggerEventFilteredDispatcherModel dispatcher = IOLoggerDAO.INSTANCE.getOLoggerEventFilteredDispatcher(alias);
        if(dispatcher==null) new IllegalStateException("There is no filtered dispatcher with alias: " + alias);

        return super.needsToBeLogged(event) && dispatcher.getExceptions().contains(event.getClass().getName());
    }

    public String getAlias() {
        return alias;
    }
}
