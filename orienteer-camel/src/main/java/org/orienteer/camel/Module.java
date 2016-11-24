package org.orienteer.camel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
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
		OSchemaHelper helper = OSchemaHelper.bind(db);
		//Install data model
		//Return null of default OModule is enough
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);

		//InputStream is = getClass().getResourceAsStream("F://work//camel-context.xml");
		InputStream is;
	
		CamelContext context = new DefaultCamelContext();

        		
        		
        try {
			is = new FileInputStream("F://work//camel-context.xml");

			RoutesDefinition routes = context.loadRoutesDefinition(is);
    		context.addRouteDefinitions(routes.getRoutes());

    		//context.start();
    		/*
			context.addRoutes(new RouteBuilder() {
			    public void configure() {
			        from("file://test/?fileName=111.txt&noop=true").
			        split(body()).
			        	to("file://test/?fileName=333.txt").
			        	to("file://test/?fileName=222.txt").
			        end();
			    }
			});
			*/
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}        
        
			//Thread.sleep(3000);
        try {
			//context.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		app.mountPages("org.orienteer.camel.web");
		app.registerWidgets("org.orienteer.camel.widget");

	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.camel.web");
		app.unregisterWidgets("org.orienteer.camel.widget");
	}
	
}
