package ru.ydn.orienteer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.guice.GuiceInjectorHolder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import ru.ydn.orienteer.components.properties.UIComponentsRegistry;
import ru.ydn.orienteer.hooks.CalculablePropertiesHook;
import ru.ydn.orienteer.hooks.ReferencesConsistencyHook;
import ru.ydn.orienteer.modules.IOrienteerModule;
import ru.ydn.orienteer.modules.ModuledDataInstallator;
import ru.ydn.orienteer.modules.OrienteerLocalizationModule;
import ru.ydn.orienteer.modules.PerspectivesModule;
import ru.ydn.orienteer.modules.UpdateDefaultSchemaModule;
import ru.ydn.orienteer.standalone.StartStandalone;
import ru.ydn.orienteer.web.LoginPage;
import ru.ydn.orienteer.web.schema.ListOClassesPage;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.server.config.OServerUserConfiguration;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see ru.ydn.orienteer.Start#main(String[])
 */
public class OrienteerWebApplication extends OrientDbWebApplication
{
	private boolean embedded;
	private Map<String, IOrienteerModule> registeredModules = new LinkedHashMap<String, IOrienteerModule>();
	
	@Inject
	public OrienteerWebApplication(@Named("orienteer.production") boolean production, @Named("orientdb.embedded") boolean embedded)
	{
		setConfigurationType(production?RuntimeConfigurationType.DEPLOYMENT:RuntimeConfigurationType.DEVELOPMENT);
		this.embedded = embedded;
	}
	
	@Inject
	@Override
	public void setOrientDbSettings(IOrientDbSettings orientDbSettings) {
		super.setOrientDbSettings(orientDbSettings);
	}
	
	public static OrienteerWebApplication get()
    {
        return (OrienteerWebApplication) WebApplication.get();
    }
	
	public static OrienteerWebApplication lookupApplication()
	{
		return lookupApplication(OrienteerWebApplication.class);
	}

	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return ListOClassesPage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		if(embedded)
		{
			getApplicationListeners().add(new EmbeddOrientDbApplicationListener(StartStandalone.class.getResource("db.config.xml"))
			{

				@Override
				public void onAfterServerStartupAndActivation(OrientDbWebApplication app)
						throws Exception {
					IOrientDbSettings settings = app.getOrientDbSettings();
					ODatabaseDocumentTx db = new ODatabaseDocumentTx(settings.getDBUrl());
					if(!db.exists()) db = db.create();
					if(db.isClosed()) db.open(settings.getDBInstallatorUserName(), settings.getDBInstallatorUserPassword());
					db.getMetadata().load();
					db.close();
				}
				
			});
		}
		super.init();
		new AnnotatedMountScanner().scanPackage("ru.ydn.orienteer.web").mount(this);
		getMarkupSettings().setStripWicketTags(true);
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		getApplicationListeners().add(new ModuledDataInstallator());
		registerModule(new OrienteerLocalizationModule());
		registerModule(new UpdateDefaultSchemaModule());
		registerModule(new PerspectivesModule());
		getOrientDbSettings().getORecordHooks().add(new CalculablePropertiesHook());
		getOrientDbSettings().getORecordHooks().add(new ReferencesConsistencyHook());
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}
	
	public Injector getInjector()
	{
		return getMetaData(GuiceInjectorHolder.INJECTOR_KEY).getInjector();
	}
	
	public <T> T getServiceInstance(Class<T> serviceType)
	{
		return getInjector().getInstance(serviceType);
	}
	
	public ODatabaseDocument getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}

	public Map<String, IOrienteerModule> getRegisteredModules() {
		return registeredModules;
	}
	
	public void registerModule(IOrienteerModule module)
	{
		registeredModules.put(module.getName(), module);
	}
	
	public IOrienteerModule getModuleByName(String name)
	{
		return registeredModules.get(name);
	}
	
	public UIComponentsRegistry getUIComponentsRegistry()
	{
		return getServiceInstance(UIComponentsRegistry.class);
	}
	
}
