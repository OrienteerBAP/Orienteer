package org.orienteer.core.tasks.console;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Task session with input and output
 */

public class OConsoleTaskSession extends OTaskSessionRuntime{

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
		super(TASK_SESSION_CLASS,true);
	}
	
	public OConsoleTaskSession setInput(String input){
		getOTaskSessionPersisted().persist("in", input);
		return this;
	}

	public OConsoleTaskSession appendOut(String out){
		out = getOTaskSessionPersisted().getDocument().field("out")+out+"\n";
		getOTaskSessionPersisted().persist("out", out);
		return this;
	}

}
