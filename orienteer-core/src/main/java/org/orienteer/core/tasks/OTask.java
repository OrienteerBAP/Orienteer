package org.orienteer.core.tasks;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass.ATTRIBUTES;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;

/**
 * Base task for task manager
 *
 */
public abstract class OTask {
	public static final String TASK_CLASS = "OTask";
	public static final String TASK_JAVA_CLASS_ATTRIBUTE = "OTaskJavaClassName";
	private ODocument doc;

	/**
	 * data fields
	 */
	public enum Field{
		NAME("name"),
		DESCRIPTION("description"),
		SESSIONS("sessions"),
		AUTODELETE_SESSIONS("autodeleteSessions");
		
		private String fieldName;
		public String fieldName(){ return fieldName;}
		private Field(String fieldName){	this.fieldName = fieldName;	}
	}
	
	/**
	 * Register fields in db 
	 */
	public static void onInstallModule(OrienteerWebApplication app, ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oAbstractClass(TASK_CLASS);
		helper.oProperty(Field.NAME.fieldName(),OType.STRING,10).markAsDocumentName();
		helper.oProperty(Field.DESCRIPTION.fieldName(),OType.STRING,20);
		helper.oProperty(Field.AUTODELETE_SESSIONS.fieldName(),OType.BOOLEAN,30);
		helper.oProperty(Field.SESSIONS.fieldName(),OType.LINKSET,40).updateCustomAttribute(CustomAttribute.HIDDEN, true);//avoid crosslinking //.linkedClass(OTask.TASK_CLASS);

		setOTaskJavaClassName(db,TASK_CLASS,"org.orienteer.core.tasks.OTask");
		
	}		
	
	public OTask(ODocument oTask) {
		this.doc = oTask;
	}
	
	public ODocument getDoc() {
		return doc;
	}
	/**
	 * Should be called in startNewSession implementation
	 * Unlink in {@link OTaskSessionOnDeleteHook}
	 * 
	 * @param oTaskSession session document
	 */
	protected void linkSession(String oTaskSessionId){
		ODatabaseDocument db = doc.getDatabase();
		db.commit();
		db.command(new OCommandSQL("update "+doc.getIdentity()+" add "+Field.SESSIONS.fieldName()+"="+oTaskSessionId)).execute();
//		db.command(new OCommandSQL("update "+oTaskSessionId+" set "+OTaskSession.Field.TASK_LINK.fieldName()+"="+oTask.getIdentity())).execute();
	}

	
	public static final OTask makeFromODocument(ODocument oTask){
		try {
			Class<?> myClass = Class.forName((String) getOTaskJavaClassName(oTask));
	
			Constructor<?> constructor = myClass.getConstructor(ODocument.class);
	
			Object result = constructor.newInstance(oTask);
			return (OTask) result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected static void setOTaskJavaClassName(ODatabaseDocument db,String taskClass, String javaClassName){
		db.getMetadata().getSchema().getClass(taskClass).setCustom(TASK_JAVA_CLASS_ATTRIBUTE, javaClassName);
	}

	private static String getOTaskJavaClassName(ODocument oTask){
		String className = oTask.getSchemaClass().getCustom(TASK_JAVA_CLASS_ATTRIBUTE);
		return className;
	}
	
	//////////////////////////////////////////////////////////////////////
	protected Object getField(Field field) {
		return getDoc().field(field.fieldName());
	}
	//////////////////////////////////////////////////////////////////////

	
	public abstract OTaskSession<?> startNewSession();
	
}
