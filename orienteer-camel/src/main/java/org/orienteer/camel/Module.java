package org.orienteer.camel;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.CamelContext;
import org.orienteer.camel.component.OIntegrationConfig;
import org.orienteer.camel.tasks.OCamelTaskSession;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.method.OMethodsManager;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'camel' module
 */
public class Module extends AbstractOrienteerModule{

	protected Module() {
		super("camel", 2);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
//		OSchemaHelper helper = OSchemaHelper.bind(db);
		makeSchema(app,db);
		//Install data model
		//Return null of default OModule is enough
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
		super.onUpdate(app, db, oldVersion, newVersion);
		onInstall(app, db);
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInitialize(app, db);
		
		app.setMetaData(OIntegrationConfig.INTEGRATION_SESSIONS_KEY, new ConcurrentHashMap<String,CamelContext>());
		app.mountPackage("org.orienteer.camel.web");
		//app.registerWidgets("org.orienteer.camel.widget");
		OMethodsManager.get().addModule(Module.class);
		OMethodsManager.get().reload();
		

	}
	
	private void makeSchema(OrienteerWebApplication app, ODatabaseSession db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(OIntegrationConfig.TASK_CLASS,OTask.TASK_CLASS)
			.oProperty("script", OType.STRING, 15).assignVisualization(UIVisualizersRegistry.VISUALIZER_CODE);
		OCamelTaskSession.onInstallModule(app, db);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		super.onDestroy(app, db);
		app.unmountPackage("org.orienteer.camel.web");
		//app.unregisterWidgets("org.orienteer.camel.widget");
		
		OMethodsManager.get().removeModule(Module.class);
		OMethodsManager.get().reload();
	}
	
}
