package org.orienteer.junit;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.inject.Inject;

class SudoRule implements MethodRule {
	
	@Inject
	private OrienteerTester tester;
	
	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		Sudo sudo = method.getMethod().getAnnotation(Sudo.class);
		if(sudo==null) return base;
		else return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				if(!tester.signIn(sudo.value(), sudo.password()))
					throw new IllegalAccessException("Can't perform sudo under '"+sudo.value()+"' for method "+method.getName());
				base.evaluate();
				tester.signOut();
			}
		};
	}

}
