package ru.ydn.orienteer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.guice.GuiceInjectorHolder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import ru.ydn.orienteer.components.properties.UIComponentsRegistry;
import ru.ydn.orienteer.modules.IOrienteerModule;
import ru.ydn.orienteer.modules.ModuledDataInstallator;
import ru.ydn.orienteer.web.LoginPage;
import ru.ydn.orienteer.web.schema.ListOClassesPage;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

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
	public OrienteerWebApplication(@Named("orientdb.embedded") boolean embedded)
	{
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
		super.init();
		if(embedded)
		{
			getApplicationListeners().add(new EmbeddOrientDbApplicationListener(OrienteerWebApplication.class.getResource("db.config.xml")));
		}
		new AnnotatedMountScanner().scanPackage("ru.ydn.orienteer.web").mount(this);
		getMarkupSettings().setStripWicketTags(true);
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		getApplicationListeners().add(new ModuledDataInstallator());
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
	
	public ODatabaseRecord getDatabase()
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
