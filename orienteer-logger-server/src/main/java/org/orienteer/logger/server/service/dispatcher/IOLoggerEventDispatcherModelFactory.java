package org.orienteer.logger.server.service.dispatcher;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.server.model.OLoggerEventDispatcherModel;

/**
 * Factory interface for create {@link OLoggerEventDispatcherModel}
 */
@ImplementedBy(OLoggerEventDispatcherModelFactory.class)
public interface IOLoggerEventDispatcherModelFactory {

    OLoggerEventDispatcherModel createEventDispatcherModel(ODocument document);
}
