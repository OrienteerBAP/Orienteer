package ru.ydn.orienteer.junit;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

public class GuiceTestRunner extends BlockJUnit4ClassRunner
{
	private final Provider<Injector> injectorProvider;

	public GuiceTestRunner(final Class<?> classToRun,
			Provider<Injector> injectorProvider) throws InitializationError
	{
		super(classToRun);
		this.injectorProvider = injectorProvider;
	}

	@Override
	public Object createTest() {
		return getInjector().getInstance(getTestClass().getJavaClass());
	}

	@Override
	protected void validateZeroArgConstructor(List<Throwable> errors) {
		// Guice can inject constructors with parameters so we don't want this
		// method to trigger an error
	}

	protected Injector getInjector() {
		return injectorProvider.get();
	}
}
