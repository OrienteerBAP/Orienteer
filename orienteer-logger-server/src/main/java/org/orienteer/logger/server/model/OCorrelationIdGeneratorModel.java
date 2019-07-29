package org.orienteer.logger.server.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.logger.IOCorrelationIdGenerator;

import java.util.Collections;
import java.util.Map;

/**
 * Wrapper for correlation id generator
 */
public class OCorrelationIdGeneratorModel extends ODocumentWrapper {

    public static final String CLASS_NAME = "OCorrelationIdGenerator";

    public static final String PROP_NAME  = "name";
    public static final String PROP_ALIAS = "alias";
    public static final String PROP_GENERATOR_CLASS = "class";

    public OCorrelationIdGeneratorModel() {
        this(CLASS_NAME);
    }

    public OCorrelationIdGeneratorModel(String iClassName) {
        super(iClassName);
    }

    public OCorrelationIdGeneratorModel(ODocument iDocument) {
        super(iDocument);
    }

    public Map<String, String> getName() {
        Map<String, String> name = document.field(PROP_NAME);
        return name != null ? name : Collections.emptyMap();
    }

    public OCorrelationIdGeneratorModel setName(Map<String, String> name) {
        document.field(PROP_NAME, name);
        return this;
    }

    public String getAlias() {
        return document.field(PROP_ALIAS);
    }

    public OCorrelationIdGeneratorModel setAlias(String alias) {
        document.field(PROP_ALIAS, alias);
        return this;
    }

    public String getCorrelationClassName() {
        return document.field(PROP_GENERATOR_CLASS);
    }

    @SuppressWarnings("unchecked")
    public <T extends IOCorrelationIdGenerator> Class<T> getCorrelationClass() {
        try {
            return (Class<T>) Class.forName(getCorrelationClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends IOCorrelationIdGenerator> T createCorrelationIdGenerator() {
        Class<IOCorrelationIdGenerator> clazz = getCorrelationClass();
        if (clazz != null) {
            try {
                return (T) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public OCorrelationIdGeneratorModel setCorrelationClass(Class<? extends IOCorrelationIdGenerator> clazz) {
        return setCorrelationClassName(clazz != null ? clazz.getName() : null);
    }

    public OCorrelationIdGeneratorModel setCorrelationClassName(String className) {
        document.field(PROP_GENERATOR_CLASS, className);
        return this;
    }
}
