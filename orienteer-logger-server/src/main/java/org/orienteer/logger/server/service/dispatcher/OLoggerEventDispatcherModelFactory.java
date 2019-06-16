package org.orienteer.logger.server.service.dispatcher;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.server.model.OLoggerEventDispatcherModel;
import org.orienteer.logger.server.model.OLoggerEventFilteredDispatcherModel;
import org.orienteer.logger.server.model.OLoggerEventMailDispatcherModel;

/**
 * Default implementation of {@link IOLoggerEventDispatcherModelFactory}
 */
public class OLoggerEventDispatcherModelFactory implements IOLoggerEventDispatcherModelFactory {
    @Override
    public OLoggerEventDispatcherModel createEventDispatcherModel(ODocument document) {
        OClass schemaClass = document.getSchemaClass();
        if (schemaClass != null) {
            switch (schemaClass.getName()) {
                case OLoggerEventDispatcherModel.CLASS_NAME:
                    return new OLoggerEventDispatcherModel(document);
                case OLoggerEventFilteredDispatcherModel.CLASS_NAME:
                    return new OLoggerEventFilteredDispatcherModel(document);
                case OLoggerEventMailDispatcherModel.CLASS_NAME:
                    return new OLoggerEventMailDispatcherModel(document);
            }
        }
        return null;
    }
}
