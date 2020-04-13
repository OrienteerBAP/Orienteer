package org.orienteer.bpm.camunda;

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.identity.db.DbReadOnlyIdentityServiceProvider;
import org.camunda.bpm.engine.impl.identity.db.DbTenantQueryImpl;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import com.orientechnologies.orient.core.metadata.security.OUser;

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
