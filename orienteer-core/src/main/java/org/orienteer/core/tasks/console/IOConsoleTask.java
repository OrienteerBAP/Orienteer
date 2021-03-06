package org.orienteer.core.tasks.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.orienteer.core.OClassDomain;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
/**
 * OTask class for system console commands
 *
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOConsoleTask.CLASS_NAME)
public interface IOConsoleTask extends IOTask {
	public static final String CLASS_NAME = "OConsoleTask";
	
	public String getInput();
	public IOConsoleTask setInput(String input);

	@OMethod(
			icon = FAIconType.play, bootstrap=BootstrapType.SUCCESS,titleKey="task.command.start",
			filters={@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
					@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),		
			},
			behaviors={}
		)
	public default void startNewSession( IMethodContext data){
		startNewSession();
	}

	@Override
	public default OTaskSessionRuntime startNewSession() {
		final OConsoleTaskSession otaskSession = new OConsoleTaskSession();
		final String input = getInput();
		otaskSession.setInput(input);
		otaskSession.setDeleteOnFinish(isAutodeleteSessions());
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
	
}
