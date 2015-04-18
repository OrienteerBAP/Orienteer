package org.orienteer.junit;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.runners.model.InitializationError;
import org.orienteer.core.service.OrienteerModule;

public class OrienteerTestRunner extends GuiceTestRunner
{
	public OrienteerTestRunner(Class<?> classToRun) throws InitializationError
	{
		super(classToRun, StaticInjectorProvider.INSTANCE);
	}

	@Override
	public Object createTest() {
		//Ensure that wicket tester and corresponding application started
		getInjector().getInstance(WicketTester.class);
		return super.createTest();
	}
	
	
}
