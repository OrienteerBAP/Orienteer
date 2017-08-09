package org.orienteer.architect.util;

import org.apache.http.util.Args;
import org.apache.wicket.util.io.IClusterable;

import java.util.List;

/**
 * Utility class which represents {@link com.orientechnologies.orient.core.metadata.schema.OClass} from JSON
 */
public class OArchitectOClass implements IClusterable {
    private String name;
    private List<String> superClassesNames;
    private List<OArchitectOProperty> properties;
    private List<OArchitectOProperty> propertiesForDelete;

    public OArchitectOClass(String name) {
        this(name, null, null);
    }

    public OArchitectOClass(String name, List<String> superClassesNames) {
        this(name, superClassesNames, null);
    }

    public OArchitectOClass(String name, List<String> superClassesNames, List<OArchitectOProperty> properties) {
        Args.notEmpty(name, "name");
        this.name = name;
        this.superClassesNames = superClassesNames;
        this.properties = properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuperClassesNames(List<String> superClassesNames) {
        this.superClassesNames = superClassesNames;
    }

    public void setProperties(List<OArchitectOProperty> properties) {
        this.properties = properties;
    }

    public void setPropertiesForDelete(List<OArchitectOProperty> propertiesForDelete) {
        this.propertiesForDelete = propertiesForDelete;
    }

    public String getName() {
        return name;
    }

    public List<String> getSuperClassesNames() {
        return superClassesNames;
    }

    public List<OArchitectOProperty> getProperties() {
        return properties;
    }

    public List<OArchitectOProperty> getPropertiesForDelete() {
        return propertiesForDelete;
    }

}
