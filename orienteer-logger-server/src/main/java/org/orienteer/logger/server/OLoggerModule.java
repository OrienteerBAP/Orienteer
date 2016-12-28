package org.orienteer.logger.server;

import java.util.Date;

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
import org.orienteer.logger.server.rest.OLoggerReceiverResource;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-logger-server' module
 */
@Singleton
public class OLoggerModule extends AbstractOrienteerModule{
	
	public static final String MODULE_OLOGGER_NAME = "ologger";
	public static final String MODULE_OLOGGER_OCLASS = "OLoggerModule";
	public static final String OLOGGER_EVENT_OCLASS = "OLoggerEvent";

	public static ODatabaseDocument db; 
	
	protected OLoggerModule() {
		super(MODULE_OLOGGER_NAME, 3);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(MODULE_OLOGGER_OCLASS, OMODULE_CLASS).domain(OClassDomain.SPECIFICATION)
			.oProperty("collectorUrl", OType.STRING, 50);
		helper.oClass("OLoggerEvent")
			.oProperty("eventId", OType.STRING, 10).markAsDocumentName()
			.oProperty("application", OType.STRING, 20).markDisplayable()
			.oProperty("nodeId", OType.STRING, 30).markDisplayable()
			.oProperty("correlationId", OType.STRING, 40).markDisplayable()
			.oProperty("dateTime", OType.DATETIME, 50).markDisplayable()
			.oProperty("remoteAddress", OType.STRING, 60)
			.oProperty("hostName", OType.STRING, 70).markDisplayable()
			.oProperty("username", OType.STRING, 80)
			.oProperty("clientUrl", OType.STRING, 90)
			.oProperty("summary", OType.STRING, 100)
				.calculateBy("message.left(message.indexOf('\\n'))")
				.markDisplayable()
				.updateCustomAttribute(CustomAttribute.UI_READONLY, true)
			.oProperty("message", OType.STRING, 110).assignVisualization("textarea");	
		ODocument moduleDoc = new ODocument(MODULE_OLOGGER_OCLASS);
		moduleDoc.field(OMODULE_ACTIVATE, false);
		return moduleDoc;
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
		app.mountPages("org.orienteer.inclogger.web");
		OLoggerReceiverResource.mount(app);
		app.getRequestCycleListeners().add(new OLoggerExceptionListener());
	}
	
	@Override
	public void onConfigurationChange(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		super.onConfigurationChange(app, db, moduleDoc);
		installOLogger(app, moduleDoc);
	}
	
	private void installOLogger(OrienteerWebApplication app, ODocument moduleDoc) {
		String collectorUrl = moduleDoc.field("collectorUrl");
		IOLoggerConfiguration config = new DefaultOLoggerConfiguration();
		config.setApplicationName(app.getResourceSettings().getLocalizer().getString("application.name", null));
		config.setCollectorUrl(collectorUrl);
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
		app.unmountPages("org.orienteer.inclogger.web");
		OLoggerReceiverResource.unmount(app);
	}
	
	public static ODocument storeOLoggerEvent(final String json) {
		return new DBClosure<ODocument>() {

			@Override
			protected ODocument execute(ODatabaseDocument db) {
				ODocument doc = new ODocument();
				doc.fromJSON(json);
				Long dateTime = doc.field("dateTime", Long.class);
				doc.field("dateTime", new Date(dateTime));
				doc.setClassName(OLoggerModule.OLOGGER_EVENT_OCLASS);
				doc.save();
				return doc;
			}
		}.execute();
	}
	
}
