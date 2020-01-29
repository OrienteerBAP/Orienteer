package org.orienteer.bpm.camunda;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;

/**
 * Factory class for {@link PersistenceSession} 
 */
public class OPersistenceSessionFactory implements SessionFactory{

	private static final Logger LOG = LoggerFactory.getLogger(OPersistenceSessionFactory.class);


	@Override
	public Class<?> getSessionType() {
		return PersistenceSession.class;
	}

	@Override
	public Session openSession() {
		OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
		IOrientDbSettings settings = app.getOrientDbSettings();
		ODatabaseDocumentInternal oldDb = ODatabaseRecordThreadLocal.instance().get();
		LOG.info("[{}] active database before open: {}", Thread.currentThread().getName(), ODatabaseRecordThreadLocal.instance().get());
		ODatabaseSession db = settings.getContext().open(settings.getDbName(), settings.getAdminUserName(), settings.getAdminPassword());
		oldDb.activateOnCurrentThread();
		LOG.info("[{}] new database                 {}", Thread.currentThread().getName(), db);
		LOG.info("[{}] active database after  open: {}", Thread.currentThread().getName(), ODatabaseRecordThreadLocal.instance().get());
		return new OPersistenceSession(db);
	}

}
