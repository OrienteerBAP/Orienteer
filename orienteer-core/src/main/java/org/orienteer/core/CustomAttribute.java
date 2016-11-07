package org.orienteer.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.string.Strings;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Orienteer needs in additional attributes specified for {@link OClass} and {@link OProperty}
 * This class allow flexibly access to that custom parameters.
 */
public final class CustomAttribute
{
	private static final Map<String, CustomAttribute> CACHE = new HashMap<String, CustomAttribute>();
	
	/**
	 * Is this property calculable or not
	 */
	public static final CustomAttribute CALCULABLE = create("orienteer.calculable", OType.BOOLEAN, false, false, true);
	/**
	 * Script to calculate value of the property
	 */
	public static final CustomAttribute CALC_SCRIPT = create("orienteer.script", OType.STRING, null, true, true);
	/**
	 * Is this property displayable or not
	 */
	public static final CustomAttribute DISPLAYABLE = create("orienteer.displayable", OType.BOOLEAN, false, false, true);
	/**
	 * Is this property should be readonly in UI
	 */
	public static final CustomAttribute UI_READONLY = create("orienteer.uireadonly", OType.BOOLEAN, false, false, true);
	
	/**
	 * Is this property value should not be visible
	 */
	public static final CustomAttribute HIDDEN = create("orienteer.hidden", OType.BOOLEAN, false, false, true);
	/**
	 * Order of this property in a table/tab
	 */
	public static final CustomAttribute ORDER = create("orienteer.order", OType.INTEGER, 0, false, true);
	/**
	 * Name of the tab where this parameter should be shown
	 */
	public static final CustomAttribute TAB = create("orienteer.tab", OType.STRING, null, false, true);
	/**
	 * Name of the property which is storing name of this entity
	 */
	public static final CustomAttribute PROP_NAME = create("orienteer.prop.name", OType.LINK, OProperty.class, null, false, true);
	/**
	 * Name of property which is storing link to a parent entity
	 */
	public static final CustomAttribute PROP_PARENT = create("orienteer.prop.parent", OType.LINK, OProperty.class, null, false, true);
	/**
	 * Name of a visualization that should be used for property visualization
	 */
	public static final CustomAttribute VISUALIZATION_TYPE = create("orienteer.visualization", OType.STRING, "default", false, true);
	/**
	 * Link to an inverse property in respect to this one
	 */
	public static final CustomAttribute PROP_INVERSE = create("orienteer.inverse", OType.LINK, OProperty.class, null, false, true);
	/**
	 * Description of {@link OProperty} or {@link OClass}
	 */
	public static final CustomAttribute DESCRIPTION = create("orienteer.description",OType.STRING,null,true, false);

	/**
	 *	Access levels ("_allow", "_allowRead", "_allowUpdate", or "_allowDelete") granted on document creation.
	 */
	public static final CustomAttribute ON_CREATE_FIELDS = create("onCreate.fields",OType.STRING,null,true, true);

	/**
	 * Identity type ("user" or "role") who will get access rights on document creation.
	 */
	public static final CustomAttribute ON_CREATE_IDENTITY_TYPE = create("onCreate.identityType",OType.STRING,null,true, true);
    /**
     * Property name by which to sort data by default
     */
	public static final CustomAttribute SORT_BY = create("orienteer.sortby", OType.LINK, OProperty.class, null, false, true);
    /**
     * Order in which to sort data
     */
	public static final CustomAttribute SORT_ORDER = create("orienteer.sortorder", OType.BOOLEAN, null, null, false, true);
	/**
	 * Default search query for class
	 */
	public static final CustomAttribute SEARCH_QUERY = create("orienteer.searchquery", OType.STRING, null, null, true, true);
	/**
	 * Domain of a class
	 */
	public static final CustomAttribute DOMAIN = create("orienteer.domain", OType.STRING, OClassDomain.class, OClassDomain.BUSINESS, false, true);

