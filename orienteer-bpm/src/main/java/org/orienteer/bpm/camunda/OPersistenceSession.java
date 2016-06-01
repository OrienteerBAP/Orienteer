package org.orienteer.bpm.camunda;

import java.util.List;

import org.camunda.bpm.engine.impl.db.AbstractPersistenceSession;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbEntityOperation;

public class OPersistenceSession extends AbstractPersistenceSession {
	
	public OPersistenceSession() {
	}

	public static void staticInit(OProcessEngineConfiguration config) {
	    //TODO
	  }

	
	@Override
	public List<?> selectList(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DbEntity> T selectById(Class<T> type, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object selectOne(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lock(String statement, Object parameter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dbSchemaCheckVersion() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void insertEntity(DbEntityOperation operation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deleteEntity(DbEntityOperation operation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deleteBulk(DbBulkOperation operation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateEntity(DbEntityOperation operation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateBulk(DbBulkOperation operation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getDbVersion() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHistoryTablePresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIdentityTablePresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCmmnTablePresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCmmnHistoryTablePresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDmnTablePresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDmnHistoryTablePresent() {
		// TODO Auto-generated method stub
		return false;
	}

}
