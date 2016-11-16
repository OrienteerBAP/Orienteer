package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.hook.ORecordHook.TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.identity.TenantQuery;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * OrientDB doesn't support multi-tenancy: so this is dummy implementation
 */
public class TenantEntityHandler implements IEntityHandler<TenantEntity> {

	@Override
	public void create(TenantEntity entity, OPersistenceSession session) {
		throw new ProcessEngineException("Orienteer doesn't support multitenancy");
	}

	@Override
	public TenantEntity read(String id, OPersistenceSession session) {
		return null;
	}
	
	@Override
	public ODocument readAsDocument(String id, OPersistenceSession session) {
		return null;
	}

	@Override
	public void update(TenantEntity entity, OPersistenceSession session) {
		throw new ProcessEngineException("Orienteer doesn't support multitenancy");
	}

	@Override
	public void delete(TenantEntity entity, OPersistenceSession session) {
		throw new ProcessEngineException("Orienteer doesn't support multitenancy");
	}

	@Override
	public TenantEntity mapToEntity(ODocument doc, TenantEntity entity, OPersistenceSession session) {
		return entity;
	}

	@Override
	public ODocument mapToODocument(TenantEntity entity, ODocument doc, OPersistenceSession session) {
		throw new ProcessEngineException("Orienteer doesn't support multitenancy");
	}

	@Override
	public boolean hasNeedInCache() {
		return false;
	}

	@Override
	public Class<TenantEntity> getEntityClass() {
		return TenantEntity.class;
	}

	@Override
	public String getSchemaClass() {
		return null;
	}
	
	@Override
	public String getPkField() {
		return null;
	}

	@Override
	public boolean supportsStatement(String statement) {
		return statement.startsWith("selectTenant");
	}

	@Override
	public void applySchema(OSchemaHelper helper) {
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
	}

	@Override
	public List<TenantEntity> selectList(String statement, Object parameter, OPersistenceSession session) {
		return new ArrayList<>();
	}

	@Override
	public TenantEntity selectOne(String statement, Object parameter, OPersistenceSession session) {
		return null;
	}

	@Override
	public void lock(String statement, Object parameter, OPersistenceSession session) {
	}

	@Override
	public void deleteBulk(DbBulkOperation operation, OPersistenceSession session) {
		
	}

	@Override
	public void updateBulk(DbBulkOperation operation, OPersistenceSession session) {
	}
	
	@Override
	public RESULT onTrigger(ODatabaseDocument db, ODocument doc, TYPE iType) {
		return RESULT.RECORD_NOT_CHANGED;
	}

}
