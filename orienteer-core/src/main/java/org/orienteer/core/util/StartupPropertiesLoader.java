package org.orienteer.core.util;

import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Class for startup loader properties
 */
public class StartupPropertiesLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(StartupPropertiesLoader.class);
	
	public static final String ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME = "orienteer.qualifier";
	public static final String DEFAULT_ORENTEER_PROPERTIES_QUALIFIER 		= "orienteer";
	public static final String PROPERTIES_RESOURCE_PATH_SYSTEM_DEFAULT 		= "orienteer-default.properties";
	
	
	
	public final static Properties PROPERTIES_DEFAULT = new Properties();


	private static final LookupResourceHelper.StackedResourceLookuper STACK_LOOKUPER = 
			new LookupResourceHelper.StackedResourceLookuper(LookupResourceHelper.SystemPropertyFileLookuper.INSTANCE,
															 LookupResourceHelper.SystemPropertyURLLookuper.INSTANCE,
															 LookupResourceHelper.UpDirectoriesFileLookuper.INSTANCE,
															 LookupResourceHelper.DirFileLookuper.CONFIG_DIR_INSTANCE,
															 new LookupResourceHelper.DirFileLookuper(getAppHome()),
															 LookupResourceHelper.SystemPropertyResourceLookuper.INSTANCE);
	
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

	private StartupPropertiesLoader() {}

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
				loadedProperties = retrieveSystemProperties(loadedProperties);
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
			loadedProperties = retrieveSystemProperties(loadedProperties);
			return loadedProperties;
		}
		else
		{
			LOG.info("Properties for qualifier '"+qualifier+"' was not found. Using embedded.");
			loadedProperties = retrieveSystemProperties(loadedProperties);
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

	private static Properties retrieveSystemProperties(Properties loadedProperties) {
		//Try to load from OS env 
		Map<String, String> osProperties = System.getenv();
		for(Map.Entry<String, String> entry : osProperties.entrySet()) {
			String env = entry.getKey().replace('_', '.');
			if(loadedProperties.containsKey(env)) loadedProperties.setProperty(env, entry.getValue());
			else {
				env = env.toLowerCase();
				if(loadedProperties.containsKey(env)) loadedProperties.setProperty(env, entry.getValue());
			}
		}
		//Load from Java system properties
		loadedProperties.putAll(System.getProperties());
		return loadedProperties;
	}

	public static String getAppHome() {
		String appHome = System.getenv("ORIENTEER_APP_HOME");
		return Strings.isEmpty(appHome) ? "./" : appHome;
	}
}
