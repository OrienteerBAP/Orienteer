package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joor.Reflect;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.IODocumentWrapper;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Abstract class of {@link IMethodHandler}s with static utility methods
 * @param <T> type of target/delegate object
 */
public abstract class AbstractMethodHandler<T> implements IMethodHandler<T>{
	
	protected static ResultHolder returnChained(Object proxy, Method method, boolean present) {
		if(Boolean.class.equals(method.getReturnType())
			|| boolean.class.equals(method.getReturnType())) 
			return new ResultHolder(Boolean.valueOf(present));
		else return present?returnChained(proxy, method) : NULL_RESULT;
	}
	
	protected static ResultHolder returnChained(Object proxy, Method method) {
		return method.getDeclaringClass().isInstance(proxy) ? new ResultHolder(proxy) : NULL_RESULT;
	}
	
	protected static Map<String, Object> toArguments(Method method, Object[] values) {
		return toArguments(null, true, method, values);
	}
	
	protected static Map<String, Object> toArguments(Map<String, Object> args, boolean override, Method method, Object[] values) {
		if(args==null) {
			args = new HashMap<>();
			override = true;
		}
		
		Parameter[] params = method.getParameters();
		for (int i = 0; i < params.length; i++) {
			Object value = prepareForDB(values[i]);
			if(override) {
				args.put(params[i].getName(), value);
				args.put("arg"+i, value);
			}
			else {
				args.putIfAbsent(params[i].getName(), value);
				args.putIfAbsent("arg"+i, value);
			}
		}
		return args;
	}
	
