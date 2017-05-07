package org.orienteer.core.tasks.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.tasks.behavior.OTaskSessionInterruptBehavior;

import com.orientechnologies.orient.core.record.impl.ODocument;
/**
 * OTask class for system console commands
 *
 */
public class OConsoleTask extends OTask {
	private static final long serialVersionUID = 1L;
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
	///////////////////////////////////////////////////////////////////////////////////
	//OMethods
	@ClassOMethod(
			icon = FAIconType.play, bootstrap=BootstrapType.SUCCESS,titleKey="task.command.start",
			filters={@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
					@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),		
			},
			behaviors={}
		)
	public void startNewSession( IMethodEnvironmentData data){
		startNewSession();
	}
	///////////////////////////////////////////////////////////////////////////////////
	
	public OConsoleTask(ODocument oTask) {
		super(oTask);
	}

	@Override
	public OTaskSessionRuntime startNewSession() {
		final OConsoleTaskSession otaskSession = new OConsoleTaskSession();
		final String input = (String) getField(Field.INPUT); 
		otaskSession.setInput(input);
		otaskSession.setDeleteOnFinish((boolean) getField(OTask.Field.AUTODELETE_SESSIONS));
		otaskSession.setOTask(this);
		try{
			Thread innerThread = new Thread(new Runnable(){
				@Override
				public void run() {
					otaskSession.start();
					String charset =  Charset.defaultCharset().displayName();
					if(System.getProperty("os.name").startsWith("Windows")){
						if (Charset.isSupported("cp866")){
							charset = "cp866";
						}
					}
					try {
						
						final Process innerProcess = Runtime.getRuntime().exec(input);
						otaskSession.setCallback(new ITaskSessionCallback() {
								
								@Override
								public void interrupt() throws Exception {
									try {
										innerProcess.exitValue();
										//There is exit code: process is finished already
									} catch (IllegalThreadStateException e) {
										//Process is active - destroying
										innerProcess.destroy();
									}
								}
							});
						BufferedReader reader =  new BufferedReader(new InputStreamReader(innerProcess.getInputStream(),charset));
						String curOutString = "";
							while ((curOutString = reader.readLine())!= null) {
								otaskSession.incrementCurrentProgress();
								otaskSession.appendOut(curOutString);
							}
					} catch (IOException e) {
						otaskSession.appendOut(e.getMessage());
					}	
					otaskSession.finish();
				}
				
			});
			innerThread.start();

		} catch (Exception e) {
			otaskSession.finish();
		}
		return otaskSession;		
	}
	//////////////////////////////////////////////////////////////////////
	protected Object getField(Field field) {
		return getDocument().field(field.fieldName());
	}
	//////////////////////////////////////////////////////////////////////
	
}
