package org.orienteer.etl.tasks;

import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.etl.component.OETLConfig;
import org.orienteer.etl.component.OrienteerETLProcessor;

/**
 * 
 * Task session callback for {@link OETLConfig}
 *
 */
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
//		processor.forceStop();
	}

}