	protected static Object prepareForDB(Object arg) {
		if(arg==null) return null;
		if(OType.isSimpleType(arg)) return arg;
		else if (arg instanceof OIdentifiable) return prepareForDB((OIdentifiable)arg);
		else if (arg instanceof IODocumentWrapper) return prepareForDB(((IODocumentWrapper)arg).getDocument());
		else if (arg instanceof ODocumentWrapper) return prepareForDB(((ODocumentWrapper)arg).getDocument());
		else if (arg instanceof Collection<?>) {
			Collection<?> col = (Collection<?>)arg;
			List<Object> ret = new ArrayList<>(col.size());
			for (Object object : col) ret.add(prepareForDB(object));
			return ret;
		} else if (arg instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)arg;
			Map<Object, Object> ret = new HashMap<Object, Object>(map.size());
			for (Map.Entry<?, ?> entry : map.entrySet()) ret.put(entry.getKey(), prepareForDB(entry.getValue()));
			return ret;
		} else if (arg.getClass().isArray()) {
			Object[] array = (Object[])arg;
			List<Object> ret = new ArrayList<>(array.length);
			for (Object object : array) ret.add(prepareForDB(object));
			return ret;
		} else throw new IllegalStateException("Type "+arg.getClass()+" can't be cast to use in DB");
	}
	
	protected static OIdentifiable prepareForDB(OIdentifiable doc) {
		ORID orid = doc.getIdentity();
		return orid.isPersistent()?orid:doc;
	}
	
	protected static Object queryDB(OQuery<ODocument> query, Map<String, Object> args, Method method) {
		if(Collection.class.isAssignableFrom(method.getReturnType())) 
			return prepareForJava(query.run(args), method.getReturnType(), method.getGenericReturnType());
		else return prepareForJava(query.runFirst(args), method.getReturnType());
	}
	
	public static Class<?> typeToRequiredClass(Type type, Class<?> parentClass) {
		return typeToRequiredClass(type, parentClass==null?false:Map.class.isAssignableFrom(parentClass));
	}
	
	private static Class<?> typeToRequiredClass(Type type, boolean isParentMap) {
		if(type instanceof Class) return (Class<?>) type;
		else if(type instanceof WildcardType) 
			return typeToRequiredClass(((WildcardType)type).getUpperBounds()[0], false);
		else if(type instanceof ParameterizedType)
			return typeToRequiredClass(((ParameterizedType)type).getActualTypeArguments()[isParentMap?1:0], false);
		return null;
	}
	
	protected static Object prepareForJava(Object result, Method method) {
		if(result==null) return null;
		Class<?> requiredClass = method.getReturnType();
		Type genericType = method.getGenericReturnType();
		Class<?> requiredSubType = typeToRequiredClass(genericType, requiredClass);
		if(result instanceof Collection) {
			Iterator<?> it = ((Collection<?>)result).iterator(); 
			if(!it.hasNext()) return onRealClass(requiredClass).create().get();
			Object probe;
			do {
				probe = it.next();
			} while(it.hasNext() && probe == null);
			if(probe instanceof ODocument) {
				List<ODocument> list;
				if(result instanceof List) list = (List<ODocument>)result;
				else {
					list = new ArrayList<>();
					list.addAll(((Collection<ODocument>)result));
				}
				return prepareForJava(list, requiredClass, genericType);
			} else if(Collection.class.isAssignableFrom(requiredClass)) {
				Reflect collection = onRealClass(requiredClass).create();
				collection.call("addAll", result);
				return collection.get();
			}
			else throw new IllegalStateException("Can't prepare required return class: "+requiredClass +" from "+result.getClass());
		} else if(result instanceof Map) {
			
			Map<?, ?> map = (Map<?, ?>)result;
			if(map.size()==0) return result;
			Iterator<?> it = map.values().iterator();
			Object probe;
			do {
				probe = it.next();
			} while(it.hasNext() && probe == null);
			if(probe instanceof ODocument) {
				return prepareForJava((Map<?, ODocument>)map, requiredClass, genericType);
			} else if(Map.class.isAssignableFrom(requiredClass)) {
				return map;
			}
			else throw new IllegalStateException("Can't prepare required return class: "+requiredClass +" from "+result.getClass());
		} else if(requiredClass.isInstance(result)) return result;
		else if(result instanceof OIdentifiable) return prepareForJava(((OIdentifiable)result).getRecord(), requiredClass);
		else throw new IllegalStateException("Can't prepare required return class: "+requiredClass +" from "+result.getClass());
	}
	
	protected static Object prepareForJava(List<ODocument> resultSet, Class<?> requiredClass, Type genericType) {
		if(resultSet==null) return null;
		Class<?> requiredSubType = typeToRequiredClass(genericType, requiredClass);
		
		List<?> ret;
		if(requiredSubType.isAssignableFrom(ODocument.class)) {
			ret = resultSet;
		}
		else {
			List<Object> inner = new ArrayList<>();
			for (ODocument oDocument : resultSet) {
				inner.add(prepareForJava(oDocument, requiredSubType));
			}
			ret = inner;
		}
		
		if(requiredClass.isAssignableFrom(List.class)) return ret;
		else if(Collection.class.isAssignableFrom(requiredClass)) {
			Reflect instance = onRealClass(requiredClass).create();
			instance.call("addAll", ret);
			return instance.get();
		}
		else throw new IllegalStateException("Can't prepare required return class: "+requiredClass);
	}
	
	protected static Object prepareForJava(Map<?, ODocument> map, Class<?> requiredClass, Type genericType) {
		if(map==null) return null;
		Class<?> requiredSubType = typeToRequiredClass(genericType, requiredClass);
		
		Map<?, ?> ret;
		if(requiredSubType.isAssignableFrom(ODocument.class)) {
			ret = map;
		}
		else {
			Map<Object, Object> inner = new HashMap<>();
			for (Map.Entry<?, ODocument> entry : map.entrySet()) {
				inner.put(entry.getKey(), prepareForJava(entry.getValue(), requiredSubType));
			}
			ret = inner;
		}
		
		if(requiredClass.isAssignableFrom(Map.class)) return ret;
		else throw new IllegalStateException("Can't prepare required return class: "+requiredClass);
	}
	
	protected static Object prepareForJava(ODocument result, Class<?> requiredClass) {
		if(result==null) return null;
		else if(requiredClass.isInstance(result)) return result;
		else if(ODocumentWrapper.class.isAssignableFrom(requiredClass)) 
			return Reflect.onClass(requiredClass).create(result).get();
		else if(requiredClass.isInterface())
			return DAO.provide(requiredClass, result);
		else if(result.containsField("value")) {
			Object value = result.field("value");
			if(value instanceof ODocument) return prepareForJava((ODocument)value, requiredClass);
			return result.field("value", requiredClass);
		}
		throw new IllegalStateException("Can't case ODocument to "+requiredClass); 
	}
	
	protected static Reflect onRealClass(Class<?> clazz) {
		if(!clazz.isInterface()) return Reflect.onClass(clazz);
		else if(clazz.isAssignableFrom(ArrayList.class)) return Reflect.onClass(ArrayList.class);
		else if(clazz.isAssignableFrom(HashSet.class)) return Reflect.onClass(HashSet.class);
		else if(clazz.isAssignableFrom(HashMap.class)) return Reflect.onClass(HashMap.class);
		return Reflect.onClass(clazz); //Will fail in case of creation of new instance
	}

}
