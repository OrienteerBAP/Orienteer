package org.orienteer.logger.server;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.logger.IOLoggerConfiguration;
import org.orienteer.logger.OLogger;
import org.orienteer.logger.OLoggerBuilder;
import org.orienteer.logger.impl.DefaultOLoggerConfiguration;
import org.orienteer.logger.server.service.EmbeddedOLoggerEventDispatcher;
import org.orienteer.logger.server.model.OLoggerEventModel;
import org.orienteer.logger.server.resource.OLoggerReceiverResource;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.orienteer.logger.server.service.OLoggerExceptionListener;
import org.orienteer.logger.server.service.OWebEnhancer;

/**
 * {@link IOrienteerModule} for 'orienteer-logger-server' module
 */
@Singleton
public class OLoggerModule extends AbstractOrienteerModule{
	
	public static final String NAME = "orienteer-logger";

	public static final int VERSION = 3;


	protected OLoggerModule() {
		super(NAME, VERSION);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		installOLoggerEvent(helper);

		return installModule(helper);
	}

	private void installOLoggerEvent(OSchemaHelper helper) {
		helper.oClass(OLoggerEventModel.CLASS_NAME)
				.oProperty(OLoggerEventModel.PROP_EVENT_ID, OType.STRING, 10)
					.markAsDocumentName()
					.notNull()
				.oProperty(OLoggerEventModel.PROP_APPLICATION, OType.STRING, 20)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_NODE_ID, OType.STRING, 30)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_CORRELATION_ID, OType.STRING, 40)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_DATE_TIME, OType.DATETIME, 50)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_REMOTE_ADDRESS, OType.STRING, 60)
				.oProperty(OLoggerEventModel.PROP_HOST_NAME, OType.STRING, 70)
					.markDisplayable()
				.oProperty(OLoggerEventModel.PROP_USERNAME, OType.STRING, 80)
				.oProperty(OLoggerEventModel.PROP_CLIENT_URL, OType.STRING, 90)
				.oProperty(OLoggerEventModel.PROP_SUMMARY, OType.STRING, 100)
					.calculateBy("message.left(message.indexOf('\\n'))")
					.markDisplayable()
					.updateCustomAttribute(CustomAttribute.UI_READONLY, true)
				.oProperty("message", OType.STRING, 110)
					.assignVisualization("code");
	}

	private ODocument installModule(OSchemaHelper helper) {
		helper.oClass(Module.CLASS_NAME, OMODULE_CLASS).domain(OClassDomain.SPECIFICATION)
				.oProperty(Module.PROP_COLLECTOR_URL, OType.STRING, 50);

		return helper.oDocument(AbstractOrienteerModule.OMODULE_NAME, NAME)
				.field(OMODULE_ACTIVATE, false)
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
		
		installOLogger(app, moduleDoc);
		app.mountPackage("org.orienteer.inclogger.web");
		OLoggerReceiverResource.mount(app);
		app.getRequestCycleListeners().add(new OLoggerExceptionListener());
	}
	
	@Override
	public void onConfigurationChange(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		super.onConfigurationChange(app, db, moduleDoc);
		installOLogger(app, moduleDoc);
	}
	
	private void installOLogger(OrienteerWebApplication app, ODocument moduleDoc) {
		IOLoggerConfiguration config = new DefaultOLoggerConfiguration();
		config.setApplicationName(app.getResourceSettings().getLocalizer().getString("application.name", null));
		//If collector URL was not overwriten from system properties: lets get it from module
		if(Strings.isEmpty(config.getCollectorUrl())) 
			config.setCollectorUrl((String)moduleDoc.field("collectorUrl"));
		OLogger oLogger = new OLoggerBuilder()
								.setLoggerEventDispatcher(new EmbeddedOLoggerEventDispatcher())
								.addEnhancer(new OWebEnhancer())
								.addDefaultEnhancers().create(config);
		OLogger.set(oLogger);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		OLogger.set(null);
		app.unmountPackage("org.orienteer.inclogger.web");
		OLoggerReceiverResource.unmount(app);
	}

	public static class Module extends ODocumentWrapper {

		public static final String CLASS_NAME = "OLoggerModule";

		public static final String PROP_COLLECTOR_URL = "collectorUrl";

		public Module() {
			super(CLASS_NAME);
		}

		public Module(ORID iRID) {
			super(iRID);
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
	}
	
}
