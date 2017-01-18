package org.orienteer.core.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.wicket.MetaDataKey;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskManager;
import org.orienteer.core.tasks.OTaskSessionRuntime;
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
	
    public static final String NAME = "task-manager";
    public static final int VERSION = 1;
    
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
		
		helper.oClass(OTaskSessionRuntime.TASK_SESSION_CLASS)
			.oProperty(ITaskSession.Field.THREAD_NAME.fieldName(),OType.STRING,10).markAsDocumentName()
			.oProperty(ITaskSession.Field.STATUS.fieldName(),OType.STRING,20)
			.oProperty(ITaskSession.Field.TASK_LINK.fieldName(),OType.LINK,30).linkedClass(OTask.TASK_CLASS)
			.oProperty(ITaskSession.Field.START_TIMESTAMP.fieldName(),OType.DATETIME,40)
			.oProperty(ITaskSession.Field.FINISH_TIMESTAMP.fieldName(),OType.DATETIME,50)
			.oProperty(ITaskSession.Field.PROGRESS.fieldName(),OType.DOUBLE,60)
			.oProperty(ITaskSession.Field.PROGRESS_CURRENT.fieldName(),OType.DOUBLE, 70)
			.oProperty(ITaskSession.Field.PROGRESS_FINAL.fieldName(),OType.DOUBLE,80)
			.oProperty(ITaskSession.Field.IS_STOPPABLE.fieldName(),OType.BOOLEAN,90)
			.oProperty(ITaskSession.Field.DELETE_ON_FINISH.fieldName(),OType.BOOLEAN,100)
			.oProperty(ITaskSession.Field.ERROR_TYPE.fieldName(),OType.INTEGER,110)
			.oProperty(ITaskSession.Field.ERROR.fieldName(),OType.STRING,120);
		helper.setupRelationship(OTask.TASK_CLASS, OTask.Field.SESSIONS.fieldName(), OTaskSessionRuntime.TASK_SESSION_CLASS, ITaskSession.Field.TASK_LINK.fieldName());
		return null;
	}
	
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		onInstall(app, db);
	}
}
