package org.orienteer.core.dao;

import java.lang.reflect.Method;

/**
 * Interface to allow to stack method invocation handlers
 * @param <T> - type of target/delegate object
 */
public interface IMethodHandler<T> {
	
	ResultHolder NULL_RESULT = new ResultHolder(null);
	
	/**
	 * Holder for results out of {@link IMethodHandler}.
	 * It's needed to cover results "null" which also should be recognized as results
	 */
	class ResultHolder {
		public final Object result;
		
		public ResultHolder(Object result) {
			this.result = result;
		}
	}
	
	/**
	 * Handle the method invocation on proxy 
	 * @param target target object
	 * @param proxy proxy object
	 * @param method method to be invoked
	 * @param args arguments
	 * @return null if invocation was not handled and not-null otherwise
	 */
	ResultHolder handle(T target, Object proxy, Method method, Object[] args) throws Throwable;
}
