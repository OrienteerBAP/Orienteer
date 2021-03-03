package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.danekja.java.util.function.serializable.SerializableFunction;
import org.orienteer.core.dao.Command;
import org.orienteer.core.dao.Function;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.Lookup;
import org.orienteer.core.dao.Query;
import org.orienteer.core.dao.Script;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.script.OCommandFunction;
import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQLResultset;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link IMethodHandler} to cover methods to {@link Query}, {@link Command}, {@link Script} or {@link Function}
 * @param <T>  type of target/delegate object
 */
public class ExecuteSQLMethodHandler<T> extends AbstractMethodHandler<T>{
	
	private final SerializableFunction<T, ? extends Object> converter;
	
	public ExecuteSQLMethodHandler() {
		this(null);
	}
	
	public ExecuteSQLMethodHandler(SerializableFunction<T, ? extends Object> converter) {
		this.converter = converter;
	}

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain) throws Throwable {
		OCommandRequest request = null;
		if(method.isAnnotationPresent(Query.class)) 
			request = new OSQLSynchQuery<ODocument>(method.getAnnotation(Query.class).value());
		else if(method.isAnnotationPresent(Command.class)) {
			if(Collection.class.isAssignableFrom(method.getReturnType()))
				request = new OCommandSQLResultset(method.getAnnotation(Command.class).value());
			else
				request = new OCommandSQL(method.getAnnotation(Command.class).value());
		}
		else if(method.isAnnotationPresent(Function.class)) 
			request = new OCommandFunction(method.getAnnotation(Function.class).value());
		else if(method.isAnnotationPresent(Script.class)) {
			Script script = method.getAnnotation(Script.class);
			request = new OCommandScript(script.language(), script.value());
		}
		
		if(request!=null) {
			Map<String, Object> argumets = toArguments(method, args);
			if(converter!=null) argumets.putIfAbsent("target", converter.apply(target));
			return Optional.ofNullable(executeRequest(request, argumets, method));
		} else return chain.handle(target, proxy, method, args);
	}

}
