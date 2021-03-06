package org.orienteer.etl;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.etl.component.IOETLConfig;
import org.orienteer.etl.tasks.OETLTaskSession;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-etl' module
 */
public class Module extends AbstractOrienteerModule{

	protected Module() {
		super("orienteer-etl", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		//Install data model
		//Return null of default OModule is enough
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInitialize(app, db);
		app.mountPages("org.orienteer.etl.web");
		makeSchema(app, db);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.etl.web");
	}
	
	public void makeSchema(OrienteerWebApplication app, ODatabaseSession db){
		OSchemaHelper.bind(db).describeAndInstallSchema(IOETLConfig.class);
		
		OETLTaskSession.onInstallModule(app, db);
	}
}
