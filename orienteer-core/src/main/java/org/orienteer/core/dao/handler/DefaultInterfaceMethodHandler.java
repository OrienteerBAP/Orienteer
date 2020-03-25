package org.orienteer.core.dao.handler;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.joor.Reflect;
import org.orienteer.core.dao.IMethodHandler;

/**
 * {@link IMethodHandler} which invoke interfaces default method implementations
 * @param <T> type of target/delegate object
 */
public class DefaultInterfaceMethodHandler<T> implements IMethodHandler<T> {

	@Override
	public ResultHolder handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
		if(method.isDefault()) {
		Class<?> declaringClass = method.getDeclaringClass();
		MethodHandles.Lookup lookup = Reflect.onClass(MethodHandles.Lookup.class)
				.create(declaringClass, MethodHandles.Lookup.PRIVATE).get();
		return new ResultHolder(lookup.unreflectSpecial(method, declaringClass)
					  .bindTo(proxy)
					  .invokeWithArguments(args));
		} else return null;
	}

}
