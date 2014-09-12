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
	CALCULABLE("orienteer.calculable", OType.BOOLEAN),
	/**
	 * Script to calculate value of the property
	 */
	CALC_SCRIPT("orienteer.script", OType.STRING),
	/**
	 * Is this property displayable or not
	 */
	DISPLAYABLE("orienteer.displayable", OType.BOOLEAN),
	
	/**
	 * Is this property value should not be visible
	 */
	HIDDEN("orienteer.hidden", OType.BOOLEAN),
	/**
	 * Order of this property in a table/tab
	 */
	ORDER("orienteer.order", OType.INTEGER),
	/**
	 * Name of the tab where this parameter should be shown
	 */
	TAB("orienteer.tab", OType.STRING),
	/**
	 * Name of the property which is storing name of this entity
	 */
	PROP_NAME("orienteer.prop.name", OType.LINK, OProperty.class),
	/**
	 * Name of property which is storing link to a parent entity
	 */
	PROP_PARENT("orienteer.prop.parent", OType.LINK, OProperty.class),
	
//	VIEW_COMPONENT("orienteer.component.view", OType.STRING),
//	EDIT_COMPONENT("orienteer.component.edit", OType.STRING),
	VISUALIZATION_TYPE("orienteer.visualization", OType.STRING);
	
	private final String name;
	private final OType type;
	private final Class<?> javaClass;
	
	private static final Map<String, CustomAttributes> QUICK_CACHE = new HashMap<String, CustomAttributes>();
	
	private CustomAttributes(String name, OType type)
	{
		this(name, type, type.getDefaultJavaType());
	}
	
	private CustomAttributes(String name, OType type, Class<?> javaClass)
	{
		this.name = name;
		this.type = type;
		this.javaClass = javaClass;
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
		return (V) OType.convert(property.getCustom(name), javaClass);
	}
	
	public <V> V getValue(OProperty property, V defaultValue)
	{
		V ret = getValue(property);
		return ret!=null?ret:defaultValue;
	}
	
	public <V> void setValue(OProperty property, V value)
	{
		property.setCustom(name, value!=null?value.toString():null);
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getValue(OClass oClass)
	{
		if(OProperty.class.isAssignableFrom(javaClass))
		{
			String propertyName = oClass.getCustom(name);
			return (V) (Strings.isEmpty(propertyName)?null:oClass.getProperty(propertyName));
		}
		else
		{
			return (V) OType.convert(oClass.getCustom(name), javaClass);
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
			oClass.setCustom(name, value!=null?value.toString():null);
		}
	}
	
}
