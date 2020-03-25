package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.IODocumentWrapper;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Abstract class of {@link IMethodHandler}s with static utility methods
 * @param <T> type of target/delegate object
 */
public abstract class AbstractMethodHandler<T> implements IMethodHandler<T>{
	
	protected static ResultHolder returnChained(Object proxy, Method method, boolean present) {
		if(Boolean.class.equals(method.getReturnType())) return new ResultHolder(Boolean.valueOf(present));
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
		else if (arg instanceof IODocumentWrapper) return prepareForDB(((IODocumentWrapper)arg).getDocument());
		else if (arg instanceof ODocumentWrapper) return prepareForDB(((ODocumentWrapper)arg).getDocument());
		else if (arg instanceof Collection<?>) {
			Collection<?> col = (Collection<?>)arg;
			List<Object> ret = new ArrayList<>(col.size());
			for (Object object : col) ret.add(prepareForDB(object));
			return ret;
		} else if (arg.getClass().isArray()) {
			Object[] array = (Object[])arg;
			List<Object> ret = new ArrayList<>(array.length);
			for (Object object : array) ret.add(prepareForDB(object));
			return ret;
		} else throw new IllegalStateException("Type "+arg.getClass()+" can't be cast to use in DB");
	}
	
	protected static OIdentifiable prepareForDB(ODocument doc) {
		ORID orid = doc.getIdentity();
		return orid.isPersistent()?orid:doc;
	}

}
