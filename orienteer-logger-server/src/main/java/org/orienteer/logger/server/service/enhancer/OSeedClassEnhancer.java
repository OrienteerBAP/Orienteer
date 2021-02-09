package org.orienteer.logger.server.service.enhancer;

import org.orienteer.logger.IOLoggerEventEnhancer;
import org.orienteer.logger.OLoggerEvent;
import org.orienteer.logger.server.model.IOLoggerEventModel;

/**
 * Add seed class to event metadata
 */
public class OSeedClassEnhancer implements IOLoggerEventEnhancer {

    @Override
    public OLoggerEvent enhance(OLoggerEvent event) {
        Object seed = event.getSeed();
        if (seed != null) {
            event.setMetaData(IOLoggerEventModel.PROP_SEEDCLASS, seed.getClass().getName());
        }
        return event;
    }

}
