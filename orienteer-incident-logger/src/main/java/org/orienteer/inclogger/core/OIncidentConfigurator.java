package org.orienteer.inclogger.core;

import org.orienteer.inclogger.client.OIncidentLogger;
import org.orienteer.inclogger.client.OIncidentLoggerData;
import org.orienteer.inclogger.client.OIncidentSender;
import org.orienteer.inclogger.core.interfaces.ICoder;
import org.orienteer.inclogger.core.interfaces.IConfigurator;
import org.orienteer.inclogger.core.interfaces.IData;
import org.orienteer.inclogger.core.interfaces.IDecoder;
import org.orienteer.inclogger.core.interfaces.ILogger;
import org.orienteer.inclogger.core.interfaces.IReceiver;
import org.orienteer.inclogger.core.interfaces.ISender;
import org.orienteer.inclogger.server.OIncidentReceiver;
/**
 * 
 */
public class OIncidentConfigurator implements IConfigurator {

	IData data;
	ISender sender;
	IReceiver receiver;
	
	public OIncidentConfigurator() {
		data = new OIncidentData();
		sender = new OIncidentSender("admin","admin","http://localhost:8080/rest/incident");
		receiver = OIncidentReceiver.INSTANCE;
	}

	@Override
	public ICoder getCoder() {
		return null;
	}

	@Override
	public IDecoder getDecoder() {
		return null;
	}

	@Override
	public ISender getSender() {
		return sender;
	}

	@Override
	public IReceiver getReceiver() {
		return receiver;
	}

	@Override
	public IData getServerData() {
		return data;
	}

	@Override
	public IData getClientData() {
		return data;
	}

	@Override
	public ILogger makeLogger() {
		return new OIncidentLogger(new OIncidentLoggerData()) ;
	}


}
