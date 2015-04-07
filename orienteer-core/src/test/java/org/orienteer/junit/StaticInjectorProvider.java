package org.orienteer.junit;

import org.apache.wicket.util.tester.WicketTester;
import org.orienteer.services.OrienteerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.util.Modules;

public class StaticInjectorProvider implements Provider<Injector>
{
	private static final Logger LOG = LoggerFactory.getLogger(StaticInjectorProvider.class);
	
	private static final Injector STATIC_INJECTOR;
	
	static
	{
		LOG.info("Using embedded mode");
		STATIC_INJECTOR = Guice.createInjector(Modules.override(new OrienteerModule()).with(new OrienteerTestModule()));
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run() {
				WicketTester wicketTester = STATIC_INJECTOR.getInstance(WicketTester.class);
				wicketTester.destroy();
			}
		});
	}
	
	public static final StaticInjectorProvider INSTANCE = new StaticInjectorProvider();

	@Override
	public Injector get() {
		return STATIC_INJECTOR;
	}

}
