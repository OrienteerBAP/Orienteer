package org.orienteer.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.core.request.mapper.HomePageMapper;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.guice.GuiceInjectorHolder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.settings.RequestCycleSettings;
import org.orienteer.core.component.meta.WicketPropertyResolver;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.hook.CalculablePropertiesHook;
import org.orienteer.core.hook.CallbackHook;
import org.orienteer.core.hook.ReferencesConsistencyHook;
import org.orienteer.core.module.*;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.web.BasePage;
import org.orienteer.core.web.HomePage;
import org.orienteer.core.web.LoginPage;
import org.orienteer.core.web.UnauthorizedPage;
import org.orienteer.core.widget.IWidgetTypesRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.security.OUser;

import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.IWebjarsSettings;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.LazyAuthorizationRequestCycleListener;
import ru.ydn.wicket.wicketorientdb.OrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Main {@link WebApplication} for Orienteer bases applications
 */
public class OrienteerWebApplication extends OrientDbWebApplication
{
	private static final Logger LOG = LoggerFactory.getLogger(OrienteerWebApplication.class);
	
	public static final DateConverter DATE_CONVERTER = new StyleDateConverter("M-", true);
	public static final DateConverter DATE_TIME_CONVERTER = new StyleDateConverter("MM", true);
	
	private LinkedHashMap<String, IOrienteerModule> registeredModules = new LinkedHashMap<String, IOrienteerModule>();
	private boolean registeredModulesSorted = false;
	
	@Inject
	private IWebjarsSettings webjarSettings;
	
	@Inject
	@Named("orientdb.embedded")
	private boolean embedded;

	@Inject
	@Named("orienteer.authenticatelazy")
	private boolean authenticateLazy;
	
	@Inject(optional=true)
	@Named("wicket.render.strategy")
	private RequestCycleSettings.RenderStrategy renderStrategy;

	@Inject
	@Named("orienteer.image.logo")
	private String imageLogoPath;

	
	@Inject(optional=true)
	public OrienteerWebApplication setConfigurationType(@Named("orienteer.production") boolean production) {
		setConfigurationType(production?RuntimeConfigurationType.DEPLOYMENT:RuntimeConfigurationType.DEVELOPMENT);
		return this;
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
		return HomePage.class;
	}
	
	@Override
	protected Class<? extends OrienteerWebSession> getWebSessionClass()
	{
		return OrienteerWebSession.class;
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
			getApplicationListeners().add(new EmbeddOrientDbApplicationListener(OrienteerWebApplication.class.getResource("db.config.xml"))
			{

				@Override
				public void onAfterServerStartupAndActivation(OrientDbWebApplication app)
						throws Exception {
					IOrientDbSettings settings = app.getOrientDbSettings();
					ODatabaseDocumentTx db = new ODatabaseDocumentTx(settings.getDBUrl());
					if(!db.exists()) {
						db = db.create();
						onDbCreated(db, settings);
					}
					if(db.isClosed()) db.open(settings.getAdminUserName(), settings.getAdminPassword());
					db.getMetadata().load();
					db.close();
				}
				
				private void onDbCreated(ODatabaseDocumentTx db, IOrientDbSettings settings) {
					if(OrientDbSettings.ADMIN_DEFAULT_USERNAME.equals(settings.getAdminUserName()) 
							&& !OrientDbSettings.ADMIN_DEFAULT_PASSWORD.equals(settings.getAdminPassword())) {
						OUser admin = db.getMetadata().getSecurity().getUser(OrientDbSettings.ADMIN_DEFAULT_USERNAME);
						admin.setPassword(settings.getAdminPassword());
						admin.save();
					}
					if(OrientDbSettings.READER_DEFAULT_USERNAME.equals(settings.getGuestUserName()) 
							&& !OrientDbSettings.READER_DEFAULT_PASSWORD.equals(settings.getGuestPassword())) {
						OUser reader = db.getMetadata().getSecurity().getUser(OrientDbSettings.READER_DEFAULT_USERNAME);
						reader.setPassword(settings.getGuestPassword());
						reader.save();
					}
					
				}
				
			});
		}
		WicketWebjars.install(this, webjarSettings);
		mountPages("org.orienteer.core.web");
		getResourceBundles().addCssBundle(BasePage.class, "orienteer.css", BasePage.SB_ADMIN_CSS, BasePage.ORIENTEER_CSS);
		mountResource("logo.png", new SharedResourceReference(imageLogoPath));
		getMarkupSettings().setStripWicketTags(true);
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		getApplicationListeners().add(new ModuledDataInstallator());
		getPageSettings().addComponentResolver(new WicketPropertyResolver());
		//Remove default BookmarkableMapper to disallow direct accessing of pages through /wicket/bookmarkable/<class>
		for(IRequestMapper mapper : getRootRequestMapperAsCompound()){
			if(mapper instanceof BookmarkableMapper) {
				getRootRequestMapperAsCompound().remove(mapper);
				break;
			}
		}
		registerModule(OrienteerLocalizationModule.class);
		registerModule(UpdateDefaultSchemaModule.class);
		registerModule(PerspectivesModule.class);
		registerModule(OWidgetsModule.class);
		registerModule(UserOnlineModule.class);
		registerModule(TaskManagerModule.class);
		getOrientDbSettings().getORecordHooks().add(CalculablePropertiesHook.class);
		getOrientDbSettings().getORecordHooks().add(ReferencesConsistencyHook.class);
		getOrientDbSettings().getORecordHooks().add(CallbackHook.class);
		mountOrientDbRestApi();
		if(authenticateLazy) getRequestCycleListeners().add(new LazyAuthorizationRequestCycleListener());
		registerWidgets("org.orienteer.core.component.widget");
		if(renderStrategy!=null) getRequestCycleSettings().setRenderStrategy(renderStrategy);
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

