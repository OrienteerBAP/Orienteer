package org.orienteer.architect.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.orientechnologies.orient.core.metadata.schema.OType;

import java.lang.reflect.Type;
import java.util.List;

/**
 *
 */
public class OClassJsonDeserializer implements JsonDeserializer<List<OArchitectOClass>> {

    public static final String NAME          = "name";
    public static final String SUPER_CLASSES = "superClasses";

    @Override
    public List<OArchitectOClass> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<OArchitectOClass> classes = Lists.newArrayList();
        if (!element.isJsonNull() && element.isJsonArray()) {
            for (JsonElement el : element.getAsJsonArray()) {
                JsonObject obj = el.getAsJsonObject();
                String name = getStringValue(obj, NAME);
                if (!Strings.isNullOrEmpty(name)) {
                    classes.add(new OArchitectOClass(name, getSuperClasses(obj.get(SUPER_CLASSES))));
                }
            }
        }
        return classes;
    }

    private List<String> getSuperClasses(JsonElement element) {
        List<String> superClasses = Lists.newArrayList();
        if (!element.isJsonNull() && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement el : array) {
                if (!element.isJsonNull()) {
                    superClasses.add(el.getAsString());
                }
            }
        }
        return superClasses;
    }

    private String getStringValue(JsonObject object, String name) {
        JsonElement jsonElement = object.get(name);
        return !jsonElement.isJsonNull() ? jsonElement.getAsString() : null;
    }

    private OType getOTypeByName(String name) {
        return null;
    }
}
