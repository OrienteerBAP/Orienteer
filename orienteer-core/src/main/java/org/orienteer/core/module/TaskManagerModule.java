package org.orienteer.core.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.MetaDataKey;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.IRealTask;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TaskManagerModule extends AbstractOrienteerModule {
	
	public static final MetaDataKey<Map<String,IRealTask>> TASK_MANAGER_SESSIONS_KEY = new MetaDataKey<Map<String,IRealTask>>()
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
	
	private void makeSchema(OrienteerWebApplication app, ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass("OTask")
			.oProperty("name", OType.STRING, 10).markAsDocumentName()
			.oProperty("data", OType.STRING, 30).assignVisualization("textarea");
	}

	
    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		app.setMetaData(TASK_MANAGER_SESSIONS_KEY, new ConcurrentHashMap<String,IRealTask>());
    }

}
