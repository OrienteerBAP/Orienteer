package org.orienteer.bpm.camunda;

import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

public class OPersistenceSessionFactory implements SessionFactory{

	@Override
	public Class<?> getSessionType() {
		return PersistenceSession.class;
	}

	@Override
	public Session openSession() {
		// TODO Auto-generated method stub
		return null;
	}

}
