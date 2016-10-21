package org.orienteer.incident.logger.driver.component;

import ru.asm.utils.incident.logger.core.ICoder;
import ru.asm.utils.incident.logger.core.IConfigurator;
import ru.asm.utils.incident.logger.core.IData;
import ru.asm.utils.incident.logger.core.IDecoder;
import ru.asm.utils.incident.logger.core.ILogger;
import ru.asm.utils.incident.logger.core.IReceiver;
import ru.asm.utils.incident.logger.core.ISender;
/**
 * 
 */
public class OrienteerIncidentConfigurator implements IConfigurator {

	IData data;
	ISender sender;
	IReceiver receiver;
	
	public OrienteerIncidentConfigurator() {
		data = new OrienteerIncidentData();
		sender = new OrienteerIncidentSender("admin","admin","http://localhost:8080/rest/incident");
		receiver = OrienteerIncidentReceiver.INSTANCE;
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
		return new OrienteerIncidentLogger(new OrienteerIncidentLoggerData()) ;
	}


}
