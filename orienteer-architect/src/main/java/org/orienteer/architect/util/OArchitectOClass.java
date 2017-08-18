package org.orienteer.architect.util;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.http.util.Args;
import org.apache.wicket.util.io.IClusterable;

import java.util.ArrayList;
import java.util.Collection;
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
    private String pageUrl;
    private boolean existsInDb;

    public static OArchitectOClass toArchitectOClass(OClass oClass) {
        OArchitectOClass architectOClass = new OArchitectOClass(oClass.getName());
        architectOClass.setExistsInDb(true);
        architectOClass.setProperties(toOArchitectProperties(oClass.properties(), oClass.getSuperClasses()));
        architectOClass.setSuperClasses(toOArchitectClassNames(oClass.getSuperClasses()));
        architectOClass.setSubClasses(toOArchitectClassNames(oClass.getSubclasses()));
        architectOClass.setPageUrl("/class/" + oClass.getName());
        return architectOClass;
    }

    private static List<OArchitectOProperty> toOArchitectProperties(Collection<OProperty> properties, List<OClass> superClasses) {
        List<OArchitectOProperty> architectProperties = new ArrayList<>(properties.size());
        for (OProperty property : properties) {
            OArchitectOProperty architectOProperty = OArchitectOProperty.toArchitectOProperty(property);
            architectOProperty.setSubClassProperty(isSubClassProperty(property, superClasses));
            architectProperties.add(architectOProperty);
        }
        return architectProperties;
    }

    private static boolean isSubClassProperty(OProperty property, List<OClass> superClasses) {
        boolean isSubClass = false;
        for (OClass oClass : superClasses) {
            isSubClass = oClass.getProperty(property.getName()) != null;
            if (!isSubClass) {
                List<OClass> classes = oClass.getSuperClasses();
                if (classes != null && !classes.isEmpty()) {
                    isSubClass = isSubClassProperty(property, classes);
                }
            } else break;
        }
        return isSubClass;
    }

    private static List<String> toOArchitectClassNames(Collection<OClass> classes) {
        List<String> architectSuperClasses = new ArrayList<>(classes.size());
        for (OClass oClass : classes) {
            architectSuperClasses.add(oClass.getName());
        }
        return architectSuperClasses;
    }


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

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
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

    public String getPageUrl() {
        return  pageUrl;
    }

    @Override
    public String toString() {
        return getName();
    }
}
