package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import org.orienteer.core.dao.IMethodHandler;

/**
 * Utility abstract class of {@link IMethodHandler}s which work with DB
 * @param <T> type of target/delegate object
 */
public abstract class AbstractSQLMethodHandler<T> implements IMethodHandler<T>{
	
	protected Map<String, Object> toArguments(Method method, Object[] values) {
		return toArguments(null, true, method, values);
	}
	
	protected Map<String, Object> toArguments(Map<String, Object> args, boolean override, Method method, Object[] values) {
		if(args==null) {
			args = new HashMap<>();
			override = true;
		}
		
		Parameter[] params = method.getParameters();
		for (int i = 0; i < params.length; i++) {
			if(override) {
				args.put(params[i].getName(), values[i]);
				args.put("arg"+i, values[i]);
			}
			else {
				args.putIfAbsent(params[i].getName(), values[i]);
				args.putIfAbsent("arg"+i, values[i]);
			}
		}
		return args;
	}

//	protected List<ODocument> run(String sql);
}
