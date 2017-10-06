package org.orienteer.architect.util;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.http.util.Args;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.CustomAttribute;

/**
 * Utility class which represents {@link com.orientechnologies.orient.core.metadata.schema.OProperty} from JSON string
 */
public class OArchitectOProperty implements IClusterable {

    private String name;
    private OType type;
    private boolean subClassProperty;
    private String linkedClass;
    private String pageUrl;
    private int order;
    private boolean inversePropertyEnable;
    private OArchitectOProperty inverseProperty;
    private boolean existsInDb;

    public static OArchitectOProperty toArchitectOProperty(OClass oClass, OProperty property) {
        OArchitectOProperty architectProperty = new OArchitectOProperty(property.getName(), property.getType());
        if (property.getLinkedClass() != null) {
            architectProperty.setLinkedClass(property.getLinkedClass().getName());
            OProperty inverse = CustomAttribute.PROP_INVERSE.getValue(property);
            if (inverse != null) {
                OArchitectOProperty prop = new OArchitectOProperty(inverse.getName(), inverse.getType());
                prop.setExistsInDb(true);
                architectProperty.setInversePropertyEnable(true);
                architectProperty.setInverseProperty(prop);
            }
        }
        int order = CustomAttribute.ORDER.getValue(property);
        architectProperty.setOrder(order);
        architectProperty.setPageUrl("/property/" + oClass.getName() + "/" + property.getName());
        architectProperty.setExistsInDb(true);
        return architectProperty;
    }

    public OArchitectOProperty(String name, OType type) {
        this(name, type, false, null);
    }

    public OArchitectOProperty(String name, OType type, boolean subClassProperty) {
        this(name, type, subClassProperty, null);
    }

    public OArchitectOProperty(String name, OType type, boolean subClassProperty, String linkedClass) {
        Args.notEmpty(name, "name");
        this.name = name;
        this.type = type;
        this.subClassProperty = subClassProperty;
        this.linkedClass = linkedClass;
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

    public String getLinkedClass() {
        return linkedClass;
    }

    public void setLinkedClass(String linkedClass) {
        this.linkedClass = linkedClass;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public OArchitectOProperty getInverseProperty() {
        return inverseProperty;
    }

    public void setInverseProperty(OArchitectOProperty inverseProperty) {
        this.inverseProperty = inverseProperty;
    }

    public boolean isExistsInDb() {
        return existsInDb;
    }

    public void setExistsInDb(boolean existsInDb) {
        this.existsInDb = existsInDb;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isInversePropertyEnable() {
        return inversePropertyEnable;
    }

    public void setInversePropertyEnable(boolean inversePropertyEnable) {
        this.inversePropertyEnable = inversePropertyEnable;
    }

    @Override
    public String toString() {
        return getName();
    }
}