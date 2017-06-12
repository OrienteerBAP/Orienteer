package org.orienteer.orienteerEtl.tasks;

import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.orienteerEtl.component.OrienteerETLProcessor;

public class OETLTaskSessionCallback implements ITaskSessionCallback {
	private OrienteerETLProcessor processor;
	private Thread initialThread;
	
	public OETLTaskSessionCallback(Thread initialThread,OrienteerETLProcessor processor) {
		this.processor = processor;
		this.initialThread = initialThread;
	}
	
	@Override
	public void interrupt() throws Exception {
		initialThread.interrupt();
		Thread.sleep(200);
		processor.forceStop();
	}

}
