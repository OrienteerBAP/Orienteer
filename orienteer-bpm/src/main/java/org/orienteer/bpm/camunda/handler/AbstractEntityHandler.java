package org.orienteer.bpm.camunda.handler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.util.string.Strings;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.HasDbRevision;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import com.github.raymanrt.orientqb.query.Operator;
import com.github.raymanrt.orientqb.query.Parameter;
import com.github.raymanrt.orientqb.query.Query;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeToken;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;


import static com.github.raymanrt.orientqb.query.Clause.*;

public abstract class AbstractEntityHandler<T extends DbEntity> implements IEntityHandler<T> {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private TypeToken<T> type = new TypeToken<T>(getClass()) {};
	
	private final String schemaClass;
	
	protected Map<String, String> mappingFromEntityToDoc;
	protected Map<String, String> mappingFromDocToEntity;
	
	private Map<String, Method> statementMethodsMapping = new HashMap<>();
	
	public AbstractEntityHandler(String schemaClass) {
		this.schemaClass = schemaClass;
		for(Method method:getClass().getMethods()) {
			Statement statement = method.getAnnotation(Statement.class);
			if(statement!=null) {
				String st = statement.value();
				if(Strings.isEmpty(st)) st = method.getName();
				statementMethodsMapping.put(st, method);
			}
		}
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
		if(mappingFromDocToEntity==null || mappingFromEntityToDoc==null){
			initMapping(db);
		}
	}
	
	protected void initMapping(ODatabaseDocument db) {
		mappingFromDocToEntity = new HashMap<>();
		mappingFromEntityToDoc = new HashMap<>();
		OClass oClass = db.getMetadata().getSchema().getClass(getSchemaClass());
		Class<T> entityClass = getEntityClass();
		for(PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(entityClass)) {
			if(oClass.getProperty(pd.getName())!=null) {
				mappingFromDocToEntity.put(pd.getName(), pd.getName());
				mappingFromEntityToDoc.put(pd.getName(), pd.getName());
			}
		}
 	}
	
	@Override
	public T mapToEntity(ODocument doc, T entity, OPersistenceSession session) {
		checkMapping(session.getDatabase());
		try {
			if(entity==null) {
				entity = getEntityClass().newInstance();
			}
			for(Map.Entry<String, String> mapToEntity : mappingFromDocToEntity.entrySet()) {
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(getEntityClass(), mapToEntity.getValue());
				pd.getWriteMethod().invoke(entity, doc.field(mapToEntity.getKey()));
			}
			session.fireEntityLoaded(entity);
			return entity;
		} catch (Exception e) {
			LOG.error("There shouldn't be this exception in case of predefined mapping", e);
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
			for(Map.Entry<String, String> mapToDoc : mappingFromEntityToDoc.entrySet()) {
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

	@Override
	public boolean supportsStatement(String statement) {
		return statementMethodsMapping.containsKey(statement);
	}
	
	protected <T> T invokeStatement(String statement, Object... args) {
		Method method = statementMethodsMapping.get(statement);
		try {
			return (T) method.invoke(this, args);
		} catch (Exception e) {
			throw new IllegalStateException("With good defined handler we should not be here. Method: "+method, e);
		}
	}
	

	@Override
	public List<T> selectList(String statement, Object parameter, OPersistenceSession session) {
		return invokeStatement(statement, session, parameter);
	}

	@Override
	public T selectOne(String statement, Object parameter, OPersistenceSession session) {
		return invokeStatement(statement, session, parameter);
	}

	@Override
	public void lock(String statement, Object parameter, OPersistenceSession session) {
		invokeStatement(statement, session, parameter);
	}

	@Override
	public void deleteBulk(DbBulkOperation operation, OPersistenceSession session) {
		invokeStatement(operation.getStatement(), session, operation.getParameter());
	}

	@Override
	public void updateBulk(DbBulkOperation operation, OPersistenceSession session) {
		invokeStatement(operation.getStatement(), session, operation.getParameter());
	}
	
	protected T querySingle(OPersistenceSession session, String sql, Object... args) {
		ODatabaseDocument db = session.getDatabase();
		List<ODocument> ret = db.query(new OSQLSynchQuery<>(sql, 1), args);
		return ret==null || ret.isEmpty()?null:mapToEntity(ret.get(0), null, session);
	}
	
	protected List<T> queryList(final OPersistenceSession session, String sql, Object... args) {
		ODatabaseDocument db = session.getDatabase();
		List<ODocument> ret = db.query(new OSQLSynchQuery<>(sql), args);
		if(ret==null) return Collections.emptyList();
		return Lists.transform(ret, new Function<ODocument, T>() {

			@Override
			public T apply(ODocument input) {
				return mapToEntity(input, null, session);
			}
		});
	}
	
	protected List<T> query(final OPersistenceSession session, org.camunda.bpm.engine.query.Query<?, ? super T> query, String... ignoreFileds) {
		return query(session, query, null, ignoreFileds);
	}
	
	protected List<T> query(final OPersistenceSession session, org.camunda.bpm.engine.query.Query<?, ? super T> query, Function<Query, Query> queryManger, String... ignoreFileds) {
		List<String> ignore = Arrays.asList(ignoreFileds);
		try {
			ODatabaseDocument db = session.getDatabase();
			OClass schemaClass = db.getMetadata().getSchema().getClass(getSchemaClass());
			Query q = new Query().from(getSchemaClass());
			List<Object> args = new ArrayList<>();
			for(PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(query.getClass())) {
				if(pd.getReadMethod()!=null 
						&& schemaClass.getProperty(pd.getName())!=null
						&& !ignore.contains(pd.getName())) {
					Object value = pd.getReadMethod().invoke(query);
					if(value!=null) {
						q.where(clause(pd.getName(), Operator.EQ, Parameter.PARAMETER));
						args.add(value);
					}
				}
			}
			if(queryManger!=null) q = queryManger.apply(q);
			return queryList(session, q.toString(), args.toArray());
		} catch (Exception e) {
			throw new ProcessEngineException("Problems with read method of "+query.getClass().getName(), e);
		} 
	}
	
	protected List<T> query(final OPersistenceSession session, Map<String, ?> query, String... ignoreFileds) {
		return query(session, query, null, ignoreFileds);
	}
	
	protected List<T> query(final OPersistenceSession session, Map<String, ?> query, Function<Query, Query> queryManger, String... ignoreFileds) {
		List<String> ignore = Arrays.asList(ignoreFileds);
		ODatabaseDocument db = session.getDatabase();
		OClass schemaClass = db.getMetadata().getSchema().getClass(getSchemaClass());
		Query q = new Query().from(getSchemaClass());
		List<Object> args = new ArrayList<>();
		for(Map.Entry<String, ?> entry : query.entrySet()) {
			if(schemaClass.getProperty(entry.getKey())!=null
					&& !ignore.contains(entry.getKey())) {
				Object value = entry.getValue();
				if(value!=null) {
					q.where(clause(entry.getKey(), Operator.EQ, Parameter.PARAMETER));
					args.add(value);
				}
			}
		}
		if(queryManger!=null) q = queryManger.apply(q);
		LOG.info("SQL: "+q);
		LOG.info("Args: "+args);
		List<T> ret = queryList(session, q.toString(), args.toArray());
		LOG.info("Res: "+ret);
		return ret;
	}
	
}
