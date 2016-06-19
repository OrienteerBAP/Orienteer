package org.orienteer.bpm.camunda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.AbstractPersistenceSession;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbEntityOperation;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

public class OPersistenceSession extends AbstractPersistenceSession {
	
	private static final Logger LOG = LoggerFactory.getLogger(OPersistenceSession.class);
	
	private final ODatabaseDocumentTx db;
	
	public OPersistenceSession(ODatabaseDocumentTx db) {
		this.db = db;
	}

	public static void staticInit(OProcessEngineConfiguration config) {
	    //TODO
	}
	
	public ODatabaseDocumentTx getDatabase() {
		return db;
	}
	
	@Override
	public List<?> selectList(String statement, Object parameter) {
		LOG.info("selectList: '"+statement+"' with '"+parameter+"' of class "+(parameter!=null?parameter.getClass():"NULL"));
		return new ArrayList<Object>();
//		return db.query(new OSQLSynchQuery<>(statement), parameter);
	}

	@Override
	public <T extends DbEntity> T selectById(Class<T> type, String id) {
		LOG.info("selectById: "+type+" id="+id);
		return HandlersManager.get().getHandler(type).read(id, this);
	}

	@Override
	public Object selectOne(String statement, Object parameter) {
		LOG.info("selectOne: '"+statement+"' with '"+parameter+"' of class "+(parameter!=null?parameter.getClass():"NULL"));
		return null;
//		List<Object> ret = db.query(new OSQLSynchQuery<>(statement, 1), parameter);
//		return ret!=null && !ret.isEmpty()? ret.get(0):null;
	}

	@Override
	public void lock(String statement, Object parameter) {
		LOG.info("lock: '"+statement+"' with '"+parameter+"' of class "+(parameter!=null?parameter.getClass():"NULL"));
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
		//db.freeze();
	}

	@Override
	public void close() {
		db.close();
	}

	@Override
	protected void insertEntity(DbEntityOperation operation) {
		HandlersManager.get().getHandler(operation.getEntityType()).create(operation.getEntity(), this);
	}

	@Override
	protected void deleteEntity(DbEntityOperation operation) {
		HandlersManager.get().getHandler(operation.getEntityType()).delete(operation.getEntity(), this);
	}

	@Override
	protected void deleteBulk(DbBulkOperation operation) {
		LOG.info("deleteBulk: statement="+operation.getStatement());
	}

	@Override
	protected void updateEntity(DbEntityOperation operation) {
		HandlersManager.get().getHandler(operation.getEntityType()).update(operation.getEntity(), this);
	}

	@Override
	protected void updateBulk(DbBulkOperation operation) {
		LOG.info("updateBulk: statement="+operation.getStatement());
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
