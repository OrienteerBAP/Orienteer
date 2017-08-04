package org.orienteer.architect;

import com.google.common.base.Strings;
import org.junit.Test;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.architect.util.OArchitectOProperty;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class OArchitectTest {

    @Test
    public void testJsonParse() {
        String json = "[{\"name\":\"Worker\"," +
                            "\"properties\":[" +
                                "{\"oClassName\":\"MyClass\",\"name\":\"name\",\"type\":\"STRING\"}," +
                                "{\"oClassName\":\"MyClass\",\"name\":\"id\",\"type\":\"INTEGER\"}" +
                                "]," +
                             "\"superClasses\":[]}," +
                        "{\"name\":\"Admin\"," +
                            "\"properties\":[" +
                                "{\"oClassName\":\"Admin\",\"name\":\"permission\",\"type\":\"INTEGER\"}]," +
                            "\"superClasses\":[\"Worker\"]}]";
        List<OArchitectOClass> classes = JsonUtil.convertFromJSON(json);
        for (OArchitectOClass oClass : classes) {
            assertNotNull(oClass);
            assertFalse(Strings.isNullOrEmpty(oClass.getName()));
            if (oClass.getProperties() != null && !oClass.getProperties().isEmpty()) {
                testOClassProperties(oClass.getProperties());
            }
            if (oClass.getSuperClasses() != null && !oClass.getSuperClasses().isEmpty()) {
                testOClassSuperClasses(oClass.getSuperClasses());
            }
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
}