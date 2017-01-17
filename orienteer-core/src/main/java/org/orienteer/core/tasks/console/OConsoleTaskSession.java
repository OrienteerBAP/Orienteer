package org.orienteer.core.tasks.console;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Task session with input and output
 *
 * @param <T> just for chaining. See also {@link OConsoleTask}
 */

public class OConsoleTaskSession extends OTaskSession{

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
	
	
	public OConsoleTaskSession() {
		super(TASK_SESSION_CLASS);
	}
	
	public OConsoleTaskSession setInput(String input){
		setField(Field.INPUT, input);
		return this;
	}

	public OConsoleTaskSession appendOut(String out){
		appendField(Field.OUTPUT, out.concat("\n"));
		return this;
	}
	
	//////////////////////////////////////////////////////////////////////
	protected void setField(Field field,Object value) {
		getSessionUpdater().set(field.fieldName(), value);
	}

	protected void appendField(Field field,String value) {
		getSessionUpdater().append(field.fieldName(), value);
	}

}
