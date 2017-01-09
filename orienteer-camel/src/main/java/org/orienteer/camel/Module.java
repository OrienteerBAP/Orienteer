package org.orienteer.camel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.orienteer.camel.tasks.OCamelTaskSession;
import org.orienteer.camel.widget.CamelWidget;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'camel' module
 */
public class Module extends AbstractOrienteerModule{

	protected Module() {
		super("camel", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
//		OSchemaHelper helper = OSchemaHelper.bind(db);
		makeSchema(app,db);
		//Install data model
		//Return null of default OModule is enough
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);

		app.setMetaData(CamelWidget.INTEGRATION_SESSIONS_KEY, new ConcurrentHashMap<String,CamelContext>());
		app.mountPages("org.orienteer.camel.web");
		app.registerWidgets("org.orienteer.camel.widget");
		
		OCamelTaskSession.onInstallModule(app, db);
	}
	
	private void makeSchema(OrienteerWebApplication app, ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass("OIntegrationConfig")
			.oProperty("name", OType.STRING, 10).markAsDocumentName()
			.oProperty("description", OType.STRING, 20)
			.oProperty("script", OType.STRING, 30).assignVisualization("textarea");
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.camel.web");
		app.unregisterWidgets("org.orienteer.camel.widget");
	}
	
}
