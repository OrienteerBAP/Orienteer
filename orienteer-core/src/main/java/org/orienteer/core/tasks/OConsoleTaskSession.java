package org.orienteer.core.tasks;


@SuppressWarnings("rawtypes")
public class OConsoleTaskSession<T extends OConsoleTaskSession> extends OTaskSession<T>{

	public static final String TASK_SESSION_CLASS = "OConsoleTaskSession";
	public static final String INPUT_FIELD = "in";
	public static final String OUTPUT_FIELD = "out";
	
	public OConsoleTaskSession() {
		super(TASK_SESSION_CLASS);
	}
	
	public T setInput(String input){
		setField(INPUT_FIELD, input);
		return this.asT();
	}

	public T appendOut(String out){
		String field = (String)getField(OUTPUT_FIELD);
		setField(OUTPUT_FIELD, field.concat(out).concat("\n"));
		return this.asT();
	}

}
