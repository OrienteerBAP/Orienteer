package org.orienteer.junit;

import org.apache.wicket.util.tester.WicketTester;
import org.orienteer.core.service.OrienteerModule;
import org.orienteer.core.service.OrienteerServletContextListener;
import org.orienteer.core.service.OrienteerServletModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.util.Modules;

public class StaticInjectorProvider implements Provider<Injector>
{
	static {
		System.setProperty(OrienteerServletModule.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, "orienteer-test");
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(StaticInjectorProvider.class);
	
	private static final Injector STATIC_INJECTOR;
	
	static
	{
		LOG.info("Using embedded mode");
		STATIC_INJECTOR = Guice.createInjector(new OrienteerServletModule());
		OrienteerServletContextListener.predefinedInjector = STATIC_INJECTOR;
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
