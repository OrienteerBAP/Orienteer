package org.orienteer.core.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.orienteer.core.OrienteerWebApplication;

public class OConsoleTask implements IRealTask {

	private volatile OTask otask;
	private volatile Process innerProcess;
	private volatile Thread innerThread;
	
	public OConsoleTask() {
	}
	
	@Override
	public void setOTask(OTask otask) {
		this.otask = otask;
	}
	

	@Override
	public void start(final OTaskData data) {
		otask.onStart();
		final Application app = Application.get();
		final Session session = ThreadContext.getSession();
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
					
					innerProcess = Runtime.getRuntime().exec(data.toString());
					BufferedReader reader =  new BufferedReader(new InputStreamReader(innerProcess.getInputStream(),charset));
					String curOutString = "";
						while ((curOutString = reader.readLine())!= null) {
							otask.onUpdateOut(curOutString);
						}
					innerProcess = null;
				} catch (IOException e) {
					otask.onUpdateOut(e.getMessage());
				}	
			    otask.onStop();
			}
			
		});
		innerThread.start();
	}

	@Override
	public void stop() {
		if (innerProcess!= null){
			innerProcess.destroy();
		}
	}

}
