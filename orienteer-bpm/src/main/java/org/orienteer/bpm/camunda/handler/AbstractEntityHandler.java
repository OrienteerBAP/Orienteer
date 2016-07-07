package org.orienteer.bpm.camunda.handler;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;
import org.apache.wicket.core.util.lang.PropertyResolver.IGetAndSet;
import org.apache.wicket.util.string.Strings;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.HasDbRevision;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import com.github.raymanrt.orientqb.query.Clause;
import com.github.raymanrt.orientqb.query.Operator;
import com.github.raymanrt.orientqb.query.Parameter;
import com.github.raymanrt.orientqb.query.Query;
import com.github.raymanrt.orientqb.query.core.AbstractQuery;
import com.gitub.raymanrt.orientqb.delete.Delete;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeToken;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;


import static com.github.raymanrt.orientqb.query.Clause.*;

/**
 * Abstract implementation of {@link IEntityHandler} 
 * @param <T> type of {@link DbEntity} to be handled by this instance
 */
public abstract class AbstractEntityHandler<T extends DbEntity> implements IEntityHandler<T> {
	
	private static final PropertyResolverConverter PROPERTY_RESOLVER_CONVERTEER = 
				new PropertyResolverConverter(OrienteerWebApplication.lookupApplication()
												.getConverterLocator(), Locale.getDefault());
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
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
		ORID orid = session.lookupORIDForIdInCache(id);
		if(orid!=null) return orid.getRecord();
		else {
			ODatabaseDocument db = session.getDatabase();
			List<ODocument> ret = db.query(new OSQLSynchQuery<>("select from "+getSchemaClass()+" where id = ?", 1), id);
			return ret==null || ret.isEmpty()? null : ret.get(0);
		}
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
		ORID orid = session.lookupORIDForIdInCache(id);
		if(orid!=null) {
			db.delete(orid);
		} else {
			db.command(new OCommandSQL("delete from "+getSchemaClass()+" where id = ?")).execute(id);
		}
	}
	
	protected void checkMapping(OPersistenceSession session) {
		if(mappingFromDocToEntity==null || mappingFromEntityToDoc==null){
			if(session!=null) initMapping(session);
		}
	}
	
	private Method getGetAndSetter;
	
	protected IGetAndSet getGetAndSetter(Class<?> clazz, String property) {
		try {
			if(getGetAndSetter==null) {
				getGetAndSetter = PropertyResolver.class.getDeclaredMethod("getGetAndSetter", String.class, Class.class);
				getGetAndSetter.setAccessible(true);
			}
			return (IGetAndSet) getGetAndSetter.invoke(null, property, clazz);
		} catch (NoSuchMethodException e) {
			throw new ProcessEngineException("Can't get required method from "+PropertyResolver.class.getSimpleName(), e);
		} catch (InvocationTargetException e) {
			Throwable targetExc = e.getTargetException();
			if(targetExc instanceof WicketRuntimeException) return null;
			else throw new ProcessEngineException("Exception during defining getters and setters", targetExc);
		} catch (Exception e) {
			throw new ProcessEngineException("Can't invoke required method from "+PropertyResolver.class.getSimpleName(), e);
		}
		
	}
	
	protected void initMapping(OPersistenceSession session) {
		mappingFromDocToEntity = new HashMap<>();
		mappingFromEntityToDoc = new HashMap<>();
		OClass oClass = session.getClass(getSchemaClass());
		Class<T> entityClass = getEntityClass();
	
		for(OProperty property : oClass.properties()) {
			String propertyName = property.getName();
			IGetAndSet getAndSet = getGetAndSetter(entityClass, propertyName);
			if(getAndSet!=null) {
				if(getAndSet.getSetter()!=null) mappingFromDocToEntity.put(propertyName, propertyName);
				if(getAndSet.getGetter()!=null) mappingFromEntityToDoc.put(propertyName, propertyName);
			}
		}
		
			/*for(PropertyDescriptor pd : Introspector.getBeanInfo(entityClass).getPropertyDescriptors()) {
				if(oClass.getProperty(pd.getName())!=null) {
					if(pd.getWriteMethod()!=null) mappingFromDocToEntity.put(pd.getName(), pd.getName());
					if(pd.getReadMethod()!=null) mappingFromEntityToDoc.put(pd.getName(), pd.getName());
				}
			}*/
 	}
	
	
	
	@Override
	public T mapToEntity(ODocument doc, T entity, OPersistenceSession session) {
		checkMapping(session);
		try {
			if(hasNeedInCache()) {
				entity = (T)session.lookupEntityInCache((String)doc.field("id"));
				if(entity!=null) return entity;
			}
			if(entity==null) {
				entity = getEntityClass().newInstance();
			}
			for(Map.Entry<String, String> mapToEntity : mappingFromDocToEntity.entrySet()) {
				PropertyResolver.setValue(mapToEntity.getValue(), entity, doc.field(mapToEntity.getKey()), PROPERTY_RESOLVER_CONVERTEER);
			}
			session.fireEntityLoaded(doc, entity, hasNeedInCache());
			return entity;
		} catch (Exception e) {
			logger.error("There shouldn't be this exception in case of predefined mapping", e);
			throw new IllegalStateException("There shouldn't be this exception in case of predefined mapping", e);
		}
	}
	
	@Override
	public boolean hasNeedInCache() {
		return false;
	}

	@Override
	public ODocument mapToODocument(T entity, ODocument doc, OPersistenceSession session) {
		checkMapping(session);
		if(doc==null) {
			doc = new ODocument(getSchemaClass());
		}
		for(Map.Entry<String, String> mapToDoc : mappingFromEntityToDoc.entrySet()) {
			Object value = PropertyResolver.getValue(mapToDoc.getKey(), entity);
			if(!Objects.equal(value, doc.field(mapToDoc.getValue()))) {
				doc.field(mapToDoc.getValue(), value);
			}
		}
		return doc;
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
		return new ArrayList<T>(Lists.transform(ret, new Function<ODocument, T>() {

			@Override
			public T apply(ODocument input) {
				return mapToEntity(input, null, session);
			}
		}));
	}
	
	protected void command(OPersistenceSession session, String sql, Object... args) {
		ODatabaseDocument db = session.getDatabase();
		db.command(new OCommandSQL(sql)).execute(args);
	}
	
	protected List<T> query(final OPersistenceSession session, org.camunda.bpm.engine.query.Query<?, ? super T> query, String... ignoreFileds) {
		return query(session, query, null, ignoreFileds);
	}
	
	protected List<T> query(final OPersistenceSession session, org.camunda.bpm.engine.query.Query<?, ? super T> query, Function<Query, Query> queryManger, String... ignoreFileds) {
		try {
			OClass schemaClass = session.getClass(getSchemaClass());
			Query q = new Query().from(getSchemaClass());
			List<Object> args = new ArrayList<>();
			enrichWhereByBean(q, schemaClass, query, args, Arrays.asList(ignoreFileds));
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
		OClass schemaClass = session.getClass(getSchemaClass());
		Query q = new Query().from(getSchemaClass());
		List<Object> args = new ArrayList<>();
		enrichWhereByMap(q, schemaClass, query, args, Arrays.asList(ignoreFileds));
		if(queryManger!=null) q = queryManger.apply(q);
		return queryList(session, q.toString(), args.toArray());
	}
	
	
	protected void delete(final OPersistenceSession session, org.camunda.bpm.engine.query.Query<?, ? super T> query, String... ignoreFileds) {
		delete(session, query, null, ignoreFileds);
	}
	
	protected void delete(final OPersistenceSession session, org.camunda.bpm.engine.query.Query<?, ? super T> query, Function<Query, Query> queryManger, String... ignoreFileds) {
		try {
			OClass schemaClass = session.getClass(getSchemaClass());
			Query q = new Query().from(getSchemaClass());
			List<Object> args = new ArrayList<>();
			enrichWhereByBean(q, schemaClass, query, args, Arrays.asList(ignoreFileds));
			if(queryManger!=null) q = queryManger.apply(q);
			command(session, q.toString(), args.toArray());
		} catch (Exception e) {
			throw new ProcessEngineException("Problems with read method of "+query.getClass().getName(), e);
		} 
	}
	
	protected void delete(final OPersistenceSession session, Map<String, ?> query, String... ignoreFileds) {
		delete(session, query, null, ignoreFileds);
	}
	
	protected void delete(final OPersistenceSession session, Map<String, ?> query, Function<Query, Query> queryManger, String... ignoreFileds) {
		OClass schemaClass = session.getClass(getSchemaClass());
		Query q = new Query().from(getSchemaClass());
		List<Object> args = new ArrayList<>();
		enrichWhereByMap(q, schemaClass, query, args, Arrays.asList(ignoreFileds));
		if(queryManger!=null) q = queryManger.apply(q);
		command(session, q.toString(), args.toArray());
	}
	
	
	private void enrichWhereByBean(AbstractQuery q, OClass schemaClass, Object query, List<Object> args, List<String> ignore) 
														throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for(PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(query.getClass())) {
			if(pd.getReadMethod()!=null 
					&& schemaClass.getProperty(pd.getName())!=null
					&& !ignore.contains(pd.getName())) {
				Object value = pd.getReadMethod().invoke(query);
				if(value!=null) {
					where(q, clause(pd.getName(), Operator.EQ, Parameter.PARAMETER));
					args.add(value);
				}
			}
		}
	}
	
	private void enrichWhereByMap(AbstractQuery q, OClass schemaClass, Map<String, ?> query, List<Object> args, List<String> ignore) {
		for(Map.Entry<String, ?> entry : query.entrySet()) {
			if(schemaClass.getProperty(entry.getKey())!=null
					&& !ignore.contains(entry.getKey())) {
				Object value = entry.getValue();
				if(value!=null) {
					where(q, clause(entry.getKey(), Operator.EQ, Parameter.PARAMETER));
					args.add(value);
				}
			}
		}
	}
	
	private AbstractQuery where(AbstractQuery q, Clause clause) {
		if(q instanceof Query)((Query)q).where(clause);
		else if(q instanceof Delete)((Delete)q).where(clause);
		return q;
	}
	
}
