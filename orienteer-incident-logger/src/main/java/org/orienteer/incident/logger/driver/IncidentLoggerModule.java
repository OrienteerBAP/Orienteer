package org.orienteer.incident.logger.driver;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.incident.logger.driver.component.OrienteerIncidentConfigurator;
import org.orienteer.incident.logger.driver.component.OrienteerIncidentReceiverResource;
import org.orienteer.incident.logger.driver.component.testresource;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.asm.utils.incident.logger.IncidentLogger;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'incident.logger.driver' module
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
			oProperty("Application", OType.STRING).
			oProperty("DateTime", OType.STRING).
			oProperty("UserName", OType.STRING).
			oProperty("Message", OType.STRING).
			oProperty("StackTrace", OType.STRING);		
		return null;
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		super.onUpdate(app, db, oldVersion, newVersion);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass("OIncident").
			oProperty("Application", OType.STRING).
			oProperty("DateTime", OType.STRING).
			oProperty("UserName", OType.STRING).
			oProperty("Message", OType.STRING).
			oProperty("StackTrace", OType.STRING);		
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);

		app.mountPages("org.orienteer.incident.logger.driver.web");
		OrienteerIncidentReceiverResource.mount(app);
		testresource.mount(app);

       //IncidentLogger.init(new OrienteerIncidentConfigurator());
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		//IncidentLogger.close();

		app.unmountPages("org.orienteer.incident.logger.driver.web");
		app.unmount(testresource.MOUNT_PATH);
		app.unmount(OrienteerIncidentReceiverResource.MOUNT_PATH);
		
	}
	
}