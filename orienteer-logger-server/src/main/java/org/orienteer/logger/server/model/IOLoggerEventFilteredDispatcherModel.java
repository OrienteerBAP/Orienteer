package org.orienteer.logger.server.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.logger.IOLoggerEventDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

/**
 * Wrapper for filtered event dispatcher
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOLoggerEventFilteredDispatcherModel.CLASS_NAME)
public interface IOLoggerEventFilteredDispatcherModel extends IOLoggerEventDispatcherModel {

    public static final String CLASS_NAME = "OLoggerEventFilteredDispatcher";
    
    public Set<String> getExceptions();
	public IOLoggerEventFilteredDispatcherModel setExceptions(Set<String> value);
}
