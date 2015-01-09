package ru.ydn.orienteer.junit;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.inject.Injector;
import com.google.inject.Provider;

public class GuiceRule implements MethodRule
{
	private final Provider<Injector> injectorProvider;
	
	public GuiceRule(Provider<Injector> injectorProvider)
	{
		this.injectorProvider = injectorProvider;
	}
	
	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				Injector injector = injectorProvider.get();
				injector.injectMembers(target);
			}
		};
	}

}
