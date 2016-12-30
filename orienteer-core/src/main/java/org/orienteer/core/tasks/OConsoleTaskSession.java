package org.orienteer.core.tasks;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Task session with input and output
 *
 * @param <T> just for chaining. See also {@link OConsoleTask}
 */

public class OConsoleTaskSession<T extends OConsoleTaskSession<T>> extends OTaskSession<T>{

	public static final String TASK_SESSION_CLASS = "OConsoleTaskSession";

	/**
	 * Fields of task session ODocument 
	 */
	public enum Field{
		INPUT("in",OType.STRING),
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
		helper.oClass(TASK_SESSION_CLASS,OTaskSession.TASK_SESSION_CLASS);
			helper.oProperty("in",OType.STRING,35);
			helper.oProperty("out",OType.STRING,37).assignVisualization("textarea");
	}	
	
	public OConsoleTaskSession() {
		super(TASK_SESSION_CLASS);
	}
	
	public T setInput(String input){
		setField(Field.INPUT, input);
		return this.asT();
	}

	public T appendOut(String out){
		String field = (String)getField(Field.OUTPUT);
		if (field==null) field = "";
		setField(Field.OUTPUT, field.concat(out).concat("\n"));
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
