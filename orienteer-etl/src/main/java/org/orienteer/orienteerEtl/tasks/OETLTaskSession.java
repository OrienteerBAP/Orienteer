package org.orienteer.orienteerEtl.tasks;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class OETLTaskSession extends OTaskSessionRuntime{
	public static final String TASK_SESSION_CLASS = "OETLTaskSession";

	/**
	 * Fields of task session ODocument 
	 */
	public enum Field{
		OUTPUT("out",OType.STRING);
		
		private String fieldName;
		private OType type;
		public String fieldName(){ return fieldName;}
		public OType type(){ return type;}
		private Field(String fieldName,OType type){	this.fieldName = fieldName;	this.type = type;}
		
	}
	
	/**
	 * Register fields in db 
	 */
	public static final void onInstallModule(OrienteerWebApplication app, ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(TASK_SESSION_CLASS,OTaskSessionRuntime.TASK_SESSION_CLASS);
			helper.oProperty(ITaskSession.Field.THREAD_NAME.fieldName(),OType.STRING,10).markAsDocumentName();
			helper.oProperty(Field.OUTPUT.fieldName(),Field.OUTPUT.type(),37).assignVisualization("textarea");
	}	
		
		
	public OETLTaskSession() {
		super(TASK_SESSION_CLASS,true);
	}
	
	public OETLTaskSession appendOut(String out){
		out = getOTaskSessionPersisted().getDocument().field(Field.OUTPUT.fieldName())+out+"\n";
		getOTaskSessionPersisted().persist(Field.OUTPUT.fieldName(), out);
		return this;
	}
}
