package org.orienteer.etl;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.etl.component.IOETLConfig;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-etl' module
 */
public class Module extends AbstractOrienteerModule{

	protected Module() {
		super("orienteer-etl", 2);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		OSchemaHelper.bind(db).describeAndInstallSchema(IOETLConfig.class);
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
		if(2>oldVersion && 2<=newVersion) {
			OSchema schema = db.getMetadata().getSchema();
			if(schema.getClass("OETLTaskSession")!=null) schema.dropClass("OETLTaskSession");
		}
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInitialize(app, db);
		app.mountPages("org.orienteer.etl.web");
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.etl.web");
	}
	
}
