package org.orienteer.architect.util;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.http.util.Args;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.model.ExtendedOPropertiesDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

    public static OArchitectOClass toArchitectOClass(OClass oClass) {
        OArchitectOClass architectOClass = new OArchitectOClass(oClass.getName());
        architectOClass.setExistsInDb(true);

        architectOClass.setProperties(toOArchitectProperties(oClass, oClass.getSuperClasses()));
        architectOClass.setSuperClasses(toOArchitectClassNames(oClass.getSuperClasses()));
        architectOClass.setSubClasses(toOArchitectClassNames(oClass.getSubclasses()));
        architectOClass.setPageUrl("/class/" + oClass.getName());
        return architectOClass;
    }

    private static List<OArchitectOProperty> toOArchitectProperties(OClass oClass, List<OClass> superClasses) {
        Collection<OProperty> properties = oClass.properties();
        List<OArchitectOProperty> architectProperties = new ArrayList<>(properties.size());
        ExtendedOPropertiesDataProvider provider = new ExtendedOPropertiesDataProvider(new CollectionModel<>(properties));
        provider.setSort(CustomAttribute.ORDER.getName(), SortOrder.ASCENDING);
        Iterator<? extends OProperty> iterator = provider.iterator(0, provider.size());
        while (iterator.hasNext()){
            OProperty property = iterator.next();
            OArchitectOProperty architectOProperty = OArchitectOProperty.toArchitectOProperty(oClass, property);
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