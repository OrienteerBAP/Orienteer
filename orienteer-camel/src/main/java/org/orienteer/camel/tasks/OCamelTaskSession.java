package org.orienteer.camel.tasks;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Task session for Orienteer Camel integration
 */
public class OCamelTaskSession extends OTaskSessionRuntime {

	public static final String TASK_SESSION_CLASS = "OCamelTaskSession";

	/**
	 * Fields of task session ODocument 
	 */
	public enum Field{
		OUTPUT("out",OType.STRING),
		CONFIG("config",OType.LINK);
		
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
			helper.oProperty(Field.CONFIG.fieldName(),Field.CONFIG.type(),35).markAsLinkToParent();
			helper.oProperty(Field.OUTPUT.fieldName(),Field.OUTPUT.type(),37).assignVisualization("textarea");
	}	
	
	public OCamelTaskSession() {
		super(TASK_SESSION_CLASS,true);
	}

	public OCamelTaskSession(String sessionClass) {
		super(sessionClass,true);
	}
	
	public OCamelTaskSession appendOut(String out){
		out = getOTaskSessionPersisted().getDocument().field(Field.OUTPUT.fieldName())+out+"\n";
		getOTaskSessionPersisted().persist(Field.OUTPUT.fieldName(), out);
		return this;
	}
	
	public OCamelTaskSession setConfig(String configId){
		getOTaskSessionPersisted().persist(Field.CONFIG.fieldName(), new ORecordId(configId));
		return this;
	}
	
}