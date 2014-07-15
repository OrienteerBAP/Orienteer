package ru.ydn.orienteer;

import java.util.HashMap;
import java.util.Map;

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
	PROP_NAME("orienteer.prop.name", OType.STRING),
	/**
	 * Name of property which is storing link to a parent entity
	 */
	PROP_PARENT("orienteer.prop.parent", OType.STRING);
	
	private final String name;
	private final OType type;
	
	private static final Map<String, CustomAttributes> QUICK_CACHE = new HashMap<String, CustomAttributes>();
	
	private CustomAttributes(String name, OType type)
	{
		this.name = name;
		this.type = type;
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
		return (V) OType.convert(property.getCustom(name), type.getDefaultJavaType());
	}
	
	public <V> void setValue(OProperty property, V value)
	{
		property.setCustom(name, value!=null?value.toString():null);
	}
	
}
