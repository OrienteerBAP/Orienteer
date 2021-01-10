package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.danekja.java.util.function.serializable.SerializableFunction;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.Lookup;
import org.orienteer.core.dao.Query;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link IMethodHandler} to cover methods to query DB
 * @param <T>  type of target/delegate object
 */
public class QueryMethodHandler<T> extends AbstractMethodHandler<T>{
	
	private final SerializableFunction<T, ? extends Object> converter;
	
	public QueryMethodHandler() {
		this(null);
	}
	
	public QueryMethodHandler(SerializableFunction<T, ? extends Object> converter) {
		this.converter = converter;
	}

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain) throws Throwable {
		if(method.isAnnotationPresent(Query.class)) {
			String sql = method.getAnnotation(Query.class).value();
			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
			Map<String, Object> argumets = toArguments(method, args);
			if(converter!=null) argumets.putIfAbsent("target", converter.apply(target));
			return Optional.ofNullable(queryDB(query, argumets, method));
		} else return chain.handle(target, proxy, method, args);
	}

}
