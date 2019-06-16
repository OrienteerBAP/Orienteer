package org.orienteer.logger.server.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.IOLoggerEventDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

/**
 * Wrapper for filtered event dispatcher
 */
public class OLoggerEventFilteredDispatcherModel extends OLoggerEventDispatcherModel {

    public static final String CLASS_NAME = "OLoggerEventFilteredDispatcher";

    public static final String PROP_EXCEPTIONS = "exceptions";

    public OLoggerEventFilteredDispatcherModel() {
        super(CLASS_NAME);
    }

    public OLoggerEventFilteredDispatcherModel(String iClassName) {
        super(iClassName);
    }

    public OLoggerEventFilteredDispatcherModel(ODocument iDocument) {
        super(iDocument);
    }

    public Set<String> getExceptions() {
        Set<String> exceptions = document.field(PROP_EXCEPTIONS);
        return exceptions != null ? exceptions : Collections.emptySet();
    }

    public OLoggerEventFilteredDispatcherModel setExceptions(Set<String> exceptions) {
        document.field(PROP_EXCEPTIONS, exceptions);
        return this;
    }

    @Override
    public IOLoggerEventDispatcher createDispatcherClassInstance() {
        try {
            return (IOLoggerEventDispatcher) Class.forName(getDispatcherClass()).getConstructor(String.class)
                    .newInstance(getAlias());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Can't create dispatcher instance for class " + getDispatcherClass() +
                    " and argument: " + getAlias(), e);
        }

    }
}
