package org.orienteer.core.dao.handler;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Optional;

import org.joor.Reflect;
import org.orienteer.core.dao.IMethodHandler;

/**
 * {@link IMethodHandler} which invoke interfaces default method implementations
 * @param <T> type of target/delegate object
 */
public class DefaultInterfaceMethodHandler<T> implements IMethodHandler<T> {

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain) throws Throwable {
		if(method.isDefault()) {
		Class<?> declaringClass = method.getDeclaringClass();
		MethodHandles.Lookup lookup = Reflect.onClass(MethodHandles.Lookup.class)
				.create(declaringClass, MethodHandles.Lookup.PRIVATE).get();
		return Optional.ofNullable(lookup.unreflectSpecial(method, declaringClass)
									  .bindTo(proxy)
									  .invokeWithArguments(args));
		} else return chain.handle(target, proxy, method, args);
	}

}
