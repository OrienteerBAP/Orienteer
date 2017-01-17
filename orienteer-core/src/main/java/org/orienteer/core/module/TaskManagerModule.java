package org.orienteer.core.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.wicket.MetaDataKey;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskManager;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.core.tasks.console.OConsoleTask;
import org.orienteer.core.tasks.console.OConsoleTaskSession;
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
		private static final long serialVersionUID = 2L;
	};
	
	
    public static final String NAME = "task-manager";
    public static final int VERSION = 1;
    
    private OTaskManager oTaskManager = new OTaskManager();
    
    TaskManagerModule(){
    	super(NAME, VERSION);
    }
    
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {

		
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oAbstractClass(OTask.TASK_CLASS)
			.oProperty(OTask.Field.NAME.fieldName(),OType.STRING,10).markAsDocumentName()
			.oProperty(OTask.Field.DESCRIPTION.fieldName(),OType.STRING,20)
			.oProperty(OTask.Field.AUTODELETE_SESSIONS.fieldName(),OType.BOOLEAN,30)
			.oProperty(OTask.Field.SESSIONS.fieldName(),OType.LINKLIST,40)
			.updateCustomAttribute(CustomAttribute.HIDDEN, true);//avoid crosslinking //.linkedClass(OTask.TASK_CLASS);
		OTask.TASK_JAVA_CLASS_ATTRIBUTE.setValue(helper.getOClass(), OTask.class.getName());
		
		helper.oClass(OTaskSession.TASK_SESSION_CLASS)
			.oProperty(OTaskSession.Field.THREAD_NAME.fieldName(),OType.STRING,10).markAsDocumentName()
			.oProperty(OTaskSession.Field.STATUS.fieldName(),OType.STRING,20)
			.oProperty(OTaskSession.Field.TASK_LINK.fieldName(),OType.LINK,30).linkedClass(OTask.TASK_CLASS)
			.oProperty(OTaskSession.Field.START_TIMESTAMP.fieldName(),OType.DATETIME,40)
			.oProperty(OTaskSession.Field.FINISH_TIMESTAMP.fieldName(),OType.DATETIME,50)
			.oProperty(OTaskSession.Field.PROGRESS.fieldName(),OType.INTEGER,60)
			.oProperty(OTaskSession.Field.PROGRESS_CURRENT.fieldName(),OType.LONG,70)
			.oProperty(OTaskSession.Field.PROGRESS_FINAL.fieldName(),OType.LONG,80)
			.oProperty(OTaskSession.Field.IS_STOPPABLE.fieldName(),OType.BOOLEAN,90)
			.oProperty(OTaskSession.Field.DELETE_ON_FINISH.fieldName(),OType.BOOLEAN,100)
			.oProperty(OTaskSession.Field.ERROR_TYPE.fieldName(),OType.INTEGER,110)
			.oProperty(OTaskSession.Field.ERROR.fieldName(),OType.STRING,120);
		helper.setupRelationship(OTask.TASK_CLASS, OTask.Field.SESSIONS.fieldName(), OTaskSession.TASK_SESSION_CLASS, OTaskSession.Field.TASK_LINK.fieldName());
		return null;
	}
	
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		onInstall(app, db);
	}
	
    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		app.setMetaData(TASK_MANAGER_CALLBACK_KEY, new ConcurrentHashMap<String,ITaskSessionCallback>());
		app.setMetaData(TASK_MANAGER_SESSION_KEY, new ConcurrentHashMap<String,Integer>());
		oTaskManager.init(db);
    }
    

}
