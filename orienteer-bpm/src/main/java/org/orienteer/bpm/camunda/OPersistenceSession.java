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
			cacheODocument(sourceDoc);
			if(hasNeedInCache) entitiesCache.put((String) sourceDoc.field("id"), entity);
		}
	}
	
	public void cacheODocument(ODocument doc) {
		ORID orid = doc.getIdentity();
//		idToOIdentifiableCache.put((String) doc.field("id"), orid.isPersistent()?orid:doc);
		idToOIdentifiableCache.put((String) doc.field("id"), orid);
	}
	
	/**
	 * Lookup cached {@link OIdentifiable}
	 * @param oid id of an entity stored in DB. Sometimes it's not the same as id of an entity.
	 * @return cached {@link OIdentifiable}
	 */
	public OIdentifiable lookupOIdentifiableForIdInCache(String oid) {
		return idToOIdentifiableCache.get(oid);
	}
	
	/**
	 * Lookup cached {@link DbEntity}
	 * @param oid id of an entity stored in DB. Sometimes it's not the same as id of an entity.
	 * @return cached {@link DbEntity}
	 */
	public DbEntity lookupEntityInCache(String oid) {
		return entitiesCache.get(oid);
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

	}

	@Override
	protected void dbSchemaCreateHistory() {

	}

	@Override
	protected void dbSchemaCreateEngine() {
		
	}

	@Override
	protected void dbSchemaCreateCmmn() {

	}

	@Override
	protected void dbSchemaCreateCmmnHistory() {

	}

	@Override
	protected void dbSchemaCreateDmn() {

	}

	@Override
	protected void dbSchemaCreateDmnHistory() {

	}

	@Override
	protected void dbSchemaDropIdentity() {

	}

	@Override
	protected void dbSchemaDropHistory() {

	}

	@Override
	protected void dbSchemaDropEngine() {

	}

	@Override
	protected void dbSchemaDropCmmn() {

	}

	@Override
	protected void dbSchemaDropCmmnHistory() {

	}

	@Override
	protected void dbSchemaDropDmn() {

	}

	@Override
	protected void dbSchemaDropDmnHistory() {

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
		return true;
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
