package org.orienteer.inclogger.server;

import org.orienteer.logger.core.interfaces.IReceiver;
import org.orienteer.logger.core.interfaces.IServer;

/**
 *  Receiver module for
 *  
 *	ATTENTION! This module - only for thread-safe IData!!!
 */
public class OIncidentReceiver implements IReceiver{
	
	public static final OIncidentReceiver INSTANCE = new OIncidentReceiver();

	IServer server;
	
	private OIncidentReceiver() {
	}

	@Override
	public void setServer(IServer server) {
		this.server = server;
	}

	@Override
	public void receive(String data) {
		if (server!=null){
			server.onReceive(data);
		}
	}

}
