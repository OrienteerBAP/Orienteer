package org.orienteer.inclogger;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.inclogger.IncidentLogger;
import org.orienteer.inclogger.client.OIncidentExceptionListener;
import org.orienteer.inclogger.core.OIncidentConfigurator;
import org.orienteer.inclogger.server.OIncidentReceiverResource;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-incident-logger' module
 */
public class IncidentLoggerModule extends AbstractOrienteerModule{

	public static ODatabaseDocument db; 
	
	protected IncidentLoggerModule() {
		super("incident.logger.driver", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		//Install data model
		//Return null of default OModule is enough
		helper.oClass("OIncident").
			oProperty("application", OType.STRING).
			oProperty("dateTime", OType.STRING).
			oProperty("userName", OType.STRING).
			oProperty("message", OType.STRING).
			oProperty("sended", OType.STRING). // for debug
			oProperty("recieved", OType.STRING). // for debug
			oProperty("stackTrace", OType.STRING);		
		return null;
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		super.onUpdate(app, db, oldVersion, newVersion);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass("OIncident").
			oProperty("application", OType.STRING).
			oProperty("dateTime", OType.STRING).
			oProperty("userName", OType.STRING).
			oProperty("message", OType.STRING).
			oProperty("sended", OType.STRING). // for debug
			oProperty("recieved", OType.STRING). // for debug
			oProperty("stackTrace", OType.STRING);		
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);

		app.mountPages("org.orienteer.inclogger.web");
		OIncidentReceiverResource.mount(app);
		Testresource.mount(app);
		
       IncidentLogger.init(new OIncidentConfigurator());
		app.getRequestCycleListeners().add(new OIncidentExceptionListener());
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		IncidentLogger.close();

		app.unmountPages("org.orienteer.inclogger.web");
		app.unmount(Testresource.MOUNT_PATH);
		app.unmount(OIncidentReceiverResource.MOUNT_PATH);
		
	}
	
}
