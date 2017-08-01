package org.orienteer.architect;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.architect.util.OClassJsonDeserializer;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OArchitectTest {

    @Test
    public void testJsonParse() {
        String json = "[{\"name\":\"Admin\",\"properties\":[],\"superClasses\":[\"User\",\"Human\"]},{\"name\":\"User\",\"properties\":[],\"superClasses\":[]},{\"name\":\"Human\",\"properties\":[],\"superClasses\":[]}]";
        Type type = new TypeToken<List<OClassJsonDeserializer>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(type, new OClassJsonDeserializer())
                .create();
        List<OArchitectOClass> classes = gson.fromJson(json, type);
        assertTrue(classes.size() > 0);
        for (OArchitectOClass architectOClass : classes) {
            assertNotNull(architectOClass.getName());
            if (architectOClass.getSuperClasses() != null) {
                for (String superClass : architectOClass.getSuperClasses()) {
                    assertNotNull(superClass);
                }
            }
        }
    }
}