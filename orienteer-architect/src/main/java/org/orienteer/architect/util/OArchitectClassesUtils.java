package org.orienteer.architect.util;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.util.CollectionModel;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.OArchitectOProperty;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.model.ExtendedOPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class OArchitectClassesUtils {

    private OArchitectClassesUtils() {}

    public static List<OArchitectOClass> getAllClasses() {
        return DBClosure.sudo(db ->
            toOArchitectClasses(db.getMetadata().getSchema().getClasses())
        );
    }

    public static List<OArchitectOClass> toOArchitectClasses(Collection<OClass> classes) {
        return classes.stream()
                .map(OArchitectClassesUtils::toArchitectOClass)
                .collect(Collectors.toList());
    }

    public static boolean isClassContainsIn(String name, List<OArchitectOClass> classes) {
        for (OArchitectOClass oClass : classes) {
            if (oClass.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

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

}
