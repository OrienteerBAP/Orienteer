package org.orienteer.bpm.camunda.handler;

import java.util.List;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.hook.ORecordHook.TYPE;
import com.orientechnologies.orient.core.metadata.schema.OImmutableClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Interface for any kind of handlers of {@link DbEntity} 
 * @param <T>
 */
public interface IEntityHandler<T extends DbEntity> {
	
	public static final String BPM_ENTITY_CLASS = "BPMEntity";
	
	public void create(T entity, OPersistenceSession session);
	public T read(String id, OPersistenceSession session);
	public void update(T entity, OPersistenceSession session);
	public void delete(T entity, OPersistenceSession session);
	
	public ODocument readAsDocument(String id, OPersistenceSession session);
	
	public T mapToEntity(ODocument doc, T entity, OPersistenceSession session);
	public ODocument mapToODocument(T entity, ODocument doc, OPersistenceSession session);
	
	public boolean hasNeedInCache();
	
	public Class<T> getEntityClass();
	public String getSchemaClass();
	public String getPkField();
	
	public boolean supportsStatement(String statement);
	
	public void applySchema(OSchemaHelper helper);
	public void applyRelationships(OSchemaHelper helper);
	
	public List<T> selectList(String statement, Object parameter, OPersistenceSession session);
	public T selectOne(String statement, Object parameter, OPersistenceSession session);
	public void lock(String statement, Object parameter, OPersistenceSession session);
	public void deleteBulk(DbBulkOperation operation, OPersistenceSession session);
	public void updateBulk(DbBulkOperation operation, OPersistenceSession session);
	
	public RESULT onTrigger(ODatabaseDocument db, ODocument doc, TYPE iType);
}
