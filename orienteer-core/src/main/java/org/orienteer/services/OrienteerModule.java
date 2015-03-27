package org.orienteer.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.wicket.Localizer;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.string.Strings;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.components.properties.UIVisualizersRegistry;
import org.orienteer.services.impl.GuiceOrientDbSettings;
import org.orienteer.services.impl.OClassIntrospector;
import org.orienteer.services.impl.OrienteerWebjarsSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.server.OServer;

import de.agilecoders.wicket.webjars.settings.IWebjarsSettings;

public class OrienteerModule extends AbstractModule
{
	public static final String PROPERTIES_FILE_NAME = "orienteer.properties";
	private static final Logger LOG = LoggerFactory.getLogger(OrienteerModule.class);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		final Properties properties = new Properties();
		try
		{
			URL propertiesURL = lookupPropertiesURL();
			if(propertiesURL==null) throw new ProvisionException("Properties files was not found");
			else properties.load(propertiesURL.openStream());
			LOG.info("Loading Orienteer properties from '"+propertiesURL+"'");
		} catch (FileNotFoundException e)
		{
			throw new ProvisionException("Properties files was not found", e);
		} catch (IOException e)
		{
			throw new ProvisionException("Properties files can't be read", e);
		}
		Names.bindProperties(binder(), properties);
		String applicationClass = properties.getProperty("orienteer.application");
		Class<? extends OrienteerWebApplication> appClass = OrienteerWebApplication.class;
		if(applicationClass!=null)
		{
			try
			{
				Class<?> customAppClass = Class.forName(applicationClass);
				
				if(OrienteerWebApplication.class.isAssignableFrom(appClass))
				{
					appClass = (Class<? extends OrienteerWebApplication>) customAppClass;
				}
				else
				{
					LOG.error("Orienteer application class '"+applicationClass+"' is not child class of '"+OrienteerWebApplication.class+"'. Using default.");
				}
			} catch (ClassNotFoundException e)
			{
				LOG.error("Orienteer application class '"+applicationClass+"' was not found. Using default.");
			}
		}
		bind(appClass).asEagerSingleton();
		Provider<? extends OrienteerWebApplication> appProvider = binder().getProvider(appClass);
		if(!OrienteerWebApplication.class.equals(appClass))
		{
			bind(OrienteerWebApplication.class).toProvider(appProvider);
		}
		bind(OrientDbWebApplication.class).toProvider(appProvider);
		bind(WebApplication.class).toProvider(appProvider);
		
		bind(Properties.class).annotatedWith(Orienteer.class).toInstance(properties);
		bind(IOrientDbSettings.class).to(GuiceOrientDbSettings.class);
		bind(IOClassIntrospector.class).to(OClassIntrospector.class);
		bind(UIVisualizersRegistry.class).asEagerSingleton();
		bind(IWebjarsSettings.class).to(OrienteerWebjarsSettings.class).asEagerSingleton();
	}
	
	@Provides
	public ODatabaseDocument getDatabaseRecord()
	{
		return DefaultODatabaseThreadLocalFactory.castToODatabaseDocument(ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner());
	}
	
	@Provides
	public OSchema getSchema(ODatabaseDocument db)
	{
		return db.getMetadata().getSchema();
	}
	
	@Provides
	public OServer getOServer(WebApplication application)
	{
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		return app.getServer();
	}
	
	@Provides
	public Localizer getLocalizer(WebApplication application)
	{
		return application.getResourceSettings().getLocalizer();
	}
	
	@Provides
	@Named("version")
	@Singleton
	public String getVersion()
	{
		String version = getClass().getPackage().getImplementationVersion();
		return version!=null?version:"";
	}
	
	public static URL lookupPropertiesURL() throws IOException
	{
		return lookupFile(PROPERTIES_FILE_NAME);
	}
	
	public static URL lookupFile(String fileName) throws IOException
	{
		return lookupFile(fileName, fileName);
	}
	
	
	public static URL lookupFile(String fileNameProperty, String fileName) throws IOException
	{
		String configFile = fileNameProperty!=null?System.getProperty(fileNameProperty):fileNameProperty;
		if(!Strings.isEmpty(configFile))
		{
			File file = new File(configFile);
			if(file.exists())
			{
				return file.toURI().toURL();
			}
			else
			{
				URL url = OrienteerWebApplication.class.getClassLoader().getResource(configFile);
				if(url!=null) return url;
				else return new URL(configFile);
			}
		}
		else
		{
			File file = new File(fileName);
			File dir = new File("").getAbsoluteFile();
			while(!file.exists() && dir!=null)
			{
				dir = dir.getParentFile();
				file = new File(dir, fileName);
			}
			return file!=null && file.exists() ?file.toURI().toURL():null;
		}
	}
	
}
