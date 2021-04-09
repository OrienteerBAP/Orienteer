package org.orienteer.core.dao.handler;

import java.lang.annotation.Annotation;
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
import org.orienteer.core.util.CommonUtils;

import com.google.inject.internal.Annotations;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.script.OCommandFunction;
import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQLResultset;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

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
		Annotation annotation = CommonUtils.getFirstPresentAnnotation(method, Query.class, 
																			  Command.class, 
																			  Function.class, 
																			  Script.class);
		
		if(annotation!=null) {
			Map<String, Object> argumets = toArguments(method, args);
			if(converter!=null) argumets.putIfAbsent("target", converter.apply(target));
			if(target instanceof ODocumentWrapper)
				argumets.put("daoClass", ((ODocumentWrapper)target).getDocument().getClassName());
			ODatabaseSession db = ODatabaseRecordThreadLocal.instance().get();
			OResultSet rs = null;
			OCommandRequest request = null;
			if(annotation instanceof Query) {
				rs = db.query(((Query)annotation).value(), argumets);
			} else if(annotation instanceof Command) {
				rs = db.command(((Query)annotation).value(), argumets);
			} else if(method.isAnnotationPresent(Function.class)) {
				request = new OCommandFunction(method.getAnnotation(Function.class).value());
			} else if(method.isAnnotationPresent(Script.class)) {
				Script script = method.getAnnotation(Script.class);
				request = new OCommandScript(script.language(), script.value());
			}
			
			if(rs != null) {
				Object ret = null;
				if(Collection.class.isAssignableFrom(method.getReturnType())) {
					ret = prepareForJava(rs, method);
				} else {
					ret = rs.hasNext()?rs.next().getElement().orElse(null):null;
					if(ret!=null) ret = prepareForJava(ret, method);
				}
				rs.close();
				return Optional.ofNullable(ret);
			}
			else if(request!=null) {
				return Optional.ofNullable(executeRequest(request, argumets, method));
			} 
		}
		return chain.handle(target, proxy, method, args);
		
	}

}
