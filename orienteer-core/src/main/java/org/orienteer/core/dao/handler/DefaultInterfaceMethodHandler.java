package org.orienteer.core.dao.handler;

import java.lang.annotation.Annotation;
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
		Class<?> declaringClass = method.getDeclaringClass();
		if(method.isDefault()) {
			MethodHandles.Lookup lookup = Reflect.onClass(MethodHandles.Lookup.class)
					.create(declaringClass, MethodHandles.Lookup.PRIVATE).get();
			return Optional.ofNullable(lookup.unreflectSpecial(method, declaringClass)
										  .bindTo(proxy)
										  .invokeWithArguments(args));
		} else if(proxy instanceof Annotation
						&& (args==null || args.length==0)
						&& !method.getDeclaringClass().equals(Annotation.class)) {
			Object defaultValue = method.getDefaultValue();
			if(defaultValue!=null) return Optional.of(defaultValue);
		}
		return chain.handle(target, proxy, method, args);
	}

}
