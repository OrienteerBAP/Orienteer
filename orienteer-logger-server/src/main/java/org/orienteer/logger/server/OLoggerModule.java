package org.orienteer.logger.server;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.Lookup;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.logger.IOLoggerConfiguration;
import org.orienteer.logger.IOLoggerEventEnhancer;
import org.orienteer.logger.OLogger;
import org.orienteer.logger.OLoggerBuilder;
import org.orienteer.logger.impl.DefaultCorrelationIdGenerator;
import org.orienteer.logger.impl.DefaultOLoggerConfiguration;
import org.orienteer.logger.server.hook.OLoggerEventHook;
import org.orienteer.logger.server.model.IOCorrelationIdGeneratorModel;
import org.orienteer.logger.server.model.IOLoggerDAO;
import org.orienteer.logger.server.model.IOLoggerEventDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventFilteredDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventMailDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventModel;
import org.orienteer.logger.server.resource.OLoggerReceiverResource;
import org.orienteer.logger.server.service.OLoggerExceptionListener;
import org.orienteer.logger.server.service.correlation.OrienteerCorrelationIdGenerator;
import org.orienteer.logger.server.service.dispatcher.OLoggerEventDispatcher;
import org.orienteer.logger.server.service.enhancer.OSeedClassEnhancer;
import org.orienteer.logger.server.service.enhancer.OWebEnhancer;
import org.orienteer.mail.OMailModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.ProvidedBy;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IOrienteerModule} for 'orienteer-logger-server' module
 */
@Singleton
public class OLoggerModule extends AbstractOrienteerModule{


	private static final Logger LOG = LoggerFactory.getLogger(OLoggerModule.class);
	
	public static final String NAME = "orienteer-logger";

	public static final int VERSION = 12;

	public static final String DISPATCHER_DEFAULT = "default";

	public static final String CORRELATION_ID_GENERATOR_DEFAULT   = "default";
	public static final String CORRELATION_ID_GENERATOR_ORIENTEER = "orienteer";
	
	@Inject
	private IOLoggerDAO loggerDAO;

	protected OLoggerModule() {
		super(NAME, VERSION, OMailModule.NAME);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		installOLoggerEvent(helper);
		createDefaultOLoggerEventDispatcher(helper);
		createDefaultCorrelationIdGenerators(helper);

		return installModule(helper).getDocument();
	}

	private void installOLoggerEvent(OSchemaHelper helper) {
		DAO.describe(helper, IOLoggerEventModel.class, 
							 IOLoggerEventDispatcherModel.class,
							 IOLoggerEventFilteredDispatcherModel.class,
							 IOLoggerEventMailDispatcherModel.class,
							 IOCorrelationIdGeneratorModel.class);
	}

	private ILoggerModuleConfiguration installModule(OSchemaHelper helper) {
		DAO.describe(helper, ILoggerModuleConfiguration.class);


		IOLoggerEventDispatcherModel dispatcher = loggerDAO.getOLoggerEventDispatcher(DISPATCHER_DEFAULT);
		if(dispatcher==null) throw new IllegalStateException("There is no default dispatcher with alias: " + DISPATCHER_DEFAULT);

		IOCorrelationIdGeneratorModel correlationIdGenerator = loggerDAO.getOCorrelationIdGenerator(CORRELATION_ID_GENERATOR_ORIENTEER);
		if(correlationIdGenerator==null) throw new IllegalStateException("There is no orienteer correlation id generator with alias: " + CORRELATION_ID_GENERATOR_ORIENTEER);

		ILoggerModuleConfiguration module = DAO.create(ILoggerModuleConfiguration.class);
		if(module.lookup(NAME)==null) {
			module.getDocument().field(AbstractOrienteerModule.OMODULE_NAME, NAME);
			module.getDocument().field(OMODULE_ACTIVATE, false);
			module.setLoggerEnhancers(Arrays.asList(OWebEnhancer.class.getName(), OSeedClassEnhancer.class.getName()));
			module.setLoggerEventDispatcher(dispatcher);
			module.setCorrelationIdGenerator(correlationIdGenerator);
			module.setDomain("http://localhost:8080");
			module.save();
		}
		return module;
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
		super.onUpdate(app, db, oldVersion, newVersion);
		onInstall(app, db);
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
		super.onInitialize(app, db);
		LOG.info("Initialize OLoggerModule");
		installOLogger(app, DAO.provide(ILoggerModuleConfiguration.class, moduleDoc));
		app.mountPackage("org.orienteer.inclogger.web");
		OLoggerReceiverResource.mount(app);
		app.getRequestCycleListeners().add(new OLoggerExceptionListener());

		app.getOrientDbSettings().addORecordHooks(OLoggerEventHook.class);
	}
	
	@Override
	public void onConfigurationChange(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
		super.onConfigurationChange(app, db, moduleDoc);
		installOLogger(app, DAO.provide(ILoggerModuleConfiguration.class, moduleDoc));
	}
	
