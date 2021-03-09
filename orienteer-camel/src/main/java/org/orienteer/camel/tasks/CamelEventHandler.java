package org.orienteer.camel.tasks;

import java.util.EventObject;

import org.apache.camel.CamelContext;
import org.apache.camel.management.event.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.orienteer.core.tasks.ITaskSessionCallback;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.ITaskSession.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To handle camel events
 *
 */
public class CamelEventHandler extends EventNotifierSupport{
	
	private static final Logger LOG = LoggerFactory.getLogger(CamelEventHandler.class);
	private OCamelContext ctx;
	
	public CamelEventHandler(OCamelContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void notify(EventObject event) throws Exception {
        if (event instanceof ExchangeSentEvent) {
        	ExchangeSentEvent sent = (ExchangeSentEvent) event;
        	String logRecord ="Took " + sent.getTimeTaken() + " millis to send to: " + sent.getEndpoint(); 
//        	LOG.info(logRecord);
        	ctx.getRuntimeSession().incrementCurrentProgress();
        }
//        LOG.info("Event = "+ event);		
        ctx.getPersistedSession().appendOutput(event.toString());
        ctx.persist();
	}

	@Override
	public boolean isEnabled(EventObject event) {
		return true;
	}
	
	public void onAllRoutesComplete(){
		ctx.getRuntimeSession().finish();
	}
	
	@Override
	protected void doStart() throws Exception {
		ctx.getRuntimeSession().start();
		super.doStart();
	}
	
	@Override
	protected void doStop() throws Exception {
		if(Status.RUNNING.equals(ctx.getRuntimeSession().getStatus()))
				ctx.getRuntimeSession().interrupt();
		ctx.getRuntimeSession().finish();
		super.doStop();
	}
	

}
