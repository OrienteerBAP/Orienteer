package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.Optional;

import org.orienteer.core.dao.IMethodHandler;

/**
 * {@link IMethodHandler} to mirror invokations between interface and target/delegate just by method name and args
 * @param <T> type of target/delegate object
 */
public class MirrorMethodHandler<T> implements IMethodHandler<T>{
	
	private final Class<?> mirrorInterface;
	
	public MirrorMethodHandler(Class<?> mirrorInterface) {
		this.mirrorInterface = mirrorInterface;
	}

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
		if(method.getDeclaringClass().equals(mirrorInterface)) {
			Object ret = target.getClass().getMethod(method.getName(), method.getParameterTypes())
					.invoke(target, args);
			return Optional.ofNullable(target == ret? proxy : ret);
		} else return null;
	}

}
