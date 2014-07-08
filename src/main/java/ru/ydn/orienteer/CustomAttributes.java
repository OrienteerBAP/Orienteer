package ru.ydn.orienteer;

public enum CustomAttributes
{
	/**
	 * Is this property calculable or not
	 */
	CALCULABLE("orienteer.calculable"),
	/**
	 * Script to calculate value of the property
	 */
	CALC_SCRIPT("orienteer.script"),
	/**
	 * Is this property displayable or not
	 */
	DISPLAYABLE("orienteer.displayable"),
	/**
	 * Order of this property in a table/tab
	 */
	ORDER("orienteer.order"),
	/**
	 * Name of the tab where this parameter should be shown
	 */
	TAB("orienteer.tab"),
	/**
	 * Name of the property which is storing name of this entity
	 */
	PROP_NAME("orienteer.prop.name"),
	/**
	 * Name of property which is storing link to a parent entity
	 */
	PROP_PARENT("orienteer.prop.parent");
	
	private final String name;
	
	private CustomAttributes(String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
