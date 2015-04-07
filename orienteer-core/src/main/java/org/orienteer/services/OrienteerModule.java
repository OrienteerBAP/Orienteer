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

	public static final String PROPERTIES_FILE_NAME_PROPERTY_NAME = "orienteer.file.properties";
	public static final String PROPERTIES_RESOURCE_NAME_PROPERTY_NAME = "orienteer.resource.property";
	public static final String CONFIG_DIR_PARENT_PATH_DEFAULT = System.getProperty("user.home");
	public static final String CONFIG_DIR_NAME_DEAFULT = ".orienteer";
	public static final String PROPERTIES_FILE_NAME_DEFAULT = "orienteer.properties";
	public static final String PROPERTIES_FILE_PATH_DEFAULT = FileSystems.getDefault().getPath(CONFIG_DIR_PARENT_PATH_DEFAULT, CONFIG_DIR_NAME_DEAFULT, PROPERTIES_FILE_NAME_DEFAULT).toString();
	public static final String PROPERTIES_RESOURCE_PATH_DEFAULT = "archetype-resources/orienteer.properties";
	private static final Logger LOG = LoggerFactory.getLogger(OrienteerModule.class);
	public final static Properties PROPERTIES_DEFAULT = new Properties();

	static {
		String resourcePath = "archetype-resources/orienteer.properties";
		InputStream propertiesDefaultInputStream
			= Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
		try {
			PROPERTIES_DEFAULT.load(propertiesDefaultInputStream);
		} catch (IOException ex) {
			LOG.error(String.format("orienteer-resources or some other artefact "
				+ "needs to provide '%s'", resourcePath), ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public OrienteerModule() {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		Properties properties = new Properties();
		try {
			properties = retrieveProperties();
			LOG.info("Loading Orienteer properties");
		} catch (FileNotFoundException e) {
			throw new ProvisionException("Properties files was not found", e);
		} catch (IOException e) {
			throw new ProvisionException("Properties files can't be read", e);
		}
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

	/**
	 * retrieves Properties as described in {@link OrienteerModule}.
	 * Behavior is controlled by system properties.
	 *
	 * @return
	 * @throws IOException
	 */
	/*
	 internal implementation notes:
	 - control by system properties is not too elegant, but keeps things as
	 simple as possible (KISS) (implement function arguments if necessary)
	 */
	public static Properties retrieveProperties() throws IOException {
		URL lookup = lookupFile(PROPERTIES_FILE_NAME_PROPERTY_NAME, PROPERTIES_RESOURCE_NAME_PROPERTY_NAME);
		if (lookup != null) {
			Properties retValue = new Properties();
			retValue.load(lookup.openStream());
			for (String key : PROPERTIES_DEFAULT.stringPropertyNames()) {
				if (!retValue.containsKey(key)) {
					retValue.setProperty(key, PROPERTIES_DEFAULT.getProperty(key));
				}
			}
			return retValue;
		}

		LOG.info(String.format("using built-in default properties", PROPERTIES_DEFAULT));
		Properties loadedProperties = new Properties(); //loading
		//default properties doesn't make sense because they're
		//overwritten at load (no idea why there's a way to
		//specify default properties anyway...)
		File configDirParent = new File(CONFIG_DIR_PARENT_PATH_DEFAULT);
		if (!configDirParent.exists()) {
			if (!configDirParent.mkdirs()) {
				throw new RuntimeException(String.format("Creation of configuration directory parent '%s' failed", configDirParent.getAbsolutePath()));
			}
		}
		File configDir = new File(configDirParent, CONFIG_DIR_NAME_DEAFULT);
		if (!configDir.exists()) {
			if (!configDir.mkdir()) {
				throw new RuntimeException(String.format("Creation of configuration directory '%s' failed", configDir.getAbsolutePath()));
			}
		}
		File propertiesFile = new File(configDir, PROPERTIES_FILE_NAME_DEFAULT);
		if (!propertiesFile.exists()) {
			//simply create the file in order to facilitate the
			//creation of the user; default values are specified
			//programmatically
			if (!propertiesFile.createNewFile()) {
				throw new RuntimeException(String.format("Creation of properties file '%s' failed", propertiesFile.getAbsolutePath()));
			}
			LOG.info(String.format("creating inexisting default configuration file '%s'", propertiesFile.getAbsolutePath()));
			InputStream propertiesFileInputStream
				= new FileInputStream(propertiesFile);
			loadedProperties.load(propertiesFileInputStream);
		}
		for (String key : PROPERTIES_DEFAULT.stringPropertyNames()) {
			if (!loadedProperties.containsKey(key)) {
				loadedProperties.setProperty(key, PROPERTIES_DEFAULT.getProperty(key));
			}
		}
		return loadedProperties;
	}

	/**
	 * tries to find properties file to load. Idea is following:
	 * <ol><li>Check system property for directly specified filename. If the
	 * file exists: use that file and return its URL</li>
	 * <li>Otherwise it looks for a properties file in the concatenation of
	 * {@link #CONFIG_DIR_PARENT_PATH_DEFAULT}, {@link #CONFIG_DIR_NAME_DEAFULT}
	 * and {@link #PROPERTIES_FILE_NAME_DEFAULT} ("default config file"). If
	 * the file exists: use it and return its URL.</li>
	 * <li>Create the "default config file" (see above) with default values
	 * and return its URL.</li>
	 * </ol>
	 *
	 * @param propertyFileNamePropertyName the name of the property to use
	 * for search for properties file in filesystem
	 * @param propertyResourceNamePropertyName the name of the property to
	 * user for search for properties resource on classpath
	 * @return
	 * @throws IOException
	 */
	/*
	 internal implementation notes:
	 - it's not nice to look for a properties/configuration file in the 
	 current directory, so seach it in a designated location (e.g. relative
	 to the home directory) only and create such a file if it isn't present
	 with default values and use such.
	 */
	private static URL lookupFile(String propertyFileNamePropertyName, String propertyResourceNamePropertyName) throws IOException {
		if (Strings.isEmpty(propertyFileNamePropertyName)) {
			// there's no sense in allowing an invalid property name
			throw new IllegalArgumentException("propertyFileNamePropertyName mustn't be null or empty");
		}
		String systemProperty = System.getProperty(propertyFileNamePropertyName);
		if (!Strings.isEmpty(systemProperty)) {
			File file = new File(systemProperty);
			if (file.exists()) {
				LOG.info(String.format("using existing properties file '%s'", file.getAbsolutePath()));
				return file.toURI().toURL();
			}
		} else {
			LOG.info(String.format("properties file name specified by system property '%s' is null or empty, skipping", propertyFileNamePropertyName));
		}

		String resourceProperty = System.getProperty(propertyResourceNamePropertyName);
		if (!Strings.isEmpty(resourceProperty)) {
			URL propertiesFileResource = Thread.currentThread().getContextClassLoader().getResource(resourceProperty);
			if (propertiesFileResource != null) {
				LOG.info(String.format("using existing properties resource '%s'", propertiesFileResource.toString()));
				return propertiesFileResource;
			} else {
				LOG.info(String.format("properties resource specified by system property '%s' is null or empty, skipping", propertyResourceNamePropertyName));
			}
		}
		return null;
	}
}
