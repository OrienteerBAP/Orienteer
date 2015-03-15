package org.orienteer.junit;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.runners.model.InitializationError;
import org.orienteer.services.OrienteerModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

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
