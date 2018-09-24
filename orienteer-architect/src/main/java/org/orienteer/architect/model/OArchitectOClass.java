package org.orienteer.architect.model;

import org.apache.http.util.Args;
import org.apache.wicket.util.io.IClusterable;

import java.util.Collections;
import java.util.List;

/**
 * Utility class which represents {@link com.orientechnologies.orient.core.metadata.schema.OClass} from JSON
 */
public class OArchitectOClass implements IClusterable {
    private String name;
    private List<String> superClasses;
    private List<String> subClasses;
    private List<OArchitectOProperty> properties;
    private String pageUrl;
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
        return superClasses != null ? superClasses : Collections.emptyList();
    }

    public List<String> getSubClasses() {
        return subClasses;
    }

    public List<OArchitectOProperty> getProperties() {
        return properties;
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