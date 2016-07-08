package org.orienteer.bpm.camunda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.AbstractPersistenceSession;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.EntityLoadListener;
import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbEntityOperation;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * OrientDB enalbes {@link PersistenceSession} 
 */
public class OPersistenceSession extends AbstractPersistenceSession {
	
	private static final Logger LOG = LoggerFactory.getLogger(OPersistenceSession.class);
	
	private ODatabaseDocumentTx db;
	
	private BiMap<String, OIdentifiable> idToOIdentifiableCache = HashBiMap.create(10);
	private Map<String, DbEntity> entitiesCache = new HashMap<>();
	
	public OPersistenceSession(ODatabaseDocumentTx db) {
		this.db = db;
	}

	public static void staticInit(OProcessEngineConfiguration config) {
	    //TODO
	}
	
	public ODatabaseDocumentTx getDatabase() {
		return db;
	}
	
	public OSchema getSchema() {
		return db.getMetadata().getSchema();
	}
	
	public OClass getClass(String className) {
		return getSchema().getClass(className);
	}
	
	public void fireEntityLoaded(ODocument sourceDoc, Object object, boolean hasNeedInCache) {
		super.fireEntityLoaded(object);
		if(object instanceof DbEntity) {
			DbEntity entity = (DbEntity) object;
			cacheODocument(sourceDoc, entity.getId());
			if(hasNeedInCache) entitiesCache.put(entity.getId(), entity);
		}
	}
	
	public void cacheODocument(ODocument doc, String id) {
		ORID orid = doc.getIdentity();
		idToOIdentifiableCache.put(id, orid.isPersistent()?orid:doc);
	}
	
	public OIdentifiable lookupOIdentifiableForIdInCache(String id) {
		return idToOIdentifiableCache.get(id);
	}
	
	public DbEntity lookupEntityInCache(String id) {
		return entitiesCache.get(id);
	}
	
	@Override
	public List<?> selectList(String statement, Object parameter) {
		db.activateOnCurrentThread();
		IEntityHandler<?> handler = HandlersManager.get().getHandlerSafe(statement);
		if(handler!=null) {
			return handler.selectList(statement, parameter, this);
		} else {
			LOG.error("Handler 'selectList' for statement '"+statement+"' was not found");
			return new ArrayList<Object>();
		}
	}

	@Override
	public <T extends DbEntity> T selectById(Class<T> type, String id) {
		db.activateOnCurrentThread();
		return (T) HandlersManager.get().getHandler(type).read(id, this);
	}

	@Override
	public Object selectOne(String statement, Object parameter) {
		db.activateOnCurrentThread();
		IEntityHandler<?> handler = HandlersManager.get().getHandlerSafe(statement);
		if(handler!=null) {
			return handler.selectOne(statement, parameter, this);
		} else {
			LOG.error("Handler 'selectOne' for statement '"+statement+"' was not found");
			return null;
		}
	}

	@Override
	public void lock(String statement, Object parameter) {
		db.activateOnCurrentThread();
		IEntityHandler<?> handler = HandlersManager.get().getHandlerSafe(statement);
		if(handler!=null) {
			handler.lock(statement, parameter, this);
		} else {
			LOG.error("Handler 'lock' for statement '"+statement+"' was not found");
		}
	}

	@Override
	public void commit() {
		db.commit();
	}

	@Override
	public void rollback() {
		db.rollback();
	}

	@Override
	public void dbSchemaCheckVersion() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		/*boolean isInTransaction = db.getTransaction().isActive();
		db.commit();
		if(isInTransaction) db.begin();*/
	}

	@Override
	public void close() {
		db.close();
		db = null;
	}
	
	@Override
	protected void insertEntity(DbEntityOperation operation) {
		db.activateOnCurrentThread();
		HandlersManager.get().getHandler(operation.getEntityType()).create(operation.getEntity(), this);
	}

	@Override
	protected void deleteEntity(DbEntityOperation operation) {
		db.activateOnCurrentThread();
		HandlersManager.get().getHandler(operation.getEntityType()).delete(operation.getEntity(), this);
	}

	@Override
	protected void deleteBulk(DbBulkOperation operation) {
		db.activateOnCurrentThread();
		IEntityHandler<?> handler = HandlersManager.get().getHandlerSafe(operation.getStatement());
		if(handler!=null) {
			handler.deleteBulk(operation, this);
		} else {
			LOG.error("Handler 'deleteBulk' for statement '"+operation.getStatement()+"' was not found");
		}
	}

	@Override
	protected void updateEntity(DbEntityOperation operation) {
		db.activateOnCurrentThread();
		HandlersManager.get().getHandler(operation.getEntityType()).update(operation.getEntity(), this);
	}

	@Override
	protected void updateBulk(DbBulkOperation operation) {
		db.activateOnCurrentThread();
		IEntityHandler<?> handler = HandlersManager.get().getHandlerSafe(operation.getStatement());
		if(handler!=null) {
			handler.updateBulk(operation, this);
		} else {
			LOG.error("Handler 'updateBulk' for statement '"+operation.getStatement()+"' was not found");
		}
	}

	@Override
	protected String getDbVersion() {
		return "OrientDB";
	}

	@Override
	protected void dbSchemaCreateIdentity() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaCreateHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaCreateEngine() {
		
	}

	@Override
	protected void dbSchemaCreateCmmn() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaCreateCmmnHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaCreateDmn() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaCreateDmnHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaDropIdentity() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaDropHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaDropEngine() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaDropCmmn() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaDropCmmnHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaDropDmn() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dbSchemaDropDmnHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isEngineTablePresent() {
		return true;
	}

	@Override
	public boolean isHistoryTablePresent() {
		return false;
	}

	@Override
	public boolean isIdentityTablePresent() {
		return false;
	}

	@Override
	public boolean isCmmnTablePresent() {
		return false;
	}

	@Override
	public boolean isCmmnHistoryTablePresent() {
		return false;
	}

	@Override
	public boolean isDmnTablePresent() {
		return false;
	}

	@Override
	public boolean isDmnHistoryTablePresent() {
		return false;
	}

}
