package org.orienteer.camel.component;

import java.util.EventObject;

import org.apache.camel.Exchange;
import org.apache.camel.management.event.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
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
	private String logId;
	
	public CamelEventHandler(String logId) {
		this.logId = logId;
	}

	@Override
	public void notify(EventObject event) throws Exception {
        if (event instanceof ExchangeSentEvent) {
        	ExchangeSentEvent sent = (ExchangeSentEvent) event;
        	//sent.getEndpoint().
        	//sent.getExchange().setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
//        	ODatabase db = OrientDbWebSession.get().getDatabase().activateOnCurrentThread();
        	String logRecord ="Took " + sent.getTimeTaken() + " millis to send to: " + sent.getEndpoint(); 
        	LOG.info(logRecord);		
//        	db.command(new OCommandSQL("update "+logId+" add records = ?")).execute(logRecord);
        }
        LOG.info("Event = "+ event);		
	}

	@Override
	public boolean isEnabled(EventObject event) {
		return true;
	}
	
	public void onAllRoutesComplete(){
		
	}

}
