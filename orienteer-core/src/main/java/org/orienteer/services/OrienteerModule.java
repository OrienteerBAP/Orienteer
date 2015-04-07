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
import org.orienteer.utils.LookupResourceHelper;
import org.orienteer.utils.LookupResourceHelper.DirFileLookuper;
import org.orienteer.utils.LookupResourceHelper.IResourceLookuper;
import org.orienteer.utils.LookupResourceHelper.SystemPropertyFileLookuper;
import org.orienteer.utils.LookupResourceHelper.SystemPropertyResourceLookuper;
import org.orienteer.utils.LookupResourceHelper.SystemPropertyURLLookuper;
import org.orienteer.utils.LookupResourceHelper.UpDirectoriesFileLookuper;
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
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.server.OServer;

import de.agilecoders.wicket.webjars.settings.IWebjarsSettings;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileSystems;

/**
 * Main module to load Orienteer stuff to Guice
 * 
 * <h1>Properties</h1>
 * Properties can be retrieved from both files from the local filesystem and
 * files on the Java classpath. The path of the file and the resource can be
 * specified by setting system properties
 * ({@link #PROPERTIES_FILE_NAME_PROPERTY_NAME} and
 * {@link #PROPERTIES_RESOURCE_NAME_PROPERTY_NAME}). If none is specified the
 * default values {@link #PROPERTIES_FILE_PATH_DEFAULT} and
 * {@link #PROPERTIES_RESOURCE_PATH_DEFAULT} are used. If both are specified the
 * property for the resource file takes precedence over the one for the file
 * path (this allows applications to overwrite properties without bothering the
 * user). Default properties are specified in the
 * {@link #PROPERTIES_RESOURCE_PATH_DEFAULT} properties file and used if not
 * explicitly overwritten in one of the specifications described above.
 *
 * @author richter
 */
public class OrienteerModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(OrienteerModule.class);
	
	public static final String ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME = "orienteer.qualifier";
	public static final String DEFAULT_ORENTEER_PROPERTIES_QUALIFIER = "orienteer";
	
	public static final String PROPERTIES_RESOURCE_PATH_SYSTEM_DEFAULT = "orienteer-default.properties";
	
	
	public final static Properties PROPERTIES_DEFAULT = new Properties();

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
	
	public static Properties retrieveProperties(String qualifier) {
		String identification = qualifier+".properties";
		URL propertiesURL = STACK_LOOKUPER.lookup(identification);
		if(propertiesURL==null) return null;
		Properties ret = new Properties();
		try {
			ret.load(propertiesURL.openStream());
			return ret;
		} catch (IOException e) {
			LOG.error("Can't read from properties file '"+propertiesURL+"' for qualifier '"+qualifier+"'", e);
			return null;
		}
	}

	
}
