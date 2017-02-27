package org.orienteer.birt;

import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.orienteer.birt.component.OUserDataProxy;
import org.orienteer.birt.orientdb.impl.Connection;

//import org.orienteer.birt.orientdb.*;

/**
 * {@link IOrienteerModule} for 'orienteer-birt' module
 */
public class Module extends AbstractOrienteerModule{

	public IReportEngine engine;

	protected Module() {
		super("orienteer-birt", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		//Install data model
		//Return null of default OModule is enough
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		app.mountPages("org.orienteer.birt.web");
		app.registerWidgets("org.orienteer.birt.component.widget");

		
		try{
			
			//Connection.
			Connection.setUserData(new OUserDataProxy());
						
		    final EngineConfig config = new EngineConfig( );

		    config.setLogConfig("/temp", Level.FINE);
		    //config.setFontConfig("c:/temp/fontsConfig.xml");

		    Platform.startup( config );
		    //If using RE API in Eclipse/RCP application this is not needed.
		    IReportEngineFactory factory = (IReportEngineFactory) Platform
		            .createFactoryObject( IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );
		    engine = factory.createReportEngine( config );
		    engine.changeLogLevel( Level.WARNING );
		}catch( Exception ex){
		    ex.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.birt.web");
		app.unregisterWidgets("org.orienteer.birt.component.widget");
		try
		{
		    engine.destroy();
		    Platform.shutdown();
		    RegistryProviderFactory.releaseDefault();
		}catch ( Exception e1 ){
			e1.printStackTrace();
		//}catch ( EngineException e1 ){
		    // Ignore
		}			
	}
	
}
