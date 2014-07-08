package ru.ydn.orienteer;

import org.apache.wicket.guice.GuiceInjectorHolder;
import org.apache.wicket.markup.html.WebPage;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import ru.ydn.orienteer.web.LoginPage;
import ru.ydn.orienteer.web.schema.ListClassesPage;
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

	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return ListClassesPage.class;
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
	
}
