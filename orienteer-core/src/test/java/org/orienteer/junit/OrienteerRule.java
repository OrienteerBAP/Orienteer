package org.orienteer.junit;

public class OrienteerRule extends GuiceRule
{

	public OrienteerRule()
	{
		super(StaticInjectorProvider.INSTANCE);
	}

}
