package org.orienteer.core;

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
public enum CustomAttributes
{
	/**
	 * Is this property calculable or not
	 */
	CALCULABLE("orienteer.calculable", OType.BOOLEAN, false, false),
	/**
	 * Script to calculate value of the property
	 */
	CALC_SCRIPT("orienteer.script", OType.STRING, null, true),
	/**
	 * Is this property displayable or not
	 */
	DISPLAYABLE("orienteer.displayable", OType.BOOLEAN, false, false),
	/**
	 * Is this property should be readonly in UI
	 */
	UI_READONLY("orienteer.uireadonly", OType.BOOLEAN, false, false),
	
	/**
	 * Is this property value should not be visible
	 */
	HIDDEN("orienteer.hidden", OType.BOOLEAN, false, false),
	/**
	 * Order of this property in a table/tab
	 */
	ORDER("orienteer.order", OType.INTEGER, 0, false),
	/**
	 * Name of the tab where this parameter should be shown
	 */
	TAB("orienteer.tab", OType.STRING, null, false),
	/**
	 * Name of the property which is storing name of this entity
	 */
	PROP_NAME("orienteer.prop.name", OType.LINK, OProperty.class, null, false, true),
	/**
	 * Name of property which is storing link to a parent entity
	 */
	PROP_PARENT("orienteer.prop.parent", OType.LINK, OProperty.class, null, false, true),
	/**
	 * Name of a visualization that should be used for property visualization
	 */
	VISUALIZATION_TYPE("orienteer.visualization", OType.STRING, "default", false),
	/**
	 * Link to an inverse property in respect to this one
	 */
	PROP_INVERSE("orienteer.inverse", OType.LINK, OProperty.class, null, false),
	/**
	 * Description of {@link OProperty} or {@link OClass}
	 */
    DESCRIPTION("orienteer.description",OType.STRING,null,true),

	/**
	 *	Access levels ("_allow", "_allowRead", "_allowUpdate", or "_allowDelete") granted on document creation.
	 */
	ON_CREATE_FIELDS("onCreate.fields",OType.STRING,null,true),

	/**
	 * Identity type ("user" or "role") who will get access rights on document creation.
	 */
	ON_CREATE_IDENTITY_TYPE("onCreate.identityType",OType.STRING,null,true),
    /**
     * Property name by which to sort data by default
     */
    SORT_BY("orienteer.sortby", OType.LINK, OProperty.class, null, false, true),
    /**
     * Order in which to sort data
     */
    SORT_ORDER("orienteer.sortorder", OType.BOOLEAN, null, false, true),
	/**
	 * Default search query for class
	 */
	SEARCH_QUERY("orienteer.searchquery", OType.STRING, null, true, true),
	
	/**
	 * Domain of a class
	 */
	DOMAIN("orienteer.domain", OType.STRING, OClassDomain.class, OClassDomain.BUSINESS, false, true);

	private final String name;
	private final OType type;
	private final Object defaultValue;
	private final Class<?> javaClass;
	private final boolean encode;
	private final boolean hiearchical;
	
	private static final Map<String, CustomAttributes> QUICK_CACHE = new HashMap<String, CustomAttributes>();
	
	private CustomAttributes(String name, OType type, Object defaultValue, boolean encode){
		this(name, type, defaultValue, encode, false);
	}
	
	private CustomAttributes(String name, OType type, Object defaultValue, boolean encode, boolean hiearchical)
	{
		this(name, type, type.getDefaultJavaType(), defaultValue, encode, hiearchical);
	}
	
	private CustomAttributes(String name, OType type, Class<?> javaClass, Object defaultValue, boolean encode) {
		this(name, type, javaClass, defaultValue, encode, false);
	}
	
	private CustomAttributes(String name, OType type, Class<?> javaClass, Object defaultValue, boolean encode, boolean hiearchical)
	{
		this.name = name;
		this.type = type;
		this.javaClass = javaClass;
		this.defaultValue = defaultValue;
		this.encode = encode;
		this.hiearchical = hiearchical;
	}

	public String getName() {
		return name;
	}
	
	public boolean match(String critery)
	{
		return name.equals(critery);
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

	public static CustomAttributes fromString(String name)
	{
		if(name==null) return null;
		if(QUICK_CACHE.containsKey(name)) return QUICK_CACHE.get(name);
		else
		{
			for (CustomAttributes customAttribute : values()) {
				if(customAttribute.getName().equals(name))
				{
					QUICK_CACHE.put(name, customAttribute);
					return customAttribute;
				}
			}
			return null;
		}
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
	
	public static boolean match(String critery, CustomAttributes... attrs)
	{
		for (CustomAttributes customAttributes : attrs)
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
	
}
