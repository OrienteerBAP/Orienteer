package org.orienteer.core.tasks.console;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.module.TaskManagerModule;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IOrienteerModule} to install datamodel for cosole tasks 
 */
public class OConsoleTasksModule extends AbstractOrienteerModule {
	public static final String NAME = "console-tasks";

	public OConsoleTasksModule() {
		super(NAME, 1, TaskManagerModule.NAME);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(OConsoleTask.TASK_CLASS,OTask.TASK_CLASS)
				.oProperty(OConsoleTask.Field.INPUT.fieldName(),OType.STRING,25);
		OTask.TASK_JAVA_CLASS_ATTRIBUTE.setValue(db.getMetadata().getSchema().getClass(OConsoleTask.TASK_CLASS), OConsoleTask.class.getName());
		
		helper.oClass(OConsoleTaskSession.TASK_SESSION_CLASS,OTaskSessionRuntime.TASK_SESSION_CLASS)
				.oProperty("in",OType.STRING,35).markAsDocumentName()
				.oProperty("out",OType.STRING,37).assignVisualization("textarea");
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		onInstall(app, db);
	}
	
}
