package org.orienteer.logger.server;

import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.logger.IOLoggerEventEnhancer;
import org.orienteer.logger.OLoggerEvent;

/**
 * {@link IOLoggerEventEnhancer} to add some web specific to an event 
 */
public class OWebEnhancer implements IOLoggerEventEnhancer {

	@Override
	public OLoggerEvent enhance(OLoggerEvent event) {
		
		RequestCycle cycle = RequestCycle.get();
		if(cycle!=null) {
			OrienteerWebSession session = OrienteerWebSession.get();
			if(session!=null) {
				if(session.isClientInfoAvailable()){
					WebClientInfo clientInfo = session.getClientInfo();
					event.setMetaData("remoteAddress", clientInfo.getProperties().getRemoteAddress());
					event.setMetaData("hostName", clientInfo.getProperties().getHostname());
				}
				if(session.isSignedIn()) {
					event.setMetaData("username", session.getEffectiveUser().getName());
				}
			
			}
			WebRequest request = (WebRequest)cycle.getRequest();
			event.setMetaData("clientUrl", request.getClientUrl());
		}
		return event;
	}

}
