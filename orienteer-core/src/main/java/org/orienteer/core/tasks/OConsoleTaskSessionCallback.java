package org.orienteer.core.tasks;

/**
 * Callback for {@link OConsoleTask} 
 *
 */
public class OConsoleTaskSessionCallback implements ITaskSessionCallback{

	private volatile Process innerProcess;

	public OConsoleTaskSessionCallback(Process innerProcess) {
		this.innerProcess = innerProcess;
	}

	@Override
	public void stop() throws Exception {
		if (innerProcess!= null){
			innerProcess.destroy();
		}		
	}
}
