package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OImmutableClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public interface IEntityHandler<T extends DbEntity> {
	
	public static final String BPM_CLASS = "BPM";
	public static final String BPM_ENTITY_CLASS = "BPMEntity";
	public static final String BPM_REVISION_CLASS = "BPMRevision";
	
	public void create(T entity, OPersistenceSession session);
	public T read(String id, OPersistenceSession session);
	public void update(T entity, OPersistenceSession session);
	public void delete(T entity, OPersistenceSession session);
	
	public T mapToEntity(ODocument doc, T entity, OPersistenceSession session);
	public ODocument mapToODocument(T entity, ODocument doc, OPersistenceSession session);
	
	public Class<T> getEntityClass();
	public String getSchemaClass();
	
	public void applySchema(OSchemaHelper helper);
}
