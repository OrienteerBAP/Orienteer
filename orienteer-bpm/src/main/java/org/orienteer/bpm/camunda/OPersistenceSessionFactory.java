package org.orienteer.bpm.camunda;

import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

public class OPersistenceSessionFactory implements SessionFactory{

	@Override
	public Class<?> getSessionType() {
		return PersistenceSession.class;
	}

	@Override
	public Session openSession() {
		return new OPersistenceSession((ODatabaseDocumentTx) ODatabaseRecordThreadLocal.INSTANCE.get());
	}

}