	private final String name;
	private final OType type;
	private final Object defaultValue;
	private final Class<?> javaClass;
	private final boolean encode;
	private final boolean hiearchical;
	
	private CustomAttribute(String name, OType type, Class<?> javaClass, Object defaultValue, boolean encode, boolean hiearchical)
	{
		this.name = name;
		this.type = type;
		this.javaClass = javaClass!=null?javaClass:type.getDefaultJavaType();
		this.defaultValue = defaultValue;
		this.encode = encode;
		this.hiearchical = hiearchical;
		CACHE.put(name, this);
	}
	
	/*public static CustomAttribute create(String name, OType type, Object defaultValue, boolean encode) {
		return create(name, type, null, defaultValue, encode, false);
	}*/
	
	/*public static CustomAttribute create(String name, OType type, Class<?> javaClass, Object defaultValue, boolean encode) {
		return create(name, type, javaClass, defaultValue, encode, false);
	}*/
	
	public static CustomAttribute create(String name, OType type, Object defaultValue, boolean encode, boolean hiearchical) {
		return create(name, type, null, defaultValue, encode, hiearchical);
	}
	
	public static CustomAttribute create(String name, OType type, Class<?> javaClass, Object defaultValue, boolean encode, boolean hiearchical) {
		CustomAttribute ret = getIfExists(name);
		if(ret!=null) throw new IllegalArgumentException("Custom attribute with name '"+name+"' is already exist");
		ret = new CustomAttribute(name, type, javaClass, defaultValue, encode, hiearchical);
		return ret;
	}
	
	public static CustomAttribute get(String name) {
		CustomAttribute ret = getIfExists(name);
		if(ret==null) throw new IllegalArgumentException("Custom attribute with name '"+name+"' was not found");
		return ret;
	}
	
	public static CustomAttribute getIfExists(String name) {
		return CACHE.get(name);
	}
	
	public static Collection<CustomAttribute> values() {
		return Collections.unmodifiableCollection(CACHE.values());
	}

	public String getName() {
		return name;
	}
	
	public boolean match(String critery)
	{
		return name.equals(critery);
	}
	
	public boolean matchAny(String... criteries)
	{
		if(criteries==null || criteries.length==0) return false;
		for(String critery : criteries) {
			if(name.equals(critery)) return true;
		}
		return false;
	}
	
	public boolean matchAny(CustomAttribute... criteries)
	{
		if(criteries==null || criteries.length==0) return false;
		for(CustomAttribute critery : criteries) {
			if(equals(critery)) return true;
		}
		return false;
	}
	
	public OType getType() {
		return type;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}

