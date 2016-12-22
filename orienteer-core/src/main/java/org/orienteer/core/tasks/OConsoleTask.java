package org.orienteer.core.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OConsoleTask implements IRealTask {

	private OTask otask;
	Process innerProcess;
	
	public OConsoleTask() {
	}
	
	@Override
	public void setOTask(OTask otask) {
		this.otask = otask;
	}
	

	@Override
	public void start(final OTaskData data) {
		otask.onStart();
			try {
				innerProcess = Runtime.getRuntime().exec(data.toString());
				BufferedReader reader =  new BufferedReader(new InputStreamReader(innerProcess.getInputStream()));
				String curOutString = "";
					while ((curOutString = reader.readLine())!= null) {
						otask.onUpdateOut(curOutString);
					}
			} catch (IOException e) {
				otask.onUpdateOut(e.getMessage());
			}	
	    otask.onStop();
			
	}

	@Override
	public void stop() {
		if (innerProcess!= null){
			innerProcess.destroy();
		}
		otask.onStop();
	}

}
