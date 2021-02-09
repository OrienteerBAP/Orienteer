package org.orienteer.logger.server.service.dispatcher;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.orienteer.core.dao.DAO;
import org.orienteer.logger.server.model.IOLoggerEventDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventFilteredDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventMailDispatcherModel;

/**
 * Default implementation of {@link IOLoggerEventDispatcherModelFactory}
 */
public class OLoggerEventDispatcherModelFactory implements IOLoggerEventDispatcherModelFactory {
    @Override
    public IOLoggerEventDispatcherModel createEventDispatcherModel(ODocument document) {
        OClass schemaClass = document.getSchemaClass();
        if (schemaClass != null) {
            switch (schemaClass.getName()) {
                case IOLoggerEventDispatcherModel.CLASS_NAME:
                    return DAO.provide(IOLoggerEventDispatcherModel.class, document);
                case IOLoggerEventFilteredDispatcherModel.CLASS_NAME:
                    return DAO.provide(IOLoggerEventFilteredDispatcherModel.class, document);
                case IOLoggerEventMailDispatcherModel.CLASS_NAME:
                    return DAO.provide(IOLoggerEventMailDispatcherModel.class, document);
            }
        }
        return null;
    }
}
