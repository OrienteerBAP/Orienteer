package org.orienteer.junit;

import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.services.InstanceOfMatcher;

import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

public class OrienteerTestModule extends AbstractModule
{
	public static final String TEST_PROPERTIES_FILE_NAME = "archetype-resources/orienteer-test.properties";

	@Override
	protected void configure() {
		
		bind(Boolean.class).annotatedWith(Names.named("testing")).toInstance(true);
		bindListener(InstanceOfMatcher.createFor(WebApplication.class), new TypeListener() {
			
			@Override
			public <I> void hear(TypeLiteral<I> type, final TypeEncounter<I> encounter) {
				final Provider<Injector> injectorProvider = encounter.getProvider(Injector.class);
				encounter.register(new InjectionListener<Object>() {

					@Override
					public void afterInjection(Object injectee) {
						WebApplication app = (WebApplication)injectee;
						app.getComponentInstantiationListeners().add(new GuiceComponentInjector(app, injectorProvider.get()));
					}
				});
			}
		});
		bind(OrienteerTester.class).asEagerSingleton();
		Provider<OrienteerTester> provider = binder().getProvider(OrienteerTester.class);
		bind(WicketTester.class).toProvider(provider);
		bind(WicketOrientDbTester.class).toProvider(provider);
	}
	
	@Provides
	public ODatabaseDocument getDatabaseRecord()
	{
		ODatabaseDocument db = DefaultODatabaseThreadLocalFactory.castToODatabaseDocument(ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner());
		if(db.isClosed())
		{
			ODatabaseRecordThreadLocal.INSTANCE.remove();
			db = DefaultODatabaseThreadLocalFactory.castToODatabaseDocument(ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner());
		}
		return db;
	}

}
