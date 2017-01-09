package org.orienteer.camel.tasks;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.core.tasks.OTaskSession.Field;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class OCamelTaskSession<T extends OCamelTaskSession<T>> extends OTaskSession<T>{

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
		helper.oClass(TASK_SESSION_CLASS,OTaskSession.TASK_SESSION_CLASS);
			helper.oProperty(OTaskSession.Field.THREAD_NAME.fieldName(),OType.STRING,10).markAsDocumentName();
			helper.oProperty(Field.CONFIG.fieldName(),Field.CONFIG.type(),35);
			helper.oProperty(Field.OUTPUT.fieldName(),Field.OUTPUT.type(),37).assignVisualization("textarea");
	}	
	
	public OCamelTaskSession() {
		super(TASK_SESSION_CLASS);
	}
	
	public T appendOut(String out){
		String field = (String)getField(Field.OUTPUT);
		if (field==null) field = "";
		setField(Field.OUTPUT, field.concat(out).concat("\n"));
		return this.asT();
	}
	
	public T setConfig(String configId){
		setField(Field.CONFIG, new ORecordId(configId));
		return this.asT();
	}
	
	//////////////////////////////////////////////////////////////////////
	protected Object getField(Field field) {
		return getSessionDoc().field(field.fieldName());
	}
	//////////////////////////////////////////////////////////////////////

	protected void setField(Field field,Object value) {
		getSessionDoc().field(field.fieldName(),value);
	}

}