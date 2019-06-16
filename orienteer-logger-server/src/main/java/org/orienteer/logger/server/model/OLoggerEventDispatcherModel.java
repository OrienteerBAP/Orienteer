package org.orienteer.logger.server.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.logger.IOLoggerEventDispatcher;

import java.util.Collections;
import java.util.Map;

/**
 * Wrapper for event dispatcher
 */
public class OLoggerEventDispatcherModel extends ODocumentWrapper {

    public static final String CLASS_NAME = "OLoggerEventDispatcher";

    public static final String PROP_NAME             = "name";
    public static final String PROP_ALIAS            = "alias";
    public static final String PROP_DISPATCHER_CLASS = "dispatcherClass";

    public OLoggerEventDispatcherModel() {
        super();
    }

    public OLoggerEventDispatcherModel(String iClassName) {
        super(iClassName);
    }

    public OLoggerEventDispatcherModel(ODocument iDocument) {
        super(iDocument);
    }

    public Map<String, String> getName() {
        Map<String, String> name = document.field(PROP_NAME);
        return name != null ? name : Collections.emptyMap();
    }

    public OLoggerEventDispatcherModel setName(Map<String, String> name) {
        document.field(PROP_NAME, name);
        return this;
    }

    public String getAlias() {
        return document.field(PROP_ALIAS);
    }

    public OLoggerEventDispatcherModel setAlias(String alias) {
        document.field(PROP_ALIAS, alias);
        return this;
    }

    public String getDispatcherClass() {
        return document.field(PROP_DISPATCHER_CLASS);
    }

    public OLoggerEventDispatcherModel setDispatcherClass(String dispatcherClass) {
        document.field(PROP_DISPATCHER_CLASS, dispatcherClass);
        return this;
    }

    public IOLoggerEventDispatcher createDispatcherClassInstance() {
        try {
            return (IOLoggerEventDispatcher) Class.forName(getDispatcherClass()).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Can't create dispatcher instance for: " + getDispatcherClass(), e);
        }
    }
}
