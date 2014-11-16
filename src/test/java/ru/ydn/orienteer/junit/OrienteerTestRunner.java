package ru.ydn.orienteer.junit;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.runners.model.InitializationError;

import ru.ydn.orienteer.services.OrienteerModule;
import ru.ydn.orienteer.standalone.StartStandalone;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class OrienteerTestRunner extends GuiceTestRunner
{
	private static final Injector STATIC_INJECTOR;
	static
	{
		System.out.println("Using embedded mode");
		System.setProperty(StartStandalone.PROPERTIES_FILE_NAME, StartStandalone.class.getResource("standalone.properties").toString());
		STATIC_INJECTOR = Guice.createInjector(Modules.override(new OrienteerModule()).with(new TestOrieenteerModule()));
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run() {
				WicketTester wicketTester = STATIC_INJECTOR.getInstance(WicketTester.class);
				wicketTester.destroy();
			}
		});
	}
	
	public OrienteerTestRunner(Class<?> classToRun) throws InitializationError
	{
		super(classToRun, STATIC_INJECTOR);
	}

	@Override
	public Object createTest() {
		//Ensure that wicket tester and corresponding application started
		getInjector().getInstance(WicketTester.class);
		return super.createTest();
	}
	
	
}
