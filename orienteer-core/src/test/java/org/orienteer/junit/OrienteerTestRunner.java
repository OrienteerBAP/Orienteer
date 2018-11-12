package org.orienteer.junit;

import java.util.List;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.rules.MethodRule;
import org.junit.runners.model.InitializationError;

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
	
	
	@Override
	protected List<MethodRule> rules(Object target) {
		List<MethodRule> result = super.rules(target);
		result.add(0, getInjector().getInstance(SudoRule.class));
		return result;
	}
	
	
}
