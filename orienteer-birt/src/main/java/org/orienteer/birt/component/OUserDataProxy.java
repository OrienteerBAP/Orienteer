package org.orienteer.birt.component;

import org.orienteer.birt.orientdb.impl.IUserDataProxy;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;;

/**
 * OrientDB BIRT plugin injection for overriding DB user name and password  
 *
 */
public class OUserDataProxy implements IUserDataProxy {

	@Override
	public String getUserName() {
		OrientDbWebSession session = OrientDbWebSession.get();
		return session.getUsername();
	}

	@Override
	public String getPassword() {
		OrientDbWebSession session = OrientDbWebSession.get();
		return session.getPassword();
	}

}
