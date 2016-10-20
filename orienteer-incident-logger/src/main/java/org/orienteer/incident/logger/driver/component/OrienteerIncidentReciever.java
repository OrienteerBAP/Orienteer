package org.orienteer.incident.logger.driver.component;

import ru.asm.utils.incident.logger.IncidentLogger;
import ru.asm.utils.incident.logger.core.IReciever;
import ru.asm.utils.incident.logger.core.IServer;
import ru.asm.utils.incident.logger.core.Server;

public class OrienteerIncidentReciever implements IReciever{
	IServer server;
	public static OrienteerIncidentReciever INSTANCE = new OrienteerIncidentReciever();// just stub. non-thread-sefety
	
	private OrienteerIncidentReciever() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setServer(Server server) {
		this.server = server;
	}

	@Override
	public void recieve(String data) {
		if (server!=null){
			server.onRecieve(data);
		}
	}

}
