package ru.ydn.orienteer.junit;

public class OrienteerRule extends GuiceRule
{

	public OrienteerRule()
	{
		super(StaticInjectorProvider.INSTANCE);
	}

}
