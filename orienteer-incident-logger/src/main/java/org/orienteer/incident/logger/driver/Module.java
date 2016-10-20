package org.orienteer.incident.logger.driver;

import org.orienteer.core.CustomAttributes;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.incident.logger.driver.component.OrienteerIncidentConfigurator;
import org.orienteer.incident.logger.driver.component.OrienteerIncidentReciever;
import org.orienteer.incident.logger.driver.component.OrienteerIncidentRecieverResource;
import org.orienteer.incident.logger.driver.component.testresource;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.asm.utils.incident.logger.IncidentLogger;
import ru.asm.utils.incident.logger.core.ILogger;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'incident.logger.driver' module
 */
public class Module extends AbstractOrienteerModule{

	public static ODatabaseDocument db; 
	
	protected Module() {
		super("incident.logger.driver", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		//Install data model
		//Return null of default OModule is enough
		
		//ODocument doc = new ODocument("Counter");
		//doc.field( "name", "incidentCounter" );
		//doc.field( "value", 0 );
		
		//OCommandSQL req = new OCommandSQL("");
		//db.commit();
		//db.command(new OCommandSQL("CREATE CLASS counter"));
		//db.command(new OCommandSQL("INSERT INTO counter SET name='mycounter', value=0"));
		//OSequenceLibrary sequenceLibrary = db.getMetadata().getSequenceLibrary();
		//OSequence seq = sequenceLibrary.createSequence("incedentId", SEQUENCE_TYPE.ORDERED, new OSequence.CreateParams().setStart(0));
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);

		OrienteerIncidentRecieverResource.mount(app);
		testresource.mount(app);
		
		Module.db = db;
		app.mountPages("org.orienteer.incident.logger.driver.web");
		
		//db.commit();
		/*
		ODocument doc = new ODocument("counter");
		doc.field( "name", "mycounter" );
		doc.field( "value", 0 );
		doc.save();
		*/
		//db.command(new OSQLSynchQuery<Object>("CREATE CLASS counter"));
		//db.command(new OSQLSynchQuery<Object>("INSERT INTO counter SET name='mycounter', value=0"));
		
        System.out.println( "----------" );
        IncidentLogger.init(new OrienteerIncidentConfigurator());

        ILogger logger = IncidentLogger.get().makeLogger();
        logger.message("Example incident1");

        try{
            logger.message("Example incident2");
            if (true){
                throw new Exception("exception body");
            }
            IncidentLogger.close();
        }catch (Exception e) {
            logger.incident(e);
		}
        logger.incident("one");
        logger.incident("two");
        logger.incident("three");

        //IncidentLogger.get().getServerData().applyData(IncidentLogger.get().getServerData().getData());
        System.out.println(IncidentLogger.get().getServerData().getData());

        
        /*
		db.command(new OCommandSQL(" "
				+ "BEGIN	"
				+ "let $counter = UPDATE counter INCREMENT value = 1 return after $current WHERE name = 'mycounter'		"
				+ "INSERT INTO OIncident SET id = $counter.value[0], data = '"+"lot of data"+"'		"
				+ "COMMIT		"));
				*/
		/*

				BEGIN	
				let $counter = UPDATE counter INCREMENT value = 1 return after $current WHERE name = 'mycounter'
				INSERT INTO OIncident SET id = (UPDATE counter INCREMENT value = 1 return after $current WHERE name = 'mycounter').value[0], data = 'lot of data'
				COMMIT




		 * */
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.incident.logger.driver.web");
	}
	
}
