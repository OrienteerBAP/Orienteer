package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.wicket.WicketRuntimeException;
import org.orienteer.core.dao.DAOHandler;
import org.orienteer.core.dao.IMethodHandler;

/**
 * {@link IMethodHandler} to check and schedule interceptors if they are defined
 * @param <T> type of target/delegate object
 */
public class DAOHandlersMethodHandler<T> implements IMethodHandler<T>{
	
	private final static Map<Class<? extends IMethodHandler<?>>, IMethodHandler<?>> CACHE = new HashMap<>();

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain)
			throws Throwable {
		prepend(method.getDeclaringClass().getAnnotationsByType(DAOHandler.class), chain);
		prepend(method.getAnnotationsByType(DAOHandler.class), chain);
		return chain.handle(target, proxy, method, args);
	}
	
	protected void prepend(DAOHandler[] handlers, InvocationChain<T> chain) {
		if(handlers==null || handlers.length==0) return;
		IMethodHandler<T>[] additionalHandlers = new IMethodHandler[handlers.length];
		for(int i=0; i<handlers.length; i++) {
			additionalHandlers[i] = getHandlerInstance((Class<? extends IMethodHandler<?>>)handlers[i].value());
		}
		chain.prepend(additionalHandlers);
	}
	
	protected IMethodHandler<T> getHandlerInstance(Class<? extends IMethodHandler<?>> handlerClass) {
		IMethodHandler<T> ret = (IMethodHandler<T>)CACHE.get(handlerClass);
		if(ret==null) {
			try {
				ret = (IMethodHandler<T>) handlerClass.newInstance();
				CACHE.put(handlerClass, ret);
			} catch (Exception e) {
				throw new WicketRuntimeException("Unable to create instance of " + handlerClass, e);
			}
		}
		return ret;
	}

}
