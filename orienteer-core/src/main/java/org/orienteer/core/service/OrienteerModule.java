package org.orienteer.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.wicket.Localizer;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.service.impl.GuiceOrientDbSettings;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.service.impl.OrienteerWebjarsSettings;
import org.orienteer.core.util.LookupResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.server.OServer;

import de.agilecoders.wicket.webjars.settings.IWebjarsSettings;

/**
 * Main module to load Orienteer stuff to Guice
 * 
 * <h1>Properties</h1>
 * Properties can be retrieved from both files from the local filesystem and
 * files on the Java classpath. 
 * System property {@link #ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME} defines
 * qualifier which should be used in properties lookup.
 * Highlevel lookup:
 * <ol>
 * <li>If there is a qualifier - lookup by this qualifier</li>
 * <li>If there is no a qualifier - lookup by default qualifier 'orienteer'</li>
 * <li>If nothing was found - use embedded configuration</li> 
 * </ol>
 * Order of lookup for a specific qualifier (for example 'myapplication'):
 * <ol>
 * <li>lookup of file specified by system property 'myapplication.properties'</li>
 * <li>lookup of URL specified by system property 'myapplication.properties'</li>
 * <li>lookup of file 'myapplication.properties' up from current directory</li>
 * <li>lookup of file 'myapplication.properties' in '~/orienteer/' directory</li>
 * <li>lookup of resource 'myapplication.properties' in a classpath</li>
 * </ol>
 */
public class OrienteerModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(OrienteerModule.class);
	
	public static final String ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME = "orienteer.qualifier";
	public static final String DEFAULT_ORENTEER_PROPERTIES_QUALIFIER = "orienteer";
	
	public static final String PROPERTIES_RESOURCE_PATH_SYSTEM_DEFAULT = "orienteer-default.properties";
	
	
	public final static Properties PROPERTIES_DEFAULT = new Properties();
	
	public static final String ORIENTDB_KEY_PREFIX="orientdb.";

	static {
		InputStream propertiesDefaultInputStream
			= Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_RESOURCE_PATH_SYSTEM_DEFAULT);
		try {
			PROPERTIES_DEFAULT.load(propertiesDefaultInputStream);
		} catch (IOException ex) {
			LOG.error("Critical system resource '"+PROPERTIES_RESOURCE_PATH_SYSTEM_DEFAULT+"' was not found. Terminating. ");
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static final LookupResourceHelper.StackedResourceLookuper STACK_LOOKUPER = 
		new LookupResourceHelper.StackedResourceLookuper(LookupResourceHelper.SystemPropertyFileLookuper.INSTANCE,
														 LookupResourceHelper.SystemPropertyURLLookuper.INSTANCE,
														 LookupResourceHelper.UpDirectoriesFileLookuper.INSTANCE,
														 LookupResourceHelper.DirFileLookuper.CONFIG_DIR_INSTANCE,
														 LookupResourceHelper.SystemPropertyResourceLookuper.INSTANCE);

	public OrienteerModule() {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		Properties properties = retrieveProperties();
		Names.bindProperties(binder(), properties);
		bindOrientDbProperties(properties);
		String applicationClass = properties.getProperty("orienteer.application");
		Class<? extends OrienteerWebApplication> appClass = OrienteerWebApplication.class;
		if (applicationClass != null) {
			try {
				Class<?> customAppClass = Class.forName(applicationClass);

				if (OrienteerWebApplication.class.isAssignableFrom(appClass)) {
					appClass = (Class<? extends OrienteerWebApplication>) customAppClass;
				} else {
					LOG.error("Orienteer application class '" + applicationClass + "' is not child class of '" + OrienteerWebApplication.class + "'. Using default.");
				}
			} catch (ClassNotFoundException e) {
				LOG.error("Orienteer application class '" + applicationClass + "' was not found. Using default.");
			}
		}
		bind(appClass).asEagerSingleton();
		Provider<? extends OrienteerWebApplication> appProvider = binder().getProvider(appClass);
		if (!OrienteerWebApplication.class.equals(appClass)) {
			bind(OrienteerWebApplication.class).toProvider(appProvider);
		}
		bind(OrientDbWebApplication.class).toProvider(appProvider);
		bind(WebApplication.class).toProvider(appProvider);

		bind(Properties.class).annotatedWith(Orienteer.class).toInstance(properties);
		bind(IOrientDbSettings.class).to(GuiceOrientDbSettings.class).asEagerSingleton();
		bind(IOClassIntrospector.class).to(OClassIntrospector.class);
		bind(UIVisualizersRegistry.class).asEagerSingleton();
		bind(IWebjarsSettings.class).to(OrienteerWebjarsSettings.class).asEagerSingleton();
	}
	
	protected void bindOrientDbProperties(Properties properties) {
		for(String key : properties.stringPropertyNames()) {
			if(key.startsWith(ORIENTDB_KEY_PREFIX)) {
				String subKey = key.substring(ORIENTDB_KEY_PREFIX.length());
				System.setProperty(subKey, properties.getProperty(key));
			}
		}
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

	/**
	 * Retrieve startup properties 
	 * @return not null {@link Properties}
	 */
	public static Properties retrieveProperties() {
		Properties loadedProperties = new Properties();
		loadedProperties.putAll(PROPERTIES_DEFAULT);
		String qualifier = System.getProperty(ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME);
		if(!Strings.isEmpty(qualifier))
		{
			LOG.info("Orienteer startup properties qualifier: "+qualifier);
			Properties qualifierProperties = retrieveProperties(qualifier);
			if(qualifierProperties!=null)
			{
				loadedProperties.putAll(qualifierProperties);
				return loadedProperties;
			}
			else
			{
				LOG.info("Properties for qualifier '"+qualifier+"' was not found.");
			}
		}
		LOG.info("Using default orienteer startup properties qualifier");
		Properties defaultQualifierProperties = retrieveProperties(DEFAULT_ORENTEER_PROPERTIES_QUALIFIER);
		if(defaultQualifierProperties!=null)
		{
			loadedProperties.putAll(defaultQualifierProperties);
			return loadedProperties;
		}
		else
		{
			LOG.info("Properties for qualifier '"+qualifier+"' was not found. Using embedded.");
			return loadedProperties;
		}
		
	}
	
	/**
	 * Lookup {@link Properties} for a specified qualifier
	 * @param qualifier qualifier to be used during startup
	 * @return {@link Properties} for a qualifier or null
	 */
	public static Properties retrieveProperties(String qualifier) {
		String identification = qualifier+".properties";
		URL propertiesURL = STACK_LOOKUPER.lookup(identification);
		if(propertiesURL==null) return null;
		Properties ret = new Properties();
		try {
			ret.load(propertiesURL.openStream());
			LOG.info("Startup properties was loaded from '"+propertiesURL+"' for qualifier '"+qualifier+"'");
			return ret;
		} catch (IOException e) {
			LOG.error("Can't read from properties file '"+propertiesURL+"' for qualifier '"+qualifier+"'", e);
			return null;
		}
	}

	
}
