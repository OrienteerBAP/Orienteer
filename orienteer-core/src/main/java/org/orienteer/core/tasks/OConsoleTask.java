package org.orienteer.core.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.orienteer.core.OrienteerWebApplication;

/**
 * 
 * @author Asm
 *
 */
public class OConsoleTask {

	private class TaskSessionImpl extends OConsoleTaskSession<TaskSessionImpl>{}

	private volatile TaskSessionImpl otask;
	private volatile Process innerProcess;
	private volatile Thread innerThread;
	
	public OConsoleTask() {
	}

	public void start() {
		otask = new TaskSessionImpl();
		final String input = "ping 127.0.0.1"; 
		final Application app = Application.get();
		final Session session = ThreadContext.getSession();
		otask.onStart().
			setInput(input).
			setFinalProgress(50).
			setTemporary(false);
		innerThread = new Thread(new Runnable(){
			@Override
			public void run() {
				if (!Application.exists()) {
					ThreadContext.setApplication(app);
					ThreadContext.setSession(session);
				}
				String charset =  Charset.defaultCharset().displayName();
				if(System.getProperty("os.name").startsWith("Windows")){
					if (Charset.isSupported("cp866")){
						charset = "cp866";
					}
				}
				try {
					
					innerProcess = Runtime.getRuntime().exec(input);
					BufferedReader reader =  new BufferedReader(new InputStreamReader(innerProcess.getInputStream(),charset));
					String curOutString = "";
						while ((curOutString = reader.readLine())!= null) {
							otask.onProcess().
								incrementCurrentProgress().
								appendOut(curOutString);
						}
					innerProcess = null;
				} catch (IOException e) {
					otask.onProcess().
					appendOut(e.getMessage());
				}	
			    otask.onStop();
			}
			
		});
		innerThread.start();
	}

	public void stop() {
		if (innerProcess!= null){
			innerProcess.destroy();
		}
	}

}
