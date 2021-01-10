package org.orienteer.core.dao;

import java.lang.reflect.Method;
import java.util.Optional;

import org.orienteer.core.dao.handler.InvocationChain;

public class TestDAOMethodHandler<T> implements IMethodHandler<T>{
	
	public static final Object RETURN = Integer.valueOf(9999);

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain)
			throws Throwable {
		return Optional.of(RETURN);
	}

}