	public synchronized List<IOrienteerModule> getRegisteredModules() {
		if(!registeredModulesSorted){
			LinkedHashMap<String, IOrienteerModule> sorted = new LinkedHashMap<String, IOrienteerModule>();
			LinkedHashMap<String, IOrienteerModule> unsorted = new LinkedHashMap<>(registeredModules);
			Set<String> toRemove = new HashSet<>();
			while(!unsorted.isEmpty()) {
				for (Map.Entry<String, IOrienteerModule> entry : unsorted.entrySet()) {
					Set<String> dependencies = entry.getValue().getDependencies();
					if(dependencies==null || dependencies.isEmpty() || sorted.keySet().containsAll(dependencies)) {
						sorted.put(entry.getKey(), entry.getValue());
						toRemove.add(entry.getKey());
					}
				}
				if(!toRemove.isEmpty()) {
					for (String keyToRemove : toRemove) {
						unsorted.remove(keyToRemove);
					}
				} else {
					LOG.error("Modules without satisfied dependencies: "+unsorted.keySet());
					sorted.putAll(unsorted);
					break;
				}
			}
			registeredModules = sorted;
			registeredModulesSorted = true;
			
		}
		return new ArrayList<>(registeredModules.values());
	}
	
	public synchronized <M extends IOrienteerModule> M registerModule(Class<M> moduleClass)
	{
		M module = getServiceInstance(moduleClass);
		registeredModules.put(module.getName(), getServiceInstance(moduleClass));
		registeredModulesSorted = false;
		return module;
	}
	
	public IOrienteerModule getModuleByName(String name)
	{
		return registeredModules.get(name);
	}
	
	public UIVisualizersRegistry getUIVisualizersRegistry()
	{
		return getServiceInstance(UIVisualizersRegistry.class);
	}
	
	public IOClassIntrospector getOClassIntrospector()
	{
		return getServiceInstance(IOClassIntrospector.class);
	}
	
	public void mountPages(String packageName) {
		mountPages(packageName, OrienteerWebApplication.class.getClassLoader());
	}
	
	public void mountPages(String packageName, ClassLoader classLoader) {
		mountOrUnmountPages(packageName, classLoader, true);
	}

	public void unmountPages(String packageName) {
		unmountPages(packageName, OrienteerWebApplication.class.getClassLoader());
	}
	
	public void unmountPages(String packageName, ClassLoader classLoader) {
		mountOrUnmountPages(packageName, classLoader, false);
	}
	
	@Override
	public void unmount(String path) {
		//Lets do not try to unmount if shutting down. Related to WICKET-6157 issue. 
		//TODO Should be fixed in wicket 7.4.0. Once released: delete this method
		if(ThreadContext.exists()) super.unmount(path);
	}
	
	
	private void mountOrUnmountPages(String packageName, ClassLoader classLoader, boolean mount) {
		ClassPath classPath;
		try {
			classPath = ClassPath.from(classLoader);
		} catch (IOException e) {
			throw new WicketRuntimeException("Can't scan classpath", e);
		}
		//Lets do not try to unmount if shutting down. Related to WICKET-6157 issue.
		//TODO Should be fixed in wicket 7.4.0. Once released: delete the following line
		if(!mount && !ThreadContext.exists()) return;
		
		for(ClassInfo classInfo : classPath.getTopLevelClassesRecursive(packageName)) {
			Class<?> clazz = classInfo.load();
			MountPath mountPath = clazz.getAnnotation(MountPath.class);
			if(mountPath!=null) {
				if(!IRequestablePage.class.isAssignableFrom(clazz)) 
					throw new WicketRuntimeException("@"+MountPath.class.getSimpleName()+" should be only on pages");
				Class<? extends IRequestablePage> pageClass = (Class<? extends IRequestablePage>) clazz;
				String mainPath = mountPath.value();
				String[] alt = mountPath.alt();
				for(int i=alt.length-1;i>=-1;i--)
				{
					String path = i<0?mainPath:alt[i];
					if(mount) {
						if ("/".equals(path)) {
							mount(new HomePageMapper(pageClass));
						}
						mount(new MountedMapper(path, pageClass));
					} else {
						unmount(path);
					}
				}
			}
		}
	}
	
	public void registerWidgets(String packageName) {
		IWidgetTypesRegistry registry = getServiceInstance(IWidgetTypesRegistry.class);
		registry.register(packageName);
	}
	
	public void unregisterWidgets(String packageName) {
		IWidgetTypesRegistry registry = getServiceInstance(IWidgetTypesRegistry.class);
		registry.unregister(packageName);
	}
	
	@Override
	public void restartResponseAtSignInPage() {
		//This is required because home page is dynamic and depends on assigned perspective.
		if(RequestCycle.get().getRequest().getQueryParameters().getParameterValue(HomePage.FROM_HOME_PARAM).toBoolean(false)) {
			throw new RestartResponseException(getSignInPageClass());
		} else super.restartResponseAtSignInPage();
	}
	
	@Override
	protected void onUnauthorizedPage(Component page) {
		throw new RestartResponseException(UnauthorizedPage.class);
	}
}
