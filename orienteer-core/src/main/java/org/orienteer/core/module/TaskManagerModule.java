package org.orienteer.core.module;

import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.IOConsoleTask;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Module for task management
 *
 */
public class TaskManagerModule extends AbstractOrienteerModule {
	
    public static final String NAME = "task-manager";
    public static final int VERSION = 4;
    
    TaskManagerModule(){
    	super(NAME, VERSION);
    }
    
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {

		
		DAO.define(IOTask.class, IOTaskSessionPersisted.class, IOConsoleTask.class);
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db,
			int oldVersion, int newVersion) {
		if(2>oldVersion && 2<=newVersion) {
			OSchema schema = db.getMetadata().getSchema();
			if(schema.getClass("OConsoleTaskSession")!=null) schema.dropClass("OConsoleTaskSession");
		}
		onInstall(app, db);
		if(oldVersion<=4) {
			OSchema schema = db.getMetadata().getSchema();
			OClass taskSessionClass = schema.getClass(IOTaskSessionPersisted.CLASS_NAME);
			OProperty toDelete = taskSessionClass.getProperty("isStopable");
			OProperty toKeep = taskSessionClass.getProperty("stopable");
			if(toDelete!=null && toKeep!=null) {
				db.execute("sql", "update "+IOTaskSessionPersisted.CLASS_NAME+" set stopable = isStopable").close();
				taskSessionClass.dropProperty("isStopable");
			}
		}
	}
}