	private void installOLogger(OrienteerWebApplication app, ILoggerModuleConfiguration module) {
		IOLoggerConfiguration config = new DefaultOLoggerConfiguration();
		config.setApplicationName(app.getResourceSettings().getLocalizer().getString("application.name", null));

		//If collector URL was not overwriten from system properties: lets get it from module
		if (Strings.isEmpty(config.getCollectorUrl())) {
			config.setCollectorUrl(module.getCollectorUrl());
		}

		if (module.getCorrelationIdGenerator() != null) {
			config.setCorrelationIdGenerator(module.getCorrelationIdGenerator().createCorrelationIdGenerator());
		}

		OLoggerBuilder builder = new OLoggerBuilder();
		builder.setLoggerEventDispatcher(module.getLoggerEventDispatcher().createDispatcherClassInstance());
		module.getLoggerEnhancersInstances().forEach(builder::addEnhancer);
		builder.addDefaultEnhancers();

		OLogger.set(builder.create(config));
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		super.onDestroy(app, db);
		OLogger.set(null);
		app.unmountPackage("org.orienteer.inclogger.web");
		OLoggerReceiverResource.unmount(app);

		app.getOrientDbSettings().removeORecordHooks(OLoggerEventHook.class);
	}

	private void createDefaultOLoggerEventDispatcher(OSchemaHelper helper) {
		
		if(loggerDAO.getOLoggerEventDispatcher(DISPATCHER_DEFAULT)==null) {
			DAO.create(IOLoggerEventDispatcherModel.class)
							.setAlias(DISPATCHER_DEFAULT)
							.setName(CommonUtils.getLocalizedStrings("logger.event.dispatcher.default.name"))
							.setDispatcherClass(OLoggerEventDispatcher.class.getName())
							.save();
		}
	}

	private void createDefaultCorrelationIdGenerators(OSchemaHelper helper) {
		
		if(loggerDAO.getOCorrelationIdGenerator(CORRELATION_ID_GENERATOR_DEFAULT)==null) {
			DAO.create(IOCorrelationIdGeneratorModel.class)
							.setAlias(CORRELATION_ID_GENERATOR_DEFAULT)
							.setName(CommonUtils.getLocalizedStrings("logger.event.correlation.id.generator.default.name"))
							.setCorrelationClassName(DefaultCorrelationIdGenerator.class.getName())
							.save();
		}
		
		if(loggerDAO.getOCorrelationIdGenerator(CORRELATION_ID_GENERATOR_ORIENTEER)==null) {
			DAO.create(IOCorrelationIdGeneratorModel.class)
							.setAlias(CORRELATION_ID_GENERATOR_ORIENTEER)
							.setName(CommonUtils.getLocalizedStrings("logger.event.correlation.id.generator.orienteer.name"))
							.setCorrelationClassName(OrienteerCorrelationIdGenerator.class.getName())
							.save();
		}
		
	}
	
	/**
	 * Wrapper for module {@link OLoggerModule}
	 */
	@ProvidedBy(ODocumentWrapperProvider.class)
	@DAOOClass(value = ILoggerModuleConfiguration.CLASS_NAME, superClasses = {OMODULE_CLASS}, orderOffset = 50,
						domain = OClassDomain.SPECIFICATION)
	public static interface ILoggerModuleConfiguration extends IODocumentWrapper {

		public static final String CLASS_NAME = "OLoggerModule";

		public String getCollectorUrl();
		public ILoggerModuleConfiguration setCollectorUrl(String url);

		@DAOField(notNull = true)
		public IOLoggerEventDispatcherModel getLoggerEventDispatcher();
		public ILoggerModuleConfiguration setLoggerEventDispatcher(IOLoggerEventDispatcherModel dispatcher);

		@DAOField(notNull = true)
		public List<String> getLoggerEnhancers();
		public ILoggerModuleConfiguration setLoggerEnhancers(List<String> enhancers);

		public default boolean isActivated() {
			Boolean activated = getDocument().field(OLoggerModule.OMODULE_ACTIVATE);
			return activated != null && activated;
		}

		public default ILoggerModuleConfiguration setActivated(boolean activated) {
			getDocument().field(OLoggerModule.OMODULE_ACTIVATE, activated);
			return this;
		}

		public String getDomain();
        public ILoggerModuleConfiguration setDomain(String domain);

        @DAOField(notNull = true)
        public IOCorrelationIdGeneratorModel getCorrelationIdGenerator();
        public ILoggerModuleConfiguration setCorrelationIdGenerator(IOCorrelationIdGeneratorModel correlationIdGenerator);


		public default List<IOLoggerEventEnhancer> getLoggerEnhancersInstances() {
			return getLoggerEnhancers().stream()
					.map(className -> (IOLoggerEventEnhancer)WicketObjects.newInstance(className))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		
		@Lookup("select from "+ILoggerModuleConfiguration.CLASS_NAME+" where name = :name")
		public ILoggerModuleConfiguration lookup(String name);
		
		public static ILoggerModuleConfiguration get() {
			return DAO.create(ILoggerModuleConfiguration.class).lookup(NAME);
		}

	}
	
}
