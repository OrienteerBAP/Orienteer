package ru.ydn.orienteer;

public enum CustomAttributes 
{
	CALCULABLE("orienteer.calculable"),
	CALC_SCRIPT("orienteer.script"),
	PROP_NAME("orienteer.prop.name"),
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
