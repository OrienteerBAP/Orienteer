package org.orienteer.core.service;

import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Modules;
import org.apache.wicket.guice.GuiceWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.StartupPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import java.util.*;


/**
 * Main module to load properties and application to Guice
 * 
 * <h1>Properties</h1>
 * Properties can be retrieved from both files from the local filesystem and
 * files on the Java classpath. 
 * System property {@link StartupPropertiesLoader#ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME} defines
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
	
	public static final String ORIENTDB_KEY_PREFIX							= "orientdb.";
	
	private final Properties properties;
	
	public OrienteerInitModule(Properties properties) {
		this.properties = properties;
	}
	
	@Override
	protected void configureServlets() {
		Map<String, String> params = new HashMap<String, String>();    
        params.put(WicketFilter.FILTER_MAPPING_PARAM, "/*");  
        params.put("applicationFactoryClassName", GuiceWebApplicationFactory.class.getName());
        params.put("injectorContextAttribute", Injector.class.getName());
        bind(WicketFilter.class).in(Singleton.class);
        filter("/*").through(WicketFilter.class, params);  
		
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

		install(
		        loadFromClasspath(new OrienteerModule())
        );
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
     * <p>
     *  Load modules from classpath.
     * </p>
     * <p>
     *  For load modules {@link Module} from classpath uses {@link ServiceLoader}
     *  If present module with annotation {@link OverrideModule}, so will be used {@link Modules#override(Module...)}
     *  for override runtime bindings (bindings created in modules without annotation {@link OverrideModule})
     * </p>
     * @param initModules default modules for init
     * @return {@link Module} created by {@link Modules#combine(Module...)} or {@link Modules#override(Module...)} if need override module
     */
    public Module loadFromClasspath(Module... initModules) {
    	List<Module> allModules = new LinkedList<Module>();
        List<Module> runtime = new LinkedList<Module>();
        List<Module> overrides = new LinkedList<Module>();
        
        if(initModules != null && initModules.length > 0) {
            allModules.addAll(Arrays.asList(initModules));
        }

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
