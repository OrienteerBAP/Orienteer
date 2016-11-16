package org.orienteer.bpm.camunda;

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.TenantQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.identity.db.DbReadOnlyIdentityServiceProvider;
import org.camunda.bpm.engine.impl.identity.db.DbTenantQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.AbstractManager;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.UserEntityHandler;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.security.OUser;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

/**
 * Slightly custom IdentityServiceProvider 
 */
public class OIdentityServiceProvider extends DbReadOnlyIdentityServiceProvider {

	@Override
	public boolean checkPassword(String userId, String password) {
		
		OPersistenceSession session  = (OPersistenceSession)getSession(PersistenceSession.class);
		OUser oUser = session.getDatabase().getMetadata().getSecurity().getUser(userId);
		return oUser!=null?oUser.checkPassword(password):false;
	}

	@Override
	public TenantEntity findTenantById(String tenantId) {
		return null;
	}

	@Override
	public long findTenantCountByQueryCriteria(DbTenantQueryImpl query) {
		return 0;
	}

	@Override
	public List<Tenant> findTenantByQueryCriteria(DbTenantQueryImpl query) {
		return Collections.EMPTY_LIST;
	}
	
	

}
