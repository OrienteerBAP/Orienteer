package ru.ydn.orienteer.junit;

import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.standalone.StartStandalone;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class TestOrieenteerModule extends AbstractModule
{
	private static class CloseableWicketTester extends WicketTester implements AutoCloseable
	{

		public CloseableWicketTester(WebApplication application)
		{
			super(application);
		}

		@Override
		public void close() throws Exception {
			destroy();
		}
		
	}

	@Override
	protected void configure() {
		
		bind(Boolean.class).annotatedWith(Names.named("testing")).toInstance(true);
	}
	
	@Provides
	@Singleton
	public WebApplication providesWebApplication(Injector injector)
	{
		WebApplication app = injector.getInstance(OrienteerWebApplication.class);
		app.getComponentInstantiationListeners().add(new GuiceComponentInjector(app, injector));
		return app;
	}
	
	@Provides
	@Singleton
	public WicketTester providesInitialWicketTester(WebApplication app)
	{
		return new CloseableWicketTester(app);
	}

}