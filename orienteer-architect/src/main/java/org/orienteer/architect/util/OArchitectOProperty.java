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

    public OArchitectOProperty(String name, OType type) {
        Args.notEmpty(name, "name");
        this.name = name;
        this.type = type;
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

    @Override
    public String toString() {
        return "OArchitectOProperty{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
