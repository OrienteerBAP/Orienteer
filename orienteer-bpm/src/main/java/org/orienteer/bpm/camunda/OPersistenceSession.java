package org.orienteer.bpm.camunda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.AbstractPersistenceSession;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbEntityOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class OPersistenceSession extends AbstractPersistenceSession {
	
	private static final Logger LOG = LoggerFactory.getLogger(OPersistenceSession.class);
	
	private static final Map<String, String> STATEMENT_MAPPING = new HashMap<>();
	
	{
//		STATEMENT_MAPPING.put("selectLatestResourcesByDeploymentName", value);
	}
	
	private final OObjectDatabaseTx db;
	
	public OPersistenceSession(ODatabaseDocumentTx db) {
		this.db = new OObjectDatabaseTx(db);
	}

	public static void staticInit(OProcessEngineConfiguration config) {
	    //TODO
	  }

	
	@Override
	public List<?> selectList(String statement, Object parameter) {
		LOG.info("selectList: '"+statement+"' with '"+parameter+"'");
		return db.query(new OSQLSynchQuery<>(statement), parameter);
	}

	@Override
	public <T extends DbEntity> T selectById(Class<T> type, String id) {
		db.getEntityManager().registerEntityClass(type);
		return (T)selectOne("select from "+type.getSimpleName()+" where id = ?", id);
//		return db.load(new ORecordId(id));
	}

	@Override
	public Object selectOne(String statement, Object parameter) {
		LOG.info("selectOne: '"+statement+"' with '"+parameter+"'");
		List<Object> ret = db.query(new OSQLSynchQuery<>(statement, 1), parameter);
		return ret!=null && !ret.isEmpty()? ret.get(0):null;
	}

	@Override
	public void lock(String statement, Object parameter) {

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
		db.freeze();
	}

	@Override
	public void close() {
		db.close();
	}

	@Override
	protected void insertEntity(DbEntityOperation operation) {
		LOG.info("Registering: "+ operation.getEntityType());
		db.getEntityManager().registerEntityClass(operation.getEntityType());
		db.attachAndSave(operation.getEntity());
	}

	@Override
	protected void deleteEntity(DbEntityOperation operation) {
		LOG.info("Registering: "+ operation.getEntityType());
		db.getEntityManager().registerEntityClass(operation.getEntityType());
		db.delete(operation.getEntity());
	}

	@Override
	protected void deleteBulk(DbBulkOperation operation) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void updateEntity(DbEntityOperation operation) {
		LOG.info("Registering: "+ operation.getEntityType());
		db.getEntityManager().registerEntityClass(operation.getEntityType());
		db.save(operation.getEntity());
	}

	@Override
	protected void updateBulk(DbBulkOperation operation) {
		db.command(new OCommandScript(operation.getStatement()));
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
