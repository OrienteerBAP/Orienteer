package org.orienteer.camel.tasks;

import java.util.EventObject;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.management.event.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * To handle camel events
 *
 */
public class CamelEventHandler extends EventNotifierSupport{
	
	private static final Logger LOG = LoggerFactory.getLogger(CamelEventHandler.class);
	private ITaskSessionCallback callback;
	private volatile OCamelTaskSession taskSession;
	private String configId;
	private CamelContext context;
	
	public CamelEventHandler(ITaskSessionCallback callback,String configId,CamelContext context) {
		this.callback = callback;
		this.configId = configId;
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
			taskSession.setCallback(callback);
			taskSession.setDeleteOnFinish(false);
			taskSession.setConfig(configId);
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
