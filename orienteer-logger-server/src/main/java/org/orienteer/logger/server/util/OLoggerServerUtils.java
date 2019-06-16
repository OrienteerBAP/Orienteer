package org.orienteer.logger.server.util;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.logger.server.service.dispatcher.IOLoggerEventDispatcherModelFactory;

/**
 * Util class
 */
public final class OLoggerServerUtils {

    private OLoggerServerUtils() {}

    public static IOLoggerEventDispatcherModelFactory getEventDispatcherModelFactory() {
        return OrienteerWebApplication.lookupApplication().getServiceInstance(IOLoggerEventDispatcherModelFactory.class);
    }
}
