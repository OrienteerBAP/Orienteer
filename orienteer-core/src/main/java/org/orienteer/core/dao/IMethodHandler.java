package org.orienteer.core.dao;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Interface to allow to stack method invocation handlers
 * @param <T> - type of target/delegate object
 */
public interface IMethodHandler<T> {
	
	/**
	 * Handle the method invocation on proxy 
	 * @param target target object
	 * @param proxy proxy object
	 * @param method method to be invoked
	 * @param args arguments
	 * @return null if invocation was not handled and not-null otherwise
	 */
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args) throws Throwable;
}
