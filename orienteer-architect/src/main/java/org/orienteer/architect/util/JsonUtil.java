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

    private static final String NAME                  = "name";
    private static final String SUPER_CLASSES         = "superClasses";
    private static final String PROPERTIES            = "properties";
    private static final String PROPERTIES_FOR_DELETE = "propertiesForDelete";
    private static final String EXISTS_IN_DB          = "existsInDb";
    private static final String SUBCLASS_PROPERTY     = "subClassProperty";
    private static final String LINKED_CLASS_NAME     = "linkedClass";
    private static final String TYPE                  = "type";
    private static final String PAGE_URL              = "pageUrl";
    private static final String INVERSE_PROPERTY      = "inverseProperty";

    private JsonUtil() {}

    /**
     * Convert JSON string with array of classes to {@link List<OArchitectOClass>}
     * @param json JSON string which contains JSON array of OrientDB classes.
     * @return {@link List<OArchitectOClass>}
     * @throws IllegalArgumentException if json is not JSON array
     */
    public static List<OArchitectOClass> fromJSON(String json) {
        Args.isTrue(json.startsWith("["), "Input JSON string is not array! json: " + json);
        List<OArchitectOClass> classes = Lists.newArrayList();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            classes.add(convertOClassFromJson(jsonObject));
        }
        return classes;
    }

    /**
     * Convert {@link List<OArchitectOClass>} to JSON array string
     * @param classes {@link List<OArchitectOClass>} classes for convert to JSON
     * @return JSON string which contains JSON array with classes
     * @throws IllegalArgumentException if classes is null
     */
    public static String toJSON(List<OArchitectOClass> classes) {
        Args.notNull(classes, "classes");
        JSONArray array = new JSONArray(classes);
        return array.toString();
    }

    private static OArchitectOClass convertOClassFromJson(JSONObject jsonObject) {
        OArchitectOClass oClass = new OArchitectOClass(jsonObject.getString(NAME));
        if (!jsonObject.isNull(SUPER_CLASSES)) {
            oClass.setSuperClasses(getSuperClasses(jsonObject.getJSONArray(SUPER_CLASSES)));
        }
        if (!jsonObject.isNull(EXISTS_IN_DB)) {
            String exists = jsonObject.getString(EXISTS_IN_DB);
            oClass.setExistsInDb(exists.equals("1") || exists.equals("true"));
        }
        if (!jsonObject.isNull(PROPERTIES)) {
            oClass.setProperties(getOPropertyListFromJson(jsonObject.getJSONArray(PROPERTIES)));
        }
        if (!jsonObject.isNull(PROPERTIES_FOR_DELETE)) {
            oClass.setPropertiesForDelete(getOPropertyListFromJson(jsonObject.getJSONArray(PROPERTIES_FOR_DELETE)));
        }
        return oClass;
    }

    private static List<String> getSuperClasses(JSONArray jsonArray) {
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
        OArchitectOProperty property = new OArchitectOProperty(name, type);
        if (!jsonObject.isNull(SUBCLASS_PROPERTY)) {
            String subClassProperty = jsonObject.getString(SUBCLASS_PROPERTY);
            property.setSubClassProperty(subClassProperty.equals("1") || subClassProperty.equals("true"));
        }
        if (!jsonObject.isNull(LINKED_CLASS_NAME)) {
            property.setLinkedClass(jsonObject.getString(LINKED_CLASS_NAME));
        }
        if (!jsonObject.isNull(INVERSE_PROPERTY)) {
            property.setInverseProperty(jsonObject.getString(INVERSE_PROPERTY));
        }
        return property;
    }
}