package org.orienteer.camel.tasks;

import java.util.EventObject;

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
	private OCamelTaskSessionImpl taskSession;
	private String configId;
	
	public CamelEventHandler(ITaskSessionCallback callback,String configId) {
		this.callback = callback;
		this.configId = configId;
		
	}

	@Override
	public void notify(EventObject event) throws Exception {
        if (event instanceof ExchangeSentEvent) {
        	ExchangeSentEvent sent = (ExchangeSentEvent) event;
        	String logRecord ="Took " + sent.getTimeTaken() + " millis to send to: " + sent.getEndpoint(); 
        	LOG.info(logRecord);
        }
        LOG.info("Event = "+ event);		
    	taskSession.onProcess().
    		incrementCurrentProgress().
    		appendOut(event.toString()).
    	end();
	}

	@Override
	public boolean isEnabled(EventObject event) {
		return true;
	}

	private void onStop() {
		if (taskSession != null){
			taskSession.onStop();
			taskSession=null;
		}
	}
	
	public void onAllRoutesComplete(){
		onStop();
	}
	
	@Override
	protected void doStart() throws Exception {
		if (taskSession == null){
			taskSession = new OCamelTaskSessionImpl();
			taskSession.onStart().
				setCallback(callback).
				setDeleteOnFinish(true).
				setConfig(configId).
			end();
		}
		super.doStart();
	}
	
	@Override
	protected void doStop() throws Exception {
		onStop();
		super.doStop();
	}

}
