package org.orienteer.incident.logger.driver.component;

import ru.asm.utils.incident.logger.core.IReceiver;
import ru.asm.utils.incident.logger.core.IServer;

/**
 *  Receiver module for
 *  
 *	ATTENTION! This module - only for thread-safe IData!!!
 */
public class OrienteerIncidentReceiver implements IReceiver{
	
	public static final OrienteerIncidentReceiver INSTANCE = new OrienteerIncidentReceiver();

	IServer server;
	
	private OrienteerIncidentReceiver() {
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
