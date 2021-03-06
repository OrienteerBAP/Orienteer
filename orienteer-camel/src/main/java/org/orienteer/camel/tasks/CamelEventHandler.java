package org.orienteer.camel.tasks;

import java.util.EventObject;

import org.apache.camel.CamelContext;
import org.apache.camel.management.event.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.orienteer.camel.component.IOIntegrationConfig;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.tasks.IOTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To handle camel events
 *
 */
public class CamelEventHandler extends EventNotifierSupport{
	
	private static final Logger LOG = LoggerFactory.getLogger(CamelEventHandler.class);
	private ITaskSessionCallback callback;
	private volatile OCamelTaskSession taskSession;
	private IOIntegrationConfig config;
	private CamelContext context;
	
	public CamelEventHandler(ITaskSessionCallback callback,IOIntegrationConfig config,CamelContext context) {
		this.callback = callback;
		this.config = config;
		this.context = context;
	}

	@Override
	public void notify(EventObject event) throws Exception {
        if (event instanceof ExchangeSentEvent) {
        	ExchangeSentEvent sent = (ExchangeSentEvent) event;
        	String logRecord ="Took " + sent.getTimeTaken() + " millis to send to: " + sent.getEndpoint(); 
//        	LOG.info(logRecord);
        	taskSession.incrementCurrentProgress();
        }
//        LOG.info("Event = "+ event);		
    	taskSession.appendOut(event.toString());
	}

	@Override
	public boolean isEnabled(EventObject event) {
		return true;
	}
	
	public void onAllRoutesComplete(){
		if(taskSession!=null) {
			taskSession.finish();
			taskSession = null;
		}
	}
	
	@Override
	protected void doStart() throws Exception {
        LOG.info(Thread.currentThread().getName());		

//		if (taskSession == null){
			taskSession = new OCamelTaskSession();
			taskSession.setOTask(config);
			taskSession.setCallback(callback);
			taskSession.setDeleteOnFinish(config.isAutodeleteSessions());
			taskSession.setConfig(config.getDocument().getIdentity().toString());
			taskSession.setFinalProgress(context.getRoutes().size());
			taskSession.start();
//		}
		super.doStart();
	}
	
	@Override
	protected void doStop() throws Exception {
		if(taskSession!=null) taskSession.interrupt();
		super.doStop();
	}
	

}