	public boolean isEncode() {
		return encode;
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue(OProperty property)
	{
		return getValue(property, (V) defaultValue);
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getValue(OProperty property, V defaultValue)
	{
		String stringValue = property.getCustom(name);
		if(encode) stringValue = decodeCustomValue(stringValue);
		V ret;
		if(OProperty.class.isAssignableFrom(javaClass))
		{
			ret = (V)resolveProperty(property.getOwnerClass(), stringValue);
		}
		else
		{
			ret = (V) OType.convert(stringValue, javaClass);
		}
		return ret!=null?ret:defaultValue;
	}
	
	public <V> void setValue(OProperty property, V value)
	{
		if(OProperty.class.isAssignableFrom(javaClass) && value instanceof OProperty)
		{
			OProperty valueProperty = (OProperty)value;
			boolean fullNameRequired = !Objects.equals(property.getOwnerClass(), valueProperty.getOwnerClass());
			property.setCustom(name, fullNameRequired?valueProperty.getFullName():valueProperty.getName());
		}
		else
		{
			if(defaultValue!=null && defaultValue.equals(value)) value = null;
			String stringValue = value!=null?value.toString():null;
			if(stringValue!=null && stringValue.length()==0) stringValue=null;
			if(encode) stringValue = encodeCustomValue(stringValue);
			property.setCustom(name, stringValue);
		}
	}
	
	public <V> V getValue(OClass oClass) {
		return getValue(oClass, hiearchical);
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getValue(OClass oClass, boolean hiearchical)
	{
		return getValue(oClass, (V) defaultValue, hiearchical);
	}
	
	public <V> V getValue(OClass oClass, V defaultValue) {
		return getValue(oClass, defaultValue, hiearchical);
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getValue(OClass oClass, V defaultValue, boolean hiearchical)
	{
		String stringValue = oClass.getCustom(name);
		if(encode) stringValue = decodeCustomValue(stringValue);
		V ret;
		if(OProperty.class.isAssignableFrom(javaClass))
		{
			ret = (V)resolveProperty(oClass, stringValue);
		}
		else
		{
			ret = (V) OType.convert(stringValue, javaClass);
		}
		if(ret==null && hiearchical) {
			for(OClass superClass : oClass.getSuperClasses()) {
				if((ret=getValue(superClass, null, true))!=null) break;
			}
		}
		return ret!=null?ret:defaultValue;
	}
	
	public <V> void setValue(OClass oClass, V value) {
		setValue(oClass, value, hiearchical);
	}
	
	public <V> void setValue(OClass oClass, V value, boolean hiearchical)
	{
		if(hiearchical && Objects.equals(value, getValue(oClass, true))) return;
		if(OProperty.class.isAssignableFrom(javaClass) && value instanceof OProperty)
		{
			OProperty valueProperty = (OProperty)value;
			boolean fullNameRequired = !Objects.equals(oClass, valueProperty.getOwnerClass());
			oClass.setCustom(name, fullNameRequired?valueProperty.getFullName():valueProperty.getName());
		}
		else
		{
			if(!hiearchical && defaultValue!=null && defaultValue.equals(value)) value = null;
			String stringValue = value!=null?value.toString():null;
			if(stringValue!=null && stringValue.length()==0) stringValue=null;
			if(encode) stringValue = encodeCustomValue(stringValue);
			oClass.setCustom(name, stringValue);
		}
	}
	
	private OProperty resolveProperty(OClass oClass, String propertyName)
	{
		if(Strings.isEmpty(propertyName)) return null;
		int indx = propertyName.indexOf('.');
		if(indx>0)
		{
			String className = propertyName.substring(0, indx);
			propertyName = propertyName.substring(indx+1);
			oClass = ((OClassImpl)oClass).getOwner().getClass(className);
			if(oClass==null) return null;
		}
		return oClass.getProperty(propertyName);
	}
	
	public static boolean match(String critery, CustomAttribute... attrs)
	{
		for (CustomAttribute customAttributes : attrs)
		{
			if(customAttributes.match(critery)) return true;
		}
		return false;
	}
	
	public static String encodeCustomValue(String value)
	{
		if(value==null) return null;
		StringBuilder sb = new StringBuilder(value.length());
		for(int i=0; i<value.length();i++)
		{
			char ch = value.charAt(i);
			switch (ch)
			{
				case '=':
					sb.append("\\e");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\n':
					sb.append("\\n");
					break;
				default:
					sb.append(ch);
					break;
			}
		}
		return sb.toString();
	}
	
	public static String decodeCustomValue(String value)
	{
		if(value==null) return null;
		StringBuilder sb = new StringBuilder(value.length());
		for(int i=0; i<value.length();i++)
		{
			char ch = value.charAt(i);
			if(ch!='\\')
			{
				sb.append(ch);
			}
			else
			{
				if(++i>=value.length())
				{
					sb.append('\\');
					break;
				}
				else
				{
					ch = value.charAt(i);
					switch (ch)
					{
						case 'e':
							sb.append('=');
							break;
						case '\\':
							sb.append('\\');
							break;
						case 'r':
							sb.append('\r');
							break;
						case 'n':
							sb.append('\n');
							break;
						default:
							sb.append('\\').append(ch);
							break;
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomAttribute other = (CustomAttribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
}
