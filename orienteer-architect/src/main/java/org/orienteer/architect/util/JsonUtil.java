package org.orienteer.architect.util;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.lang.Args;

import java.util.List;

/**
 * Utility class for work with JSON for 'orienteer-architect'
 */
public abstract class JsonUtil implements IClusterable {

    public static final String NAME          = "name";
    public static final String SUPER_CLASSES = "superClasses";
    public static final String PROPERTIES    = "properties";
    public static final String TYPE          = "type";

    private JsonUtil() {}

    /**
     * Convert JSON string with array of classes to {@link List<OArchitectOClass>}
     * @param json JSON string which contains JSON array of OrientDB classes.
     * @return {@link List<OArchitectOClass>}
     * @throws IllegalArgumentException if json is not JSON array
     */
    public static List<OArchitectOClass> convertFromJSON(String json) {
        Args.isTrue(json.startsWith("["), "Input JSON string is not array! json: " + json);
        List<OArchitectOClass> classes = Lists.newArrayList();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            classes.add(convertOClassFromJson(jsonObject));
        }
        return classes;
    }


    private static OArchitectOClass convertOClassFromJson(JSONObject jsonObject) {
        OArchitectOClass oClass = new OArchitectOClass(jsonObject.getString(NAME));
        oClass.setSuperClasses(getStringListFromJson(jsonObject.getJSONArray(SUPER_CLASSES)));
        oClass.setProperties(getOPropertyListFromJson(jsonObject.getJSONArray(PROPERTIES)));
        return oClass;
    }

    private static List<String> getStringListFromJson(JSONArray jsonArray) {
        List<String> stringList = Lists.newArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            stringList.add(jsonArray.getString(i));
        }
        return stringList;
    }

    private static List<OArchitectOProperty> getOPropertyListFromJson(JSONArray jsonArray) {
        List<OArchitectOProperty> properties = Lists.newArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            properties.add(convertOPropertyFromJson(jsonArray.getJSONObject(i)));
        }
        return properties;
    }

    private static OArchitectOProperty convertOPropertyFromJson(JSONObject jsonObject) {
        String name = jsonObject.getString(NAME);
        OType type = OType.valueOf(jsonObject.getString(TYPE));
        return new OArchitectOProperty(name, type);
    }
}