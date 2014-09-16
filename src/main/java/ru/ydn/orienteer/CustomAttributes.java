package ru.ydn.orienteer;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.util.string.Strings;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;

public enum CustomAttributes
{
	/**
	 * Is this property calculable or not
	 */
	CALCULABLE("orienteer.calculable", OType.BOOLEAN, false),
	/**
	 * Script to calculate value of the property
	 */
	CALC_SCRIPT("orienteer.script", OType.STRING, true),
	/**
	 * Is this property displayable or not
	 */
	DISPLAYABLE("orienteer.displayable", OType.BOOLEAN, false),
	
	/**
	 * Is this property value should not be visible
	 */
	HIDDEN("orienteer.hidden", OType.BOOLEAN, false),
	/**
	 * Order of this property in a table/tab
	 */
	ORDER("orienteer.order", OType.INTEGER, false),
	/**
	 * Name of the tab where this parameter should be shown
	 */
	TAB("orienteer.tab", OType.STRING, false),
	/**
	 * Name of the property which is storing name of this entity
	 */
	PROP_NAME("orienteer.prop.name", OType.LINK, OProperty.class, false),
	/**
	 * Name of property which is storing link to a parent entity
	 */
	PROP_PARENT("orienteer.prop.parent", OType.LINK, OProperty.class, false),
	
//	VIEW_COMPONENT("orienteer.component.view", OType.STRING),
//	EDIT_COMPONENT("orienteer.component.edit", OType.STRING),
	VISUALIZATION_TYPE("orienteer.visualization", OType.STRING, false);
	
	private final String name;
	private final OType type;
	private final Class<?> javaClass;
	private final boolean encode;
	
	private static final Map<String, CustomAttributes> QUICK_CACHE = new HashMap<String, CustomAttributes>();
	
	private CustomAttributes(String name, OType type, boolean encode)
	{
		this(name, type, type.getDefaultJavaType(), encode);
	}
	
	private CustomAttributes(String name, OType type, Class<?> javaClass, boolean encode)
	{
		this.name = name;
		this.type = type;
		this.javaClass = javaClass;
		this.encode = encode;
	}

	public String getName() {
		return name;
	}
	
	public static CustomAttributes fromString(String name)
	{
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
		String stringValue = property.getCustom(name);
		if(encode) stringValue = decodeCustomValue(stringValue);
		return (V) OType.convert(stringValue, javaClass);
	}
	
	public <V> V getValue(OProperty property, V defaultValue)
	{
		V ret = getValue(property);
		return ret!=null?ret:defaultValue;
	}
	
	public <V> void setValue(OProperty property, V value)
	{
		String stringValue = value!=null?value.toString():null;
		if(encode) stringValue = encodeCustomValue(stringValue);
		property.setCustom(name, stringValue);
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getValue(OClass oClass)
	{
		String stringValue = oClass.getCustom(name);
		if(encode) stringValue = decodeCustomValue(stringValue);
		if(OProperty.class.isAssignableFrom(javaClass))
		{
			return (V) (Strings.isEmpty(stringValue)?null:oClass.getProperty(stringValue));
		}
		else
		{
			return (V) OType.convert(stringValue, javaClass);
		}
	}
	
	public <V> V getValue(OClass oClass, V defaultValue)
	{
		V ret = getValue(oClass);
		return ret!=null?ret:defaultValue;
	}
	
	public <V> void setValue(OClass oClass, V value)
	{
		if(OProperty.class.isAssignableFrom(javaClass) && value instanceof OProperty)
		{
			oClass.setCustom(name, ((OProperty)value).getName());
		}
		else
		{
			String stringValue = value!=null?value.toString():null;
			if(encode) stringValue = encodeCustomValue(stringValue);
			oClass.setCustom(name, stringValue);
		}
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
