package org.orienteer.junit;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.orienteer.core.service.InstanceOfMatcher;
import org.orienteer.core.service.OverrideModule;
import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;

@OverrideModule
public class OrienteerTestModule extends AbstractModule
{

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

		Multibinder<String> binder = Multibinder.newSetBinder(binder(), String.class, Names.named("orient.model.packages"));
		binder.addBinding().toInstance("org.orienteer.core.persist");
	}
	
	@Provides
	public ODatabaseDocument getDatabaseRecord()
	{
		ODatabaseDocument db = DefaultODatabaseThreadLocalFactory.castToODatabaseDocument(ODatabaseRecordThreadLocal.instance().get().getDatabaseOwner());
		if(db.isClosed())
		{
			ODatabaseRecordThreadLocal.instance().remove();
			db = DefaultODatabaseThreadLocalFactory.castToODatabaseDocument(ODatabaseRecordThreadLocal.instance().get().getDatabaseOwner());
		}
		return db;
	}

}
