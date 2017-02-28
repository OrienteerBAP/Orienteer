package org.orienteer.birt;

import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.orienteer.birt.component.OUserDataProxy;
import org.orienteer.birt.component.widget.AbstractBirtWidget;
import org.orienteer.birt.component.widget.BrowseBirtWidget;
import org.orienteer.birt.component.widget.ODocumentBirtWidget;
import org.orienteer.birt.orientdb.impl.Connection;

//import org.orienteer.birt.orientdb.*;

/**
 * {@link IOrienteerModule} for 'orienteer-birt' module
 */
public class Module extends AbstractOrienteerModule{

	public static final String logsPath = "/temp";
	
	private IReportEngine engine;

	protected Module() {
		super("orienteer-birt", 1,OWidgetsModule.NAME);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		//Install data model
		makeSchema(db);
		//Return null of default OModule is enough
		return null;
	}
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		makeSchema(db);
		
		super.onUpdate(app, db, oldVersion, newVersion);
	}
	
	private void makeSchema(ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(AbstractBirtWidget.OCLASS_NAME, OWidgetsModule.OCLASS_WIDGET).domain(OClassDomain.SYSTEM)
			.oProperty(AbstractBirtWidget.REPORT_FIELD_NAME, OType.BINARY, 100)
			.oProperty(AbstractBirtWidget.PARAMETERS_FIELD_NAME, OType.EMBEDDEDMAP, 110).linkedType(OType.STRING);
		
		helper.oClass(BrowseBirtWidget.OCLASS_NAME, AbstractBirtWidget.OCLASS_NAME).domain(OClassDomain.SYSTEM);
		helper.oClass(ODocumentBirtWidget.OCLASS_NAME, AbstractBirtWidget.OCLASS_NAME).domain(OClassDomain.SYSTEM);
		

	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		app.mountPages("org.orienteer.birt.web");
		app.registerWidgets("org.orienteer.birt.component.widget");
		
		try{
			
			Connection.setUserData(new OUserDataProxy());
						
		    final EngineConfig config = new EngineConfig( );

		    config.setLogConfig(logsPath, Level.FINE);
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
		    // Ignore
		}			
	}

	public IReportEngine getEngine() {
		return engine;
	}
	
}
