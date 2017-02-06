package org.orienteer.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import org.apache.wicket.guice.GuiceWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.LookupResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Modules;


/**
 * Main module to load properties and application to Guice
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
public class OrienteerInitModule extends ServletModule {
	
	private static final Logger LOG = LoggerFactory.getLogger(OrienteerInitModule.class);
	
	public static final String ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME = "orienteer.qualifier";
	public static final String DEFAULT_ORENTEER_PROPERTIES_QUALIFIER = "orienteer";
	
	public static final String PROPERTIES_RESOURCE_PATH_SYSTEM_DEFAULT = "orienteer-default.properties";
	
	public static final String ORIENTDB_KEY_PREFIX="orientdb.";
	
	
	public final static Properties PROPERTIES_DEFAULT = new Properties();


	private static final LookupResourceHelper.StackedResourceLookuper STACK_LOOKUPER = 
			new LookupResourceHelper.StackedResourceLookuper(LookupResourceHelper.SystemPropertyFileLookuper.INSTANCE,
															 LookupResourceHelper.SystemPropertyURLLookuper.INSTANCE,
															 LookupResourceHelper.UpDirectoriesFileLookuper.INSTANCE,
															 LookupResourceHelper.DirFileLookuper.CONFIG_DIR_INSTANCE,
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
	
	@Override
	protected void configureServlets() {
		Map<String, String> params = new HashMap<String, String>();    
        params.put(WicketFilter.FILTER_MAPPING_PARAM, "/*");  
        params.put("applicationFactoryClassName", GuiceWebApplicationFactory.class.getName());
        params.put("injectorContextAttribute", Injector.class.getName());
        bind(WicketFilter.class).in(Singleton.class);
        filter("/*").through(WicketFilter.class, params);  
        
        
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
        
        install(loadFromClasspath(new OrienteerModule()));
	}
	
	protected void bindOrientDbProperties(Properties properties) {
		for(String key : properties.stringPropertyNames()) {
			if(key.startsWith(ORIENTDB_KEY_PREFIX)) {
				String subKey = key.substring(ORIENTDB_KEY_PREFIX.length());
				System.setProperty(subKey, properties.getProperty(key));
			}
		}
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
	
    public Module loadFromClasspath(Module... initModules) {
    	List<Module> allModules = new LinkedList<Module>();
        List<Module> runtime = new LinkedList<Module>();
        List<Module> overrides = new LinkedList<Module>();
        
        if(initModules!=null && initModules.length>0) allModules.addAll(Arrays.asList(initModules));
        Iterables.addAll(allModules, ServiceLoader.load(Module.class));
        
        
        for (Module module : allModules) {
            if (module.getClass().isAnnotationPresent(OverrideModule.class))
                overrides.add(module);
            else
                runtime.add(module);
        }
        return overrides.isEmpty() ? Modules.combine(runtime) : Modules.override(runtime).with(overrides);
    }

}
