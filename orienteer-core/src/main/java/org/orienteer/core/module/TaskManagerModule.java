package org.orienteer.core.module;

import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.ODatabaseSession;
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
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {

		
		OSchemaHelper helper = OSchemaHelper.bind(db);
		
		helper.oClass(OTaskSessionRuntime.TASK_SESSION_CLASS)
			.oProperty(ITaskSession.Field.THREAD_NAME.fieldName(),OType.STRING,10).markAsDocumentName()
			.oProperty(ITaskSession.Field.STATUS.fieldName(),OType.STRING,20)
			.oProperty(ITaskSession.Field.TASK_LINK.fieldName(),OType.LINK,30)
			.oProperty(ITaskSession.Field.START_TIMESTAMP.fieldName(),OType.DATETIME,40)
			.oProperty(ITaskSession.Field.FINISH_TIMESTAMP.fieldName(),OType.DATETIME,50)
			.oProperty(ITaskSession.Field.PROGRESS.fieldName(),OType.DOUBLE,60)
			.oProperty(ITaskSession.Field.PROGRESS_CURRENT.fieldName(),OType.DOUBLE, 70)
			.oProperty(ITaskSession.Field.PROGRESS_FINAL.fieldName(),OType.DOUBLE,80)
			.oProperty(ITaskSession.Field.IS_STOPPABLE.fieldName(),OType.BOOLEAN,90)
			.oProperty(ITaskSession.Field.DELETE_ON_FINISH.fieldName(),OType.BOOLEAN,100)
			.oProperty(ITaskSession.Field.ERROR_TYPE.fieldName(),OType.INTEGER,110)
			.oProperty(ITaskSession.Field.ERROR.fieldName(),OType.STRING,120);
		
		DAO.describe(helper, IOTask.class);
		
		helper.setupRelationship(IOTask.CLASS_NAME, "sessions", OTaskSessionRuntime.TASK_SESSION_CLASS, ITaskSession.Field.TASK_LINK.fieldName());
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db,
			int oldVersion, int newVersion) {
		onInstall(app, db);
	}
}
