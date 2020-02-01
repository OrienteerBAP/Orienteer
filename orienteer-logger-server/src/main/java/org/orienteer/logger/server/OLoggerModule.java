package org.orienteer.logger.server;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
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
import org.orienteer.logger.server.model.*;
import org.orienteer.logger.server.repository.OLoggerRepository;
import org.orienteer.logger.server.resource.OLoggerReceiverResource;
import org.orienteer.logger.server.service.OLoggerExceptionListener;
import org.orienteer.logger.server.service.correlation.OrienteerCorrelationIdGenerator;
import org.orienteer.logger.server.service.dispatcher.IOLoggerEventDispatcherModelFactory;
import org.orienteer.logger.server.service.dispatcher.OLoggerEventDispatcher;
import org.orienteer.logger.server.service.enhancer.OSeedClassEnhancer;
import org.orienteer.logger.server.service.enhancer.OWebEnhancer;
import org.orienteer.logger.server.util.OLoggerServerUtils;
import org.orienteer.mail.OMailModule;
import org.orienteer.mail.model.OMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link IOrienteerModule} for 'orienteer-logger-server' module
 */
@Singleton
public class OLoggerModule extends AbstractOrienteerModule{


	private static final Logger LOG = LoggerFactory.getLogger(OLoggerModule.class);
	
	public static final String NAME = "orienteer-logger";

	public static final int VERSION = 11;

	public static final String DISPATCHER_DEFAULT = "default";

	public static final String CORRELATION_ID_GENERATOR_DEFAULT   = "default";
	public static final String CORRELATION_ID_GENERATOR_ORIENTEER = "orienteer";

	protected OLoggerModule() {
		super(NAME, VERSION, OMailModule.NAME);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		installOLoggerEvent(helper);
		createDefaultOLoggerEventDispatcher(helper);
		createDefaultCorrelationIdGenerators(helper);

		return installModule(helper);
	}

