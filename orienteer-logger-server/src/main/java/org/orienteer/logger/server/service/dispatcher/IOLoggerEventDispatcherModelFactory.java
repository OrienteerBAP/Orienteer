package org.orienteer.logger.server.service.dispatcher;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.server.model.IOLoggerEventDispatcherModel;

/**
 * Factory interface for create {@link IOLoggerEventDispatcherModel}
 */
@ImplementedBy(OLoggerEventDispatcherModelFactory.class)
public interface IOLoggerEventDispatcherModelFactory {

    IOLoggerEventDispatcherModel createEventDispatcherModel(ODocument document);
}
