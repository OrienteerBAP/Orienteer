package org.orienteer.architect.util;

import org.apache.http.util.Args;
import org.apache.wicket.util.io.IClusterable;

import java.util.List;

/**
 * Utility class which represents {@link com.orientechnologies.orient.core.metadata.schema.OClass} from JSON
 */
public class OArchitectOClass implements IClusterable {
    private String name;
    private List<String> superClasses;
    private List<String> subClasses;
    private List<OArchitectOProperty> properties;
    private List<OArchitectOProperty> propertiesForDelete;
    private boolean existsInDb;

    public OArchitectOClass(String name) {
        this(name, null, null);
    }

    public OArchitectOClass(String name, List<String> superClasses) {
        this(name, superClasses, null);
    }

    public OArchitectOClass(String name, List<String> superClasses, List<OArchitectOProperty> properties) {
        Args.notEmpty(name, "name");
        this.name = name;
        this.superClasses = superClasses;
        this.properties = properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
    }

    public void setSubClasses(List<String> subClasses) {
        this.subClasses = subClasses;
    }

    public void setProperties(List<OArchitectOProperty> properties) {
        this.properties = properties;
    }

    public void setPropertiesForDelete(List<OArchitectOProperty> propertiesForDelete) {
        this.propertiesForDelete = propertiesForDelete;
    }

    public void setExistsInDb(boolean exists) {
        this.existsInDb = exists;
    }

    public String getName() {
        return name;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public List<String> getSubClasses() {
        return subClasses;
    }

    public List<OArchitectOProperty> getProperties() {
        return properties;
    }

    public List<OArchitectOProperty> getPropertiesForDelete() {
        return propertiesForDelete;
    }

    public boolean isExistsInDb() {
        return this.existsInDb;
    }

}
