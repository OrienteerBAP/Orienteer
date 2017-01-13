package org.orienteer.core.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.orienteer.core.OrienteerWebApplication;
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
	
	/**
	 * Register fields in db 
	 */
	public static void onInstallModule(OrienteerWebApplication app, ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(TASK_CLASS,OTask.TASK_CLASS);
		helper.oProperty(Field.INPUT.fieldName(),OType.STRING,25);

		setOTaskJavaClassName(db,TASK_CLASS,"org.orienteer.core.tasks.OConsoleTask");

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
						
						Process innerProcess = Runtime.getRuntime().exec(input);
						otaskSession.onProcess().
							setCallback(new OConsoleTaskSessionCallback(innerProcess)).
						end();
						BufferedReader reader =  new BufferedReader(new InputStreamReader(innerProcess.getInputStream(),charset));
						String curOutString = "";
							while ((curOutString = reader.readLine())!= null) {
								otaskSession.onProcess().
									incrementCurrentProgress().
									appendOut(curOutString).
								end();
							}
						innerProcess = null;
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
		return getDoc().field(field.fieldName());
	}
	//////////////////////////////////////////////////////////////////////
	
}
