package org.orienteer.incident.logger.driver.component;

import ru.asm.utils.incident.logger.core.DefaultData;
import ru.asm.utils.incident.logger.core.ICoder;
import ru.asm.utils.incident.logger.core.IConfigurator;
import ru.asm.utils.incident.logger.core.IData;
import ru.asm.utils.incident.logger.core.IDecoder;
import ru.asm.utils.incident.logger.core.ILogger;
import ru.asm.utils.incident.logger.core.IReciever;
import ru.asm.utils.incident.logger.core.ISender;
/**
 * 
 */
public class OrienteerIncidentConfigurator implements IConfigurator {

	IData data;
	
	public OrienteerIncidentConfigurator() {
		data = new OrienteerIncidentData();
		
	}

	@Override
	public ICoder getCoder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDecoder getDecoder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISender getSender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReciever getReciever() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IData<?> getServerData() {
		return data;
	}

	@Override
	public IData<?> getClientData() {
		return data;
	}

	@Override
	public ILogger makeLogger() {
		// TODO Auto-generated method stub
		return new OrienteerIncidentLogger(new OrienteerIncidentLoggerData()) ;
	}


}
