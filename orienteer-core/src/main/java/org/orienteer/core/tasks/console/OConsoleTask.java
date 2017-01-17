package org.orienteer.core.tasks.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.core.tasks.OTask.Field;
import org.orienteer.core.tasks.OTaskSession.ErrorTypes;
import org.orienteer.core.util.OSchemaHelper;

import com.google.common.base.Throwables;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
/**
 * OTask class for system console commands
 *
 */
public class OConsoleTask extends OTask {
	private class ConsoleTaskSessionImpl extends OConsoleTaskSession<ConsoleTaskSessionImpl> {}
	
	public static final String TASK_CLASS = "OConsoleTask";
	/**
	 * data fields
	 */
	public enum Field{
		INPUT("input");
		
		private String fieldName;
		public String fieldName(){ return fieldName;}
		private Field(String fieldName){	this.fieldName = fieldName;}
	}
	
	
	public OConsoleTask(ODocument oTask) {
		super(oTask);
	}

	@Override
	public OTaskSession<?> startNewSession() {
		final ConsoleTaskSessionImpl otaskSession = new ConsoleTaskSessionImpl();
		final String input = (String) getField(Field.INPUT); 
		otaskSession.onStart(this).
			setInput(input).
			setDeleteOnFinish((boolean) getField(OTask.Field.AUTODELETE_SESSIONS)).
		end();
		try{
			Thread innerThread = new Thread(new Runnable(){
				@Override
				public void run() {
					otaskSession.onProcess().
						updateThread().
					end();
					String charset =  Charset.defaultCharset().displayName();
					if(System.getProperty("os.name").startsWith("Windows")){
						if (Charset.isSupported("cp866")){
							charset = "cp866";
						}
					}
					try {
						
						final Process innerProcess = Runtime.getRuntime().exec(input);
						otaskSession.onProcess().
							setCallback(new ITaskSessionCallback() {
								
								@Override
								public void stop() throws Exception {
									if (innerProcess.isAlive()){
										innerProcess.destroy();
									}
								}
							}).end();
						BufferedReader reader =  new BufferedReader(new InputStreamReader(innerProcess.getInputStream(),charset));
						String curOutString = "";
							while ((curOutString = reader.readLine())!= null) {
								otaskSession.onProcess().
									incrementCurrentProgress().
									appendOut(curOutString).
								end();
							}
					} catch (IOException e) {
						otaskSession.onProcess().
							appendOut(e.getMessage()).
						end();
					}	
					otaskSession.onStop();
				}
				
			});
			innerThread.start();

		} catch (Exception e) {
			otaskSession.onError(ErrorTypes.UNKNOWN_ERROR,e.getMessage()).
				appendOut(Throwables.getStackTraceAsString(e)).
			end();
			otaskSession.onStop();
		}
		return otaskSession;		
	}
	//////////////////////////////////////////////////////////////////////
	protected Object getField(Field field) {
		return getDocument().field(field.fieldName());
	}
	//////////////////////////////////////////////////////////////////////
	
}