	private void installOLoggerEvent(OSchemaHelper helper) {
		helper.oClass(OLoggerEventModel.CLASS_NAME)
				.oProperty(OLoggerEventModel.PROP_EVENT_ID, OType.STRING, 10)
					.switchDisplayable(false)
					.notNull()
				.oProperty(OLoggerEventModel.PROP_APPLICATION, OType.STRING, 20)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_NODE_ID, OType.STRING, 30)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_CORRELATION_ID, OType.STRING, 40)
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_RESTRICTED_WIDTH)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_DATE_TIME, OType.DATETIME, 50)
					.markDisplayable()
					.markAsDocumentName()
				.oProperty(OLoggerEventModel.PROP_REMOTE_ADDRESS, OType.STRING, 60)
				.oProperty(OLoggerEventModel.PROP_HOST_NAME, OType.STRING, 70)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_USERNAME, OType.STRING, 80)
				.oProperty(OLoggerEventModel.PROP_CLIENT_URL, OType.STRING, 90)
                .oProperty(OLoggerEventModel.PROP_SEED_CLASS, OType.STRING, 100)
				.oProperty(OLoggerEventModel.PROP_SUMMARY, OType.STRING, 110)
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_RESTRICTED_WIDTH)
					.markDisplayable()
					.updateCustomAttribute(CustomAttribute.UI_READONLY, true)
				.oProperty(OLoggerEventModel.PROP_MESSAGE, OType.STRING, 120)
					.assignVisualization("code");


		helper.oClass(OLoggerEventDispatcherModel.CLASS_NAME)
				.oProperty(OLoggerEventDispatcherModel.PROP_NAME, OType.EMBEDDEDMAP, 0)
					.linkedType(OType.STRING)
					.notNull()
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
					.markAsDocumentName()
				.oProperty(OLoggerEventDispatcherModel.PROP_ALIAS, OType.STRING, 10)
					.notNull()
					.oIndex(OClass.INDEX_TYPE.UNIQUE)
				.oProperty(OLoggerEventDispatcherModel.PROP_DISPATCHER_CLASS, OType.STRING, 20)
					.notNull();

		helper.oClass(OLoggerEventFilteredDispatcherModel.CLASS_NAME, OLoggerEventDispatcherModel.CLASS_NAME)
				.oProperty(OLoggerEventFilteredDispatcherModel.PROP_EXCEPTIONS, OType.EMBEDDEDSET, 30)
					.linkedType(OType.STRING)
					.notNull();

		helper.oClass(OLoggerEventMailDispatcherModel.CLASS_NAME, OLoggerEventFilteredDispatcherModel.CLASS_NAME)
				.oProperty(OLoggerEventMailDispatcherModel.PROP_MAIL, OType.LINK, 40)
					.notNull()
					.linkedClass(OMail.CLASS_NAME)
				.oProperty(OLoggerEventMailDispatcherModel.PROP_RECIPIENTS, OType.EMBEDDEDSET, 50)
					.linkedType(OType.STRING)
					.notNull();

		helper.oClass(OCorrelationIdGeneratorModel.CLASS_NAME)
				.oProperty(OCorrelationIdGeneratorModel.PROP_NAME, OType.EMBEDDEDMAP, 0)
					.markAsDocumentName()
					.linkedType(OType.STRING)
					.assignTab(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
					.notNull()
				.oProperty(OCorrelationIdGeneratorModel.PROP_ALIAS, OType.STRING, 10)
					.notNull()
					.oIndex(OClass.INDEX_TYPE.UNIQUE)
				.oProperty(OCorrelationIdGeneratorModel.PROP_GENERATOR_CLASS, OType.STRING, 20)
					.notNull();
	}

	private ODocument installModule(OSchemaHelper helper) {
		helper.oClass(Module.CLASS_NAME, OMODULE_CLASS).domain(OClassDomain.SPECIFICATION)
				.oProperty(Module.PROP_COLLECTOR_URL, OType.STRING, 50)
				.oProperty(Module.PROP_LOGGER_ENHANCERS, OType.EMBEDDEDLIST, 60)
					.linkedType(OType.STRING)
					.notNull()
				.oProperty(Module.PROP_LOGGER_EVENT_DISPATCHER, OType.LINK, 70)
					.linkedClass(OLoggerEventDispatcherModel.CLASS_NAME)
					.notNull()
				.oProperty(Module.PROP_CORRELATION_ID_GENERATOR, OType.LINK, 80)
					.linkedClass(OCorrelationIdGeneratorModel.CLASS_NAME)
					.notNull()
                .oProperty(Module.PROP_DOMAIN, OType.STRING, 80);


		ODocument dispatcher = OLoggerRepository.getOLoggerEventDispatcherAsDocument(helper.getDatabase(), DISPATCHER_DEFAULT)
			.orElseThrow(() -> new IllegalStateException("There is no default dispatcher with alias: " + DISPATCHER_DEFAULT));

		ODocument correlationIdGenerator = OLoggerRepository.getOCorrelationIdGeneratorAsDocument(helper.getDatabase(), CORRELATION_ID_GENERATOR_ORIENTEER)
				.orElseThrow(() -> new IllegalStateException("There is no orienteer correlation id generator with alias: " + CORRELATION_ID_GENERATOR_ORIENTEER));

		return helper.oClass(Module.CLASS_NAME)
				.oDocument(AbstractOrienteerModule.OMODULE_NAME, NAME)
					.field(OMODULE_ACTIVATE, false)
					.field(Module.PROP_LOGGER_ENHANCERS, Arrays.asList(OWebEnhancer.class.getName(), OSeedClassEnhancer.class.getName()))
					.field(Module.PROP_LOGGER_EVENT_DISPATCHER, dispatcher)
					.field(Module.PROP_CORRELATION_ID_GENERATOR, correlationIdGenerator)
                    .field(Module.PROP_DOMAIN, "http://localhost:8080")
					.saveDocument()
				.getODocument();
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		super.onUpdate(app, db, oldVersion, newVersion);
		onInstall(app, db);
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		super.onInitialize(app, db);
		LOG.info("Initialize OLoggerModule");
		installOLogger(app, new Module(moduleDoc));
		app.mountPackage("org.orienteer.inclogger.web");
		OLoggerReceiverResource.mount(app);
		app.getRequestCycleListeners().add(new OLoggerExceptionListener());

		List<Class<? extends ORecordHook>> hooks = new LinkedList<>(app.getOrientDbSettings().getORecordHooks());
		hooks.add(OLoggerEventHook.class);
		app.getOrientDbSettings().setORecordHooks(hooks);
	}
	
	@Override
	public void onConfigurationChange(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		super.onConfigurationChange(app, db, moduleDoc);
		installOLogger(app, new Module(moduleDoc));
	}
	
	private void installOLogger(OrienteerWebApplication app, Module module) {
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
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		OLogger.set(null);
		app.unmountPackage("org.orienteer.inclogger.web");
		OLoggerReceiverResource.unmount(app);

		List<Class<? extends ORecordHook>> hooks = new LinkedList<>(app.getOrientDbSettings().getORecordHooks());
		hooks.remove(OLoggerEventHook.class);
		app.getOrientDbSettings().setORecordHooks(hooks);
	}

	private void createDefaultOLoggerEventDispatcher(OSchemaHelper helper) {
		helper.oClass(OLoggerEventDispatcherModel.CLASS_NAME);

		String name = new ResourceModel("logger.event.dispatcher.default.name").getObject();

		helper.oDocument(OLoggerEventDispatcherModel.PROP_ALIAS, DISPATCHER_DEFAULT)
				.field(OLoggerEventDispatcherModel.PROP_NAME, CommonUtils.toMap("en", name))
				.field(OLoggerEventDispatcherModel.PROP_DISPATCHER_CLASS, OLoggerEventDispatcher.class.getName())
				.saveDocument();
	}

	private void createDefaultCorrelationIdGenerators(OSchemaHelper helper) {
		helper.oClass(OCorrelationIdGeneratorModel.CLASS_NAME);

		String defaultName = new ResourceModel("logger.event.correlation.id.generator.default.name").getObject();

		helper.oDocument(OCorrelationIdGeneratorModel.PROP_ALIAS, CORRELATION_ID_GENERATOR_DEFAULT)
				.field(OCorrelationIdGeneratorModel.PROP_NAME, CommonUtils.toMap("en", defaultName))
				.field(OCorrelationIdGeneratorModel.PROP_GENERATOR_CLASS, DefaultCorrelationIdGenerator.class.getName())
				.saveDocument();

		String orienteerName = new ResourceModel("logger.event.correlation.id.generator.orienteer.name").getObject();

		helper.oDocument(OCorrelationIdGeneratorModel.PROP_ALIAS, CORRELATION_ID_GENERATOR_ORIENTEER)
				.field(OCorrelationIdGeneratorModel.PROP_NAME, CommonUtils.toMap("en", orienteerName))
				.field(OCorrelationIdGeneratorModel.PROP_GENERATOR_CLASS, OrienteerCorrelationIdGenerator.class.getName())
				.saveDocument();
	}

	/**
	 * Wrapper for module {@link OLoggerModule}
	 */
	public static class Module extends ODocumentWrapper {

		public static final String CLASS_NAME = "OLoggerModule";

		public static final String PROP_COLLECTOR_URL            = "collectorUrl";
		public static final String PROP_LOGGER_EVENT_DISPATCHER  = "loggerEventDispatcher";
		public static final String PROP_LOGGER_ENHANCERS         = "loggerEnhancers";
		public static final String PROP_CORRELATION_ID_GENERATOR = "correlationIdGenerator";
		public static final String PROP_DOMAIN                   = "domain";

		public Module() {
			super(CLASS_NAME);
		}

		public Module(ODocument doc) {
			super(doc);
		}

		public Module(String iClassName) {
			super(iClassName);
		}

		public String getCollectorUrl() {
			return document.field(PROP_COLLECTOR_URL);
		}

		public Module setCollectorUrl(String url) {
			document.field(PROP_COLLECTOR_URL, url);
			return this;
		}

		public OLoggerEventDispatcherModel getLoggerEventDispatcher() {
			ODocument dispatcher = getLoggerEventDispatcherAsDocument();
            IOLoggerEventDispatcherModelFactory factory = OLoggerServerUtils.getEventDispatcherModelFactory();
            return dispatcher != null ? factory.createEventDispatcherModel(dispatcher) : null;
		}

		public ODocument getLoggerEventDispatcherAsDocument() {
			OIdentifiable dispatcher = document.field(PROP_LOGGER_EVENT_DISPATCHER);
			return dispatcher != null ? dispatcher.getRecord() : null;
		}

		public Module setLoggerEventDispatcher(OLoggerEventDispatcherModel dispatcher) {
			return setLoggerEventDispatcherAsDocument(dispatcher != null ? dispatcher.getDocument() : null);
		}

		public Module setLoggerEventDispatcherAsDocument(ODocument dispatcher) {
			document.field(PROP_LOGGER_EVENT_DISPATCHER, dispatcher);
			return this;
		}

		public List<String> getLoggerEnhancers() {
			List<String> enhancers = document.field(PROP_LOGGER_ENHANCERS);
			return enhancers != null ? enhancers : Collections.emptyList();
		}

		public Module setLoggerEnhancers(List<String> enhancers) {
			document.field(PROP_LOGGER_ENHANCERS, enhancers);
			return this;
		}

		public boolean isActivated() {
			Boolean activated = document.field(OLoggerModule.OMODULE_ACTIVATE);
			return activated != null && activated;
		}

		public Module setActivated(boolean activated) {
			document.field(OLoggerModule.OMODULE_ACTIVATE, activated);
			return this;
		}

		public String getDomain() {
		    return document.field(PROP_DOMAIN);
        }

        public Module setDomain(String domain) {
		    document.field(PROP_DOMAIN, domain);
		    return this;
        }

        public OCorrelationIdGeneratorModel getCorrelationIdGenerator() {
			ODocument generator = getCorrelationIdGeneratorAsDocument();
			return generator != null ? new OCorrelationIdGeneratorModel(generator) : null;
		}

		public ODocument getCorrelationIdGeneratorAsDocument() {
			OIdentifiable generator = document.field(PROP_CORRELATION_ID_GENERATOR);
			return generator != null ? generator.getRecord() : null;
		}

        public Module setCorrelationIdGenerator(OCorrelationIdGeneratorModel correlationIdGenerator) {
			return setCorrelationIdGeneratorAsDocument(correlationIdGenerator != null ? correlationIdGenerator.getDocument() : null);
		}

		public Module setCorrelationIdGeneratorAsDocument(ODocument correlationIdGenerator) {
			document.field(PROP_CORRELATION_ID_GENERATOR, correlationIdGenerator);
			return this;
		}


		private List<IOLoggerEventEnhancer> getLoggerEnhancersInstances() {
			return getLoggerEnhancers().stream()
					.map(className -> {
						try {
							return (IOLoggerEventEnhancer) Class.forName(className).newInstance();
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
							throw new IllegalStateException(e);
						}
					}).collect(Collectors.toCollection(LinkedList::new));
		}

	}
	
}
