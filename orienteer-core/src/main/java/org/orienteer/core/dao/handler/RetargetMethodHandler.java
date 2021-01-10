package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.Optional;

import org.orienteer.core.dao.IMethodHandler;

/**
 * {@link IMethodHandler} which retarget method call if target/delegate supports it
 * @param <T>  type of target/delegate object
 */
public class RetargetMethodHandler<T> implements IMethodHandler<T>{

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
		Class<?> declaringClass = method.getDeclaringClass();
		if(declaringClass.isInstance(target)) {
			return Optional.ofNullable(method.invoke(target, args));
		} else return null;
	}

}
