package org.orienteer.birt.component;

import org.orienteer.birt.orientdb.impl.IUserDataProxy;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;;

public class OUserDataProxy implements IUserDataProxy {

	public OUserDataProxy() {
	}

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
