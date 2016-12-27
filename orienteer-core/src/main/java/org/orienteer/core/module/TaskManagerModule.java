package org.orienteer.core.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.MetaDataKey;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.tasks.OConsoleTaskSession;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Module for task management
 *
 */
public class TaskManagerModule extends AbstractOrienteerModule {
	
	public static final MetaDataKey<Map<String,ITaskSessionCallback>> TASK_MANAGER_CALLBACK_KEY = new MetaDataKey<Map<String,ITaskSessionCallback>>()
	{
		private static final long serialVersionUID = 1L;
	};

	public static final MetaDataKey<Map<String,Integer>> TASK_MANAGER_SESSION_KEY = new MetaDataKey<Map<String,Integer>>()
	{
		private static final long serialVersionUID = 1L;
	};
	
	
    public static final String NAME = "task-manager";
    public static final int VERSION = 1;
    
    TaskManagerModule(){
    	super(NAME, VERSION);
    }
    
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		return null;
	}
	
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		
	}
	
	private void componentsOnInstall(OrienteerWebApplication app, ODatabaseDocument db){
		OTaskSession.onInstallModule(app, db);
		OConsoleTaskSession.onInstallModule(app, db);		
	}

	
    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		app.setMetaData(TASK_MANAGER_CALLBACK_KEY, new ConcurrentHashMap<String,ITaskSessionCallback>());
		app.setMetaData(TASK_MANAGER_SESSION_KEY, new ConcurrentHashMap<String,Integer>());
		componentsOnInstall(app,db);
    }
    

}
