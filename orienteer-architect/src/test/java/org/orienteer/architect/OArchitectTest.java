package org.orienteer.architect;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.Test;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.architect.util.OArchitectOProperty;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class OArchitectTest {

    @Test
    public void testJSON() {
        String json = createJSON();
        parseJSON(json);
    }

    public void parseJSON(String json) {
        List<OArchitectOClass> classes = JsonUtil.fromJSON(json);
        for (OArchitectOClass oClass : classes) {
            testOArchitectClass(oClass);
        }
    }

    public String createJSON() {
        List<OArchitectOClass> classes = createClasses();
        String json = JsonUtil.toJSON(classes);
        return json;
    }

    private void testOArchitectClass(OArchitectOClass oClass) {
        assertNotNull(oClass);
        assertFalse(Strings.isNullOrEmpty(oClass.getName()));
        if (oClass.getProperties() != null && !oClass.getProperties().isEmpty()) {
            testOClassProperties(oClass.getProperties());
        }
        if (oClass.getSuperClasses() != null && !oClass.getSuperClasses().isEmpty()) {
            testOClassSuperClasses(oClass.getSuperClasses());
        }
    }

    private void testOClassProperties(List<OArchitectOProperty> properties) {
        for (OArchitectOProperty property : properties) {
            assertFalse(Strings.isNullOrEmpty(property.getName()));
            assertNotNull(property.getType());
        }
    }

    private void testOClassSuperClasses(List<String> superClasses) {
        for (String superClass : superClasses) {
            assertFalse(Strings.isNullOrEmpty(superClass));
        }
    }

    private List<OArchitectOClass> createClasses() {
        List<OArchitectOClass> classes = Lists.newArrayList();
        List<OArchitectOProperty> properties = Lists.newArrayList();
        List<String> superClasses = Lists.newArrayList();
        OArchitectOClass oClass = new OArchitectOClass("Test");

        properties.add(new OArchitectOProperty("id", OType.INTEGER));
        properties.add(new OArchitectOProperty("name", OType.STRING));
        superClasses.add("Test2");
        superClasses.add("Test3");
        oClass.setProperties(properties);
        oClass.setSuperClasses(superClasses);
        classes.add(oClass);

        superClasses = Lists.newArrayList();
        properties = Lists.newArrayList();

        oClass = new OArchitectOClass("Worker");
        properties.add(new OArchitectOProperty("id", OType.INTEGER));
        properties.add(new OArchitectOProperty("name", OType.STRING));
        properties.add(new OArchitectOProperty("country", OType.STRING));
        superClasses.add("Test3");
        superClasses.add("Test5");
        oClass.setProperties(properties);
        oClass.setSuperClasses(superClasses);
        classes.add(oClass);

        return classes;
    }
}