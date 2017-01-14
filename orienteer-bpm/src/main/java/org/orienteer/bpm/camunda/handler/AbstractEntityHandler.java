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
import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeToken;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.hook.ORecordHook.TYPE;
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
	private final String pkField;
	
	protected Map<String, String> mappingFromEntityToDoc;
	protected Map<String, String> mappingFromDocToEntity;
	/**
	 * Additional map to customize mapping especially from Query to doc queries
	 */
	protected Map<String, String> mappingFromQueryToDoc = new HashMap<>();
	/**
	 * Converters for changing value. Applied in both ways
	 */
	protected Map<String, Converter<Object, Object>> mappingConvertors = new HashMap<>();
	
	
	private Map<String, Method> statementMethodsMapping = new HashMap<>();
	
	public AbstractEntityHandler(String schemaClass) {
		this(schemaClass, "id");
	}
	
	public AbstractEntityHandler(String schemaClass, String pkField) {
		this.schemaClass = schemaClass;
		this.pkField = pkField;
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
	public String getPkField() {
		return pkField;
	}
	
	@Override
	public void create(T entity, OPersistenceSession session) {
		ODocument doc = mapToODocument(entity, null, session);
		session.getDatabase().save(doc);
		session.cacheODocument(doc);
	}
	
	@Override
	public T read(String id, OPersistenceSession session) {
		ODocument doc = readAsDocument(id, session);
		return doc==null?null:mapToEntity(doc, null, session);
	}
	
	@Override
	public ODocument readAsDocument(String id, OPersistenceSession session) {
		String oid = (String) convertValueFromEntity("id", id);
		OIdentifiable oIdentifiable = session.lookupOIdentifiableForIdInCache(oid);
		if(oIdentifiable!=null) return oIdentifiable.getRecord();
		else {
			ODatabaseDocument db = session.getDatabase();
			List<ODocument> ret = db.query(new OSQLSynchQuery<>("select from "+getSchemaClass()+" where "+getPkField()+" = ?", 1), oid);
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
		String oid = (String) convertValueFromEntity("id", id);
		OIdentifiable oIdentifiable = session.lookupOIdentifiableForIdInCache(oid);
		if(oIdentifiable!=null) {
			db.delete(oIdentifiable.getIdentity());
		} else {
			db.command(new OCommandSQL("delete from "+getSchemaClass()+" where "+getPkField()+" = ?")).execute(oid);
		}
	}
	
	protected void checkMapping(OPersistenceSession session) {
		if(mappingFromDocToEntity==null || mappingFromEntityToDoc==null){
			if(session!=null) initMapping(session);
		}
	}
	
	protected IGetAndSet getGetAndSetter(Class<?> clazz, String property) {
		return PropertyResolver.getLocator().get(clazz, property);
	}
	
	protected void initMapping(OPersistenceSession session) {
		mappingFromDocToEntity = new HashMap<>();
		mappingFromEntityToDoc = new HashMap<>();
		
		OClass oClass = session.getClass(getSchemaClass());
		Class<T> entityClass = getEntityClass();
	
		for(OProperty property : oClass.properties()) {
			String propertyName = property.getName();
			String beanPropertyName = propertyName;
			boolean isLink = property.getType().isLink();
			if(isLink) beanPropertyName+="Id";
			IGetAndSet getAndSet = getGetAndSetter(entityClass, beanPropertyName);
			if(getAndSet!=null) {
				if(isLink) {
					IEntityHandler<?> targetHandler = HandlersManager.get().getHandlerBySchemaClass(property.getLinkedClass().getName());
					if(targetHandler==null || targetHandler.getPkField()==null) continue;
					propertyName+="."+targetHandler.getPkField();
				}
				if(getAndSet.getSetter()!=null) mappingFromDocToEntity.put(propertyName, beanPropertyName);
				if(getAndSet.getGetter()!=null) mappingFromEntityToDoc.put(beanPropertyName, propertyName);
			}
		}
 	}
	
	protected Object convertValueToEntity(String entityFieldName, Object value) {
		Converter<Object, Object> converter = mappingConvertors.get(entityFieldName);
		return converter==null?value:converter.reverse().convert(value);
	}
	
	protected Object convertValueFromEntity(String entityFieldName, Object value) {
		Converter<Object, Object> converter = mappingConvertors.get(entityFieldName);
		return converter==null?value:converter.convert(value);
	}
	
	
	@Override
	public T mapToEntity(ODocument doc, T entity, OPersistenceSession session) {
		checkMapping(session);
		try {
			if(hasNeedInCache() && session!=null) {
				entity = (T)session.lookupEntityInCache((String)doc.field(getPkField()));
				if(entity!=null) return entity;
			}
			if(entity==null) {
				entity = getEntityClass().newInstance();
			}
			for(Map.Entry<String, String> mapToEntity : mappingFromDocToEntity.entrySet()) {
				Object valueToSet = doc.field(mapToEntity.getKey());
				valueToSet = convertValueToEntity(mapToEntity.getValue(), valueToSet);
				if(valueToSet!=null) PropertyResolver.setValue(mapToEntity.getValue(), entity, valueToSet, PROPERTY_RESOLVER_CONVERTEER);
				else {
					IGetAndSet getAndSet = getGetAndSetter(entity.getClass(), mapToEntity.getValue());
					if(!getAndSet.getTargetClass().isPrimitive()) getAndSet.setValue(entity, null, PROPERTY_RESOLVER_CONVERTEER);
				}
			}
			if(entity instanceof HasDbRevision) {
				((HasDbRevision)entity).setRevision(doc.getVersion());
			}
			if(session!=null) session.fireEntityLoaded(doc, entity, hasNeedInCache());
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
			String docField = mapToDoc.getValue();
			
			
			int refIndex = docField.indexOf('.');
			if(refIndex>=0 || !Objects.equal(value, doc.field(docField))) {
				if(refIndex>=0) {
					String refPkField = docField.substring(refIndex+1); 
					docField = docField.substring(0, refIndex);
					OProperty refProperty = doc.getSchemaClass().getProperty(docField);
					IEntityHandler<?> refHandler = refProperty!=null
															? HandlersManager.get().getHandlerBySchemaClass(refProperty.getLinkedClass())
															: null;
					if(refHandler==null || !Objects.equal(refPkField, refHandler.getPkField())) {
						logger.error("Mapping for entity field '"+mapToDoc.getKey()+"' is wrongly set to '"+mapToDoc.getValue()+"'");
						continue;
					}
					if(value!=null)
					{
						String referToId = value.toString();
						OIdentifiable referTo = session.lookupOIdentifiableForIdInCache(referToId);
						if(referTo==null) {
							referTo = refHandler.readAsDocument(referToId, session);
						}
						if(!Objects.equal(doc.field(docField), referTo)) doc.field(docField, referTo);
					} else
					{
						doc.field(docField, (Object) null);
					}
				} else {
					doc.field(docField, convertValueFromEntity(mapToDoc.getKey(), value));
				}
			}
		}
		return doc;
	}

	@Override
	public void applySchema(OSchemaHelper helper) {
		helper.oClass(schemaClass, BPM_ENTITY_CLASS);
	}
	
	@Override
	public void applyRelationships(OSchemaHelper helper) {
		helper.oClass(schemaClass, BPM_ENTITY_CLASS);
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
			enrichWhereByBean(session, q, schemaClass, query, args, Arrays.asList(ignoreFileds));
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
		enrichWhereByMap(session, q, schemaClass, query, args, Arrays.asList(ignoreFileds));
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
			enrichWhereByBean(session, q, schemaClass, query, args, Arrays.asList(ignoreFileds));
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
		enrichWhereByMap(session, q, schemaClass, query, args, Arrays.asList(ignoreFileds));
		if(queryManger!=null) q = queryManger.apply(q);
		command(session, q.toString(), args.toArray());
	}
	
	
	protected void enrichWhereByBean(OPersistenceSession session, AbstractQuery q, OClass schemaClass, Object query, List<Object> args, List<String> ignore) 
														throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		checkMapping(session);
		for(PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(query.getClass())) {
			if(pd.getReadMethod()!=null 
					&& ( mappingFromEntityToDoc.containsKey(pd.getName())
							|| mappingFromQueryToDoc.containsKey(pd.getName()))
					&& (ignore==null || !ignore.contains(pd.getName()))) {
				String docMapping = mappingFromEntityToDoc.get(pd.getName());
				if(docMapping==null) docMapping = mappingFromQueryToDoc.get(pd.getName());
				Object value = pd.getReadMethod().invoke(query);
				if(value!=null) {
					where(q, clause(docMapping, Operator.EQ, Parameter.PARAMETER));
					args.add(convertValueFromEntity(pd.getName(), value));
				}
			}
		}
	}
	
	protected void enrichWhereByMap(OPersistenceSession session, AbstractQuery q, OClass schemaClass, Map<String, ?> query, List<Object> args, List<String> ignore) {
		checkMapping(session);
		for(Map.Entry<String, ?> entry : query.entrySet()) {
			if((mappingFromEntityToDoc.containsKey(entry.getKey()) || mappingFromEntityToDoc.containsKey(entry.getKey()))
					&& (ignore==null || !ignore.contains(entry.getKey()))) {
				String docMapping = mappingFromEntityToDoc.get(entry.getKey());
				if(docMapping==null) docMapping = mappingFromQueryToDoc.get(entry.getKey());
				Object value = entry.getValue();
				if(value!=null) {
					where(q, clause(docMapping, Operator.EQ, Parameter.PARAMETER));
					args.add(convertValueFromEntity(entry.getKey(), value));
				}
			}
		}
	}
	
	protected AbstractQuery where(AbstractQuery q, Clause clause) {
		if(q instanceof Query)((Query)q).where(clause);
		else if(q instanceof Delete)((Delete)q).where(clause);
		return q;
	}
	
	@Override
	public RESULT onTrigger(ODatabaseDocument db, ODocument doc, TYPE iType) {
		return RESULT.RECORD_NOT_CHANGED;
	}
	
}
