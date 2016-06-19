package org.orienteer.bpm.camunda.handler;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.HasDbRevision;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.reflect.TypeToken;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public abstract class AbstractEntityHandler<T extends DbEntity> implements IEntityHandler<T> {
	
	private TypeToken<T> type = new TypeToken<T>(getClass()) {};
	
	private final String schemaClass;
	
	private BiMap<String, String> mapping;
	
	public AbstractEntityHandler(String schemaClass) {
		this.schemaClass = schemaClass;
	}
	
	@Override
	public Class<T> getEntityClass() {
		return (Class<T>) type.getRawType();
	}
	
	@Override
	public String getSchemaClass() {
		return schemaClass;
	}
	
	@Override
	public void create(T entity, OPersistenceSession session) {
		ODocument doc = mapToODocument(entity, null, session);
		session.getDatabase().save(doc);
	}
	
	@Override
	public T read(String id, OPersistenceSession session) {
		ODocument doc = readAsDocument(id, session);
		return doc==null?null:mapToEntity(doc, null, session);
	}
	
	public ODocument readAsDocument(String id, OPersistenceSession session) {
		ODatabaseDocument db = session.getDatabase();
		List<ODocument> ret = db.query(new OSQLSynchQuery<>("select from "+getSchemaClass()+" where id = ?", 1), id);
		return ret==null || ret.isEmpty()? null : ret.get(0);
	}
	
	@Override
	public void update(T entity, OPersistenceSession session) {
		ODocument doc = readAsDocument(entity.getId(), session);
		mapToODocument(entity, doc, session);
		session.getDatabase().save(doc);
	}
	
	@Override
	public void delete(T entity, OPersistenceSession session) {
		ODatabaseDocument db = session.getDatabase();
		String id = entity.getId();
		db.command(new OCommandSQL("delete from "+getSchemaClass()+" where id = ?")).execute(id);
	}
	
	protected void checkMapping(ODatabaseDocument db) {
		if(mapping==null){
			mapping = constactMapping(db);
		}
	}
	
	protected BiMap<String, String> constactMapping(ODatabaseDocument db) {
		BiMap<String, String> ret = HashBiMap.create();
		OClass oClass = db.getMetadata().getSchema().getClass(getSchemaClass());
		Class<T> entityClass = getEntityClass();
		for(PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(entityClass)) {
			if(oClass.getProperty(pd.getName())!=null) ret.put(pd.getName(), pd.getName());
		}
		return ret;
 	}
	
	@Override
	public T mapToEntity(ODocument doc, T entity, OPersistenceSession session) {
		checkMapping(session.getDatabase());
		try {
			if(entity==null) {
				entity = getEntityClass().newInstance();
			}
			for(Map.Entry<String, String> mapToEntity : mapping.entrySet()) {
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(getEntityClass(), mapToEntity.getValue());
				pd.getWriteMethod().invoke(entity, doc.field(mapToEntity.getKey()));
			}
			return entity;
		} catch (Exception e) {
			throw new IllegalStateException("There shouldn't be this exception in case of predefined mapping", e);
		}
	}

	@Override
	public ODocument mapToODocument(T entity, ODocument doc, OPersistenceSession session) {
		checkMapping(session.getDatabase());
		try {
			if(doc==null) {
				doc = new ODocument(getSchemaClass());
			}
			for(Map.Entry<String, String> mapToDoc : mapping.inverse().entrySet()) {
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(getEntityClass(), mapToDoc.getKey());
				Object value = pd.getReadMethod().invoke(entity);
				if(!Objects.equal(value, doc.field(mapToDoc.getValue()))) {
					doc.field(mapToDoc.getValue(), value);
				}
			}
			return doc;
		} catch (Exception e) {
			throw new IllegalStateException("There shouldn't be this exception in case of predefined mapping", e);
		}
	}

	@Override
	public void applySchema(OSchemaHelper helper) {
		Class<?> clazz = type.getRawType();
		List<String> superClasses = new ArrayList<>();
		if(DbEntity.class.isAssignableFrom(clazz)) superClasses.add(BPM_ENTITY_CLASS);
		if(HasDbRevision.class.isAssignableFrom(clazz)) superClasses.add(BPM_REVISION_CLASS);
		if(superClasses.isEmpty())superClasses.add(BPM_CLASS);
		helper.oClass(schemaClass, superClasses.toArray(new String[superClasses.size()]));
	}
	
}
