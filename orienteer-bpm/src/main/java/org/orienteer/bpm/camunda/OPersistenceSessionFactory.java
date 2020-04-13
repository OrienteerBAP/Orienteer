package org.orienteer.bpm.camunda;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.orienteer.core.OrienteerWebApplication;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;

/**
 * Factory class for {@link PersistenceSession} 
 */
public class OPersistenceSessionFactory implements SessionFactory {

	@Override
	public Class<?> getSessionType() {
		return PersistenceSession.class;
	}

	@Override
	public Session openSession() {
		OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
		IOrientDbSettings settings = app.getOrientDbSettings();
		ODatabaseSession db = settings.getContext().cachedPool(settings.getDbName(), settings.getAdminUserName(), settings.getAdminPassword())
				.acquire();
		return new OPersistenceSession(db);
	}

}
