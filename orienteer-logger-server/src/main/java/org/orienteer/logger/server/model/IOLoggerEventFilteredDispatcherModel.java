package org.orienteer.logger.server.model;

import java.util.Set;

import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;

/**
 * Wrapper for filtered event dispatcher
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOLoggerEventFilteredDispatcherModel.CLASS_NAME)
public interface IOLoggerEventFilteredDispatcherModel extends IOLoggerEventDispatcherModel {

    public static final String CLASS_NAME = "OLoggerEventFilteredDispatcher";
    
    public Set<String> getExceptions();
	public IOLoggerEventFilteredDispatcherModel setExceptions(Set<String> value);
}
