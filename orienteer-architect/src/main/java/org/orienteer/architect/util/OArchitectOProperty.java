package org.orienteer.architect.util;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.http.util.Args;
import org.apache.wicket.util.io.IClusterable;

/**
 * Utility class which represents {@link com.orientechnologies.orient.core.metadata.schema.OProperty} from JSON string
 */
public class OArchitectOProperty implements IClusterable {

    private String name;
    private OType type;
    private boolean subClassProperty;
    private String linkedClassName;

    public OArchitectOProperty(String name, OType type) {
        this(name, type, false, null);
    }

    public OArchitectOProperty(String name, OType type, boolean subClassProperty) {
        this(name, type, subClassProperty, null);
    }

    public OArchitectOProperty(String name, OType type, boolean subClassProperty, String linkedClassName) {
        Args.notEmpty(name, "name");
        this.name = name;
        this.type = type;
        this.subClassProperty = subClassProperty;
        this.linkedClassName = linkedClassName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OType getType() {
        return type;
    }

    public void setType(OType type) {
        this.type = type;
    }

    public boolean isSubClassProperty() {
        return subClassProperty;
    }

    public void setSubClassProperty(boolean subClassProperty) {
        this.subClassProperty = subClassProperty;
    }

    public String getLinkedClassName() {
        return linkedClassName;
    }

    public void setLinkedClassName(String linkedClassName) {
        this.linkedClassName = linkedClassName;
    }

}
