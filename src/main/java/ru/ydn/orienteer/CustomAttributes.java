package ru.ydn.orienteer;

public enum CustomAttributes 
{
	CALCULABLE("orienteer.calculable"),
	CALC_SCRIPT("orienteer.script");
	private final String name;
	
	private CustomAttributes(String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
